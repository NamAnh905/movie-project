package com.example.movie.controller.payment;

import com.example.movie.dto.request.payment.VnpayCreateRequest;
import com.example.movie.dto.response.payment.VnpayCreateResponse;
import com.example.movie.model.user.User;
import com.example.movie.repository.user.UserRepository;
import com.example.movie.service.payment.VnpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final VnpayService vnpayService;
    private final UserRepository userRepo;

    // FE gọi để lấy paymentUrl (đăng nhập hay không đều được)
    @PostMapping("/vnpay/create")
    public ResponseEntity<VnpayCreateResponse> create(@RequestBody VnpayCreateRequest req,
                                                      Authentication auth,
                                                      HttpServletRequest http) {
        Long userId = null;
        if (auth != null && auth.isAuthenticated()) {
            userId = userRepo.findByUsername(auth.getName()).map(User::getId).orElse(null);
        }
        return ResponseEntity.ok(vnpayService.createPayment(req, userId, http));
    }

    // VNPAY redirect về đây (GET). Trả về HTML auto-redirect về FE.
    @GetMapping(value = "/vnpay-return", produces = MediaType.TEXT_HTML_VALUE)
    public String vnpayReturn(@RequestParam Map<String, String> allParams) {
        return vnpayService.handleReturn(allParams);
    }
}
