package com.example.movie.service.payment;

import com.example.movie.config.pay.VnpayConfig;
import com.example.movie.dto.request.payment.VnpayCreateRequest;
import com.example.movie.dto.response.payment.VnpayCreateResponse;
import com.example.movie.model.booking.Booking;
import com.example.movie.model.cinema.Showtime;
import com.example.movie.model.user.User;
import com.example.movie.repository.booking.BookingRepository;
import com.example.movie.repository.cinema.ShowtimeRepository;
import com.example.movie.repository.user.UserRepository;
import com.example.movie.util.VnpayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VnpayService {

    private final VnpayConfig cfg;
    private final BookingRepository bookingRepo;
    private final ShowtimeRepository showtimeRepo;
    private final UserRepository userRepo;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** Tạo booking PENDING và trả về payment URL */
    @Transactional
    public VnpayCreateResponse createPayment(VnpayCreateRequest req, Long userId, HttpServletRequest servletReq) {
        if (req.showtimeId == null || req.quantity == null || req.quantity < 1 || req.quantity > 10)
            throw new IllegalArgumentException("showtimeId/quantity không hợp lệ");

        Showtime s = showtimeRepo.findById(req.showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Suất chiếu không tồn tại"));
        BigDecimal unit = s.getPrice() != null ? s.getPrice() : BigDecimal.ZERO;
        int qty = req.quantity;
        // Giảm giá demo: giống FE
        BigDecimal discount = BigDecimal.ZERO;
        if (req.coupon != null) {
            String code = req.coupon.trim().toUpperCase();
            if (code.equals("SALE10")) discount = unit.multiply(BigDecimal.valueOf(qty)).multiply(BigDecimal.valueOf(0.10));
            else if (code.equals("SALE50K")) discount = BigDecimal.valueOf(50000);
        }
        BigDecimal subtotal = unit.multiply(BigDecimal.valueOf(qty));
        if (discount.compareTo(subtotal) > 0) discount = subtotal;
        BigDecimal total = subtotal.subtract(discount);

        // 1) Tạo booking PENDING (chưa trừ ghế)
        Booking b = new Booking();
        if (userId != null) {
            User u = userRepo.findById(userId).orElse(null);
            if (u != null) {
                b.setUser(u);
                b.setCustomerName(u.getFullName()!=null && !u.getFullName().isBlank() ? u.getFullName() : u.getUsername());
                b.setCustomerEmail(u.getEmail());
            }
        } else {
            b.setCustomerName(req.customerName);
            b.setCustomerEmail(req.customerEmail);
        }
        b.setShowtime(s);
        b.setQuantity(qty);
        b.setUnitPrice(unit);
        b.setTotalPrice(total);
        b.setStatus("PENDING");
        b.setPaymentMethod("VNPAY");
        bookingRepo.save(b);

        // 2) Xây tham số VNPAY
        String txnRef = "BK" + b.getId() + "-" + System.currentTimeMillis();
        b.setPaymentTxnId(txnRef); // lưu để đối soát
        // bookingRepo.save(b); // không bắt buộc trong @Transactional

        Map<String,String> params = new LinkedHashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", cfg.getTmnCode());
        params.put("vnp_Amount", String.valueOf(total.multiply(BigDecimal.valueOf(100)).longValue())); // x100
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan don #" + b.getId());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_IpAddr", getIp(servletReq));
        params.put("vnp_CreateDate", FMT.format(LocalDateTime.now()));
        params.put("vnp_ExpireDate", FMT.format(LocalDateTime.now().plusMinutes(cfg.getExpireMinutes())));
        // Return về BE kèm bookingId & clientReturnUrl
        String rurl = cfg.getReturnUrl() + "?bookingId=" + b.getId();
        if (req.clientReturnUrl != null && !req.clientReturnUrl.isBlank()) {
            rurl += "&clientUrl=" + VnpayUtil.urlEncode(req.clientReturnUrl);
        }
        params.put("vnp_ReturnUrl", rurl);

        // 3) Ký và build URL
        StringBuilder query = new StringBuilder();
        String secureHash = VnpayUtil.buildQueryAndHash(params, cfg.getHashSecret(), query);
        query.append("&vnp_SecureHash=").append(secureHash);
        String payUrl = cfg.getPayUrl() + "?" + query;

        return new VnpayCreateResponse(b.getId(), payUrl);
    }

    /** Xử lý kết quả trả về từ VNPAY (returnUrl) */
    @Transactional
    public String handleReturn(Map<String, String> allParams) {
        String receivedHash = allParams.get("vnp_SecureHash");

        // ✅ CHỈ lấy tham số vnp_* để tính hash
        Map<String,String> vnpParams = new java.util.HashMap<>();
        for (var e : allParams.entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            if (k != null && k.startsWith("vnp_") && !"vnp_SecureHash".equals(k) && v != null && !v.isBlank()) {
                vnpParams.put(k, v);
            }
        }

        StringBuilder ignore = new StringBuilder();
        String calcHash = VnpayUtil.buildQueryAndHash(vnpParams, cfg.getHashSecret(), ignore);

        Long bookingId = parseLong(allParams.get("bookingId"));           // tham số tự thêm
        String clientUrl = decodeSafe(allParams.get("clientUrl"));        // tham số tự thêm
        String respCode = allParams.get("vnp_ResponseCode");
        String transStatus = allParams.get("vnp_TransactionStatus");
        String transNo = allParams.get("vnp_TransactionNo");

        if (!calcHash.equalsIgnoreCase(receivedHash) || bookingId == null) {
            return redirect(clientUrl, bookingId, "invalid");
        }

        Booking b = bookingRepo.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking không tồn tại"));
        if (transNo != null) b.setPaymentTxnId(transNo);

        if ("00".equals(respCode) && "00".equals(transStatus)) {
            // success -> khóa suất & trừ ghế
            Showtime s = showtimeRepo.findByIdForUpdate(b.getShowtime().getId()).orElseThrow();
            int cap = s.getCapacity() != null ? s.getCapacity() : 50;
            int sold = s.getSoldSeats() != null ? s.getSoldSeats() : 0;
            if (cap - sold >= b.getQuantity()) {
                s.setSoldSeats(sold + b.getQuantity());
                b.setStatus("PAID");
                b.setPaidAt(java.time.Instant.now());
            } else {
                b.setStatus("FAILED");
            }
        } else {
            b.setStatus("FAILED");
        }
        return redirect(clientUrl, bookingId, b.getStatus());
    }


    private String redirect(String clientUrl, Long bookingId, String status) {
        // BE trả HTML nhỏ để chuyển hướng về FE (nếu clientUrl thiếu, trả JSON text/plain)
        String target = (clientUrl != null && !clientUrl.isBlank())
                ? clientUrl + (clientUrl.contains("?") ? "&" : "?") + "id=" + bookingId + "&status=" + status
                : null;
        if (target == null) {
            return "<html><body>Payment status: " + status + " (bookingId=" + bookingId + ")</body></html>";
        }
        return "<html><head><meta http-equiv=\"refresh\" content=\"0;url=" + target + "\"/></head>"
                + "<body>Redirecting...</body></html>";
    }

    private static String getIp(HttpServletRequest req) {  // <-- jakarta
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = req.getRemoteAddr();
        return ip;
    }
    private static Long parseLong(String s){ try { return s==null?null:Long.valueOf(s); } catch(Exception e){ return null; } }
    private static String decodeSafe(String s){ try { return s==null?null:java.net.URLDecoder.decode(s, java.nio.charset.StandardCharsets.UTF_8); } catch(Exception e){ return s; } }
}
