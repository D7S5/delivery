package com.delivery.payment.service;

import com.delivery.common.ApiResponse;
import com.delivery.payment.client.OrderClient;
import com.delivery.payment.dto.CreatePaymentRequest;
import com.delivery.payment.dto.OrderInternalResponse;
import com.delivery.payment.dto.PaymentDetailResponse;
import com.delivery.payment.dto.PaymentSummaryResponse;
import com.delivery.payment.entity.Payment;
import com.delivery.payment.entity.PaymentStatus;
import com.delivery.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

    @Transactional
    public ApiResponse<PaymentDetailResponse> createPayment(Long userId, String email, String role, CreatePaymentRequest request) {
        validateCustomer(role);

        OrderInternalResponse order = orderClient.getOrder(request.orderId());

        if (!order.customerId().equals(userId)) {
            throw new IllegalArgumentException("본인 주문만 결제할 수 있습니다.");
        }

        if (!"CREATED".equals(order.status())) {
            throw new IllegalArgumentException("결제 가능한 주문 상태가 아닙니다.");
        }

        Payment payment = Payment.builder()
                .orderId(order.orderId())
                .customerId(order.customerId())
                .customerEmail(order.customerEmail())
                .amount(order.totalAmount())
                .paymentMethod(request.paymentMethod())
                .status(PaymentStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        orderClient.markOrderPaid(order.orderId());

        return new ApiResponse<>(true, PaymentDetailResponse.from(savedPayment), "결제가 완료되었습니다.");
    }

    public ApiResponse<List<PaymentSummaryResponse>> getMyPayments(Long userId) {
        List<PaymentSummaryResponse> payments = paymentRepository.findByCustomerIdOrderByIdDesc(userId)
                .stream()
                .map(PaymentSummaryResponse::from)
                .toList();

        return new ApiResponse<>(true, payments, "내 결제 목록 조회 성공");
    }

    public ApiResponse<PaymentDetailResponse> getMyPaymentDetail(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        if (!payment.getCustomerId().equals(userId)) {
            throw new IllegalArgumentException("본인 결제만 조회할 수 있습니다.");
        }

        return new ApiResponse<>(true, PaymentDetailResponse.from(payment), "결제 상세 조회 성공");
    }

    private void validateCustomer(String role) {
        if (!"CUSTOMER".equals(role)) {
            throw new IllegalArgumentException("고객 권한만 결제할 수 있습니다.");
        }
    }
}