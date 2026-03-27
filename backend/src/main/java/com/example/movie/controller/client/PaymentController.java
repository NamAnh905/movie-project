package com.example.movie.controller.client;

import com.example.movie.dto.request.client.VnpayPaymentRequest;
import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.dto.response.shared.VnpayPaymentResponse;
import com.example.movie.service.payment.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments") // Đã cấu hình /api global trong yml
@RequiredArgsConstructor
public class PaymentController {

    private final VnpayService vnpayService;

    @PostMapping("/vnpay/create")
    public ApiResponse<VnpayPaymentResponse> create(
            @Valid @RequestBody VnpayPaymentRequest req,
            HttpServletRequest http) {
        // Mọi logic kiểm tra User đăng nhập được đẩy xuống Service
        return ApiResponse.success(vnpayService.createPayment(req, http));
    }

    @GetMapping(value = "/vnpay-return", produces = MediaType.TEXT_HTML_VALUE)
    public String vnpayReturn(@RequestParam Map<String, String> allParams) {
        return vnpayService.handleReturn(allParams);
    }
}