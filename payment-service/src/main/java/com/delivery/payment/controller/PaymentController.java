package com.delivery.payment.controller;

import com.delivery.common.ApiResponse;
import com.delivery.payment.dto.CreatePaymentRequest;
import com.delivery.payment.dto.PaymentDetailResponse;
import com.delivery.payment.dto.PaymentSummaryResponse;
import com.delivery.payment.dto.TossPaymentCheckoutResponse;
import com.delivery.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/orders/{orderId}/checkout")
    public ApiResponse<TossPaymentCheckoutResponse> getCheckoutInfo(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long orderId
    ) {
        return paymentService.getCheckoutInfo(userId, role, orderId);
    }

    @PostMapping
    public ApiResponse<PaymentDetailResponse> createPayment(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        return paymentService.createPayment(userId, email, role, request);
    }

    @GetMapping("/my")
    public ApiResponse<List<PaymentSummaryResponse>> getMyPayments(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return paymentService.getMyPayments(userId);
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<PaymentDetailResponse> getMyPaymentDetail(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long paymentId
    ) {
        return paymentService.getMyPaymentDetail(userId, paymentId);
    }
}
