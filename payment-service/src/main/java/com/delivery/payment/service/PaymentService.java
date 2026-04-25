package com.delivery.payment.service;

import com.delivery.common.ApiResponse;
import com.delivery.payment.client.OrderClient;
import com.delivery.payment.dto.*;
import com.delivery.payment.entity.OutboxEvent;
import com.delivery.payment.entity.Payment;
import com.delivery.payment.entity.PaymentMethod;
import com.delivery.payment.entity.PaymentStatus;
import com.delivery.payment.gateway.PaymentApprovalCommand;
import com.delivery.payment.gateway.PaymentGateway;
import com.delivery.payment.gateway.PaymentGatewayResult;
import com.delivery.payment.gateway.TossPaymentsProperties;
import com.delivery.payment.repository.OutboxRepository;
import com.delivery.payment.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final OrderClient orderClient;
    private final ObjectMapper objectMapper;
    private final PaymentGateway paymentGateway;
    private final TossPaymentsProperties tossPaymentsProperties;

    public ApiResponse<TossPaymentCheckoutResponse> getCheckoutInfo(Long userId, String role, Long orderId) {
        validateCustomer(role);

        OrderInternalResponse order = orderClient.getOrder(orderId);

        if (!order.customerId().equals(userId)) {
            throw new IllegalArgumentException("본인 주문만 결제할 수 있습니다.");
        }

        if (!"CREATED".equals(order.status())) {
            throw new IllegalArgumentException("결제 가능한 주문 상태가 아닙니다.");
        }

        if (paymentRepository.existsByOrderIdAndStatus(orderId, PaymentStatus.COMPLETED)) {
            throw new IllegalArgumentException("이미 결제된 주문입니다.");
        }

        validateCheckoutConfiguration();

        TossPaymentCheckoutResponse response = new TossPaymentCheckoutResponse(
                order.orderId(),
                createTossOrderId(order.orderId()),
                createOrderName(order),
                order.totalAmount(),
                tossPaymentsProperties.clientKey(),
                tossPaymentsProperties.successUrl(),
                tossPaymentsProperties.failUrl()
        );

        return new ApiResponse<>(true, response, "토스 결제 정보 조회 성공");
    }

    @Transactional
    public ApiResponse<PaymentDetailResponse> createPayment(Long userId, String email, String role, CreatePaymentRequest request) {
        validateCustomer(role);

        if (paymentRepository.existsByOrderIdAndStatus(request.orderId(), PaymentStatus.COMPLETED)) {
            throw new IllegalArgumentException("이미 결제된 주문입니다.");
        }

        OrderInternalResponse order = orderClient.getOrder(request.orderId());

        if (!order.customerId().equals(userId)) {
            throw new IllegalArgumentException("본인 주문만 결제할 수 있습니다.");
        }

        if (!"CREATED".equals(order.status())) {
            throw new IllegalArgumentException("결제 가능한 주문 상태가 아닙니다.");
        }

        if (request.amount() != null && !order.totalAmount().equals(request.amount())) {
            throw new IllegalArgumentException("주문 금액과 결제 금액이 일치하지 않습니다.");
        }

        String merchantOrderId = createTossOrderId(order.orderId());
        if (!merchantOrderId.equals(request.tossOrderId())) {
            throw new IllegalArgumentException("토스 주문번호가 올바르지 않습니다.");
        }

        Payment savedPayment;
        try {
            Payment payment = Payment.builder()
                    .orderId(order.orderId())
                    .customerId(order.customerId())
                    .customerEmail(order.customerEmail())
                    .amount(order.totalAmount())
                    .merchantOrderId(merchantOrderId)
                    .paymentKey(request.paymentKey())
                    .paymentMethod(PaymentMethod.UNKNOWN)
                    .status(PaymentStatus.PENDING)
                    .provider(paymentGateway.providerName())
                    .createdAt(LocalDateTime.now())
                    .build();

            savedPayment = paymentRepository.save(payment);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 결제된 주문입니다.");
        }

        PaymentGatewayResult gatewayResult = paymentGateway.approve(new PaymentApprovalCommand(
                order.orderId(),
                order.customerId(),
                order.customerEmail(),
                order.totalAmount(),
                merchantOrderId,
                request.paymentKey()
        ));

        if (!gatewayResult.approved()) {
            savedPayment.markFailed(gatewayResult.failureReason());
            return new ApiResponse<>(true, PaymentDetailResponse.from(savedPayment), "결제가 실패했습니다.");
        }

        savedPayment.updatePaymentMethod(gatewayResult.paymentMethod());
        savedPayment.markCompleted(gatewayResult.providerTransactionId(), gatewayResult.approvedAt());
        publishPaymentCompleted(savedPayment);

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

    private void validateCheckoutConfiguration() {
        if (isBlank(tossPaymentsProperties.clientKey())
                || isBlank(tossPaymentsProperties.successUrl())
                || isBlank(tossPaymentsProperties.failUrl())) {
            throw new IllegalArgumentException("토스페이먼츠 체크아웃 설정이 올바르지 않습니다.");
        }
    }

    private String createTossOrderId(Long orderId) {
        return "delivery-order-" + orderId;
    }

    private String createOrderName(OrderInternalResponse order) {
        return order.storeName() + " 주문 " + order.orderId();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void publishPaymentCompleted(Payment payment) {
        try {
            PaymentCompletedEvent event = new PaymentCompletedEvent(
                    UUID.randomUUID().toString(),
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getCustomerId(),
                    payment.getAmount()
            );

            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = OutboxEvent.create(
                    "PAYMENT",
                    payment.getId(),
                    "PAYMENT_COMPLETED",
                    payload
            );

            outboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
