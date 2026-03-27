package com.example.movie.service.payment;

import com.example.movie.config.VnpayConfig;
import com.example.movie.dto.request.client.VnpayPaymentRequest;
import com.example.movie.dto.response.shared.VnpayPaymentResponse;
import com.example.movie.entity.Booking;
import com.example.movie.entity.Showtime;
import com.example.movie.entity.User;
import com.example.movie.exception.AppException;
import com.example.movie.exception.ErrorCode;
import com.example.movie.repository.BookingRepository;
import com.example.movie.repository.ShowtimeRepository;
import com.example.movie.repository.UserRepository;
import com.example.movie.util.SecurityUtils;
import com.example.movie.util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SecurityUtils securityUtils;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Transactional
    public VnpayPaymentResponse createPayment(VnpayPaymentRequest req, HttpServletRequest http) {
        Showtime st = showtimeRepo.findById(req.showtimeId())
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        // Lấy thông tin User từ Token (Hỗ trợ cả Guest)
        Optional<String> currentUserOpt = securityUtils.getCurrentUserLoginOptional();
        User user = currentUserOpt.flatMap(userRepo::findByUsername).orElse(null);

        // Tạo Booking trạng thái PENDING
        BigDecimal unitPrice = st.getPrice() != null ? st.getPrice() : BigDecimal.ZERO;
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(req.quantity()));

        Booking booking = Booking.builder()
                .showtime(st)
                .user(user)
                .customerName(user != null ? (user.getFullName() != null ? user.getFullName() : user.getUsername()) : req.customerName())
                .customerEmail(user != null ? user.getEmail() : req.customerEmail())
                .quantity(req.quantity())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .status("PENDING")
                .clientReturnUrl(req.clientReturnUrl())
                .build();

        booking = bookingRepo.save(booking);

        // Build VNPAY URL
        String vnp_TxnRef = String.valueOf(booking.getId());
        String vnp_IpAddr = getIp(http);
        long amount = totalPrice.longValue() * 100L;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", cfg.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan ve xem phim ma " + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", cfg.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        LocalDateTime now = LocalDateTime.now();
        vnp_Params.put("vnp_CreateDate", FMT.format(now));
        vnp_Params.put("vnp_ExpireDate", FMT.format(now.plusMinutes(15)));

        StringBuilder query = new StringBuilder();
        String hashData = VnpayUtil.buildQueryAndHash(vnp_Params, cfg.getHashSecret(), query);
        String paymentUrl = cfg.getPayUrl() + "?" + query.toString() + "&vnp_SecureHash=" + hashData;

        return new VnpayPaymentResponse(booking.getId(), paymentUrl);
    }

    @Transactional
    public String handleReturn(Map<String, String> params) {
        String secureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        StringBuilder query = new StringBuilder();
        String computedHash = VnpayUtil.buildQueryAndHash(params, cfg.getHashSecret(), query);

        Long bookingId = parseLong(params.get("vnp_TxnRef"));
        if (bookingId == null) return "Invalid transaction reference";

        Booking b = bookingRepo.findById(bookingId).orElse(null);
        if (b == null) return "Booking not found";

        String clientUrl = b.getClientReturnUrl();

        if (computedHash.equals(secureHash)) {
            if ("00".equals(params.get("vnp_ResponseCode"))) {
                b.setStatus("PAID");
                b.setPaidAt(java.time.Instant.now());

                // Cập nhật lại số ghế đã bán của suất chiếu
                Showtime st = b.getShowtime();
                st.setSoldSeats((st.getSoldSeats() != null ? st.getSoldSeats() : 0) + b.getQuantity());
                showtimeRepo.save(st);
            } else {
                b.setStatus("FAILED");
            }
        } else {
            b.setStatus("FAILED");
        }
        bookingRepo.save(b);
        return redirect(clientUrl, bookingId, b.getStatus());
    }

    private String redirect(String clientUrl, Long bookingId, String status) {
        String target = (clientUrl != null && !clientUrl.isBlank())
                ? clientUrl + (clientUrl.contains("?") ? "&" : "?") + "id=" + bookingId + "&status=" + status
                : null;
        if (target == null) {
            return "<html><body>Payment status: " + status + " (bookingId=" + bookingId + ")</body></html>";
        }
        return "<html><head><meta http-equiv=\"refresh\" content=\"0;url=" + target + "\"/></head><body>Redirecting...</body></html>";
    }

    private static String getIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = req.getRemoteAddr();
        return ip;
    }

    private static Long parseLong(String s){ try { return s==null?null:Long.valueOf(s); } catch(Exception e){ return null; } }
}