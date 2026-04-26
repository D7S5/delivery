package com.delivery.payment.service;

import com.delivery.common.ApiResponse;
import com.delivery.payment.client.OrderClient;
import com.delivery.payment.dto.CreatePaymentRequest;
import com.delivery.payment.dto.OrderInternalResponse;
import com.delivery.payment.dto.PaymentDetailResponse;
import com.delivery.payment.entity.Payment;
import com.delivery.payment.entity.PaymentMethod;
import com.delivery.payment.entity.PaymentStatus;
import com.delivery.payment.gateway.PaymentGateway;
import com.delivery.payment.gateway.PaymentGatewayResult;
import com.delivery.payment.gateway.TossPaymentsProperties;
import com.delivery.payment.repository.OutboxRepository;
import com.delivery.payment.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private OrderClient orderClient;

    @Mock
    private PaymentGateway paymentGateway;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(
                paymentRepository,
                outboxRepository,
                orderClient,
                new ObjectMapper(),
                List.of(paymentGateway),
                new TossPaymentsProperties(
                        "toss",
                        "https://api.tosspayments.com",
                        "/v1/payments/confirm",
                        "test_ck",
                        "test_sk",
                        "http://localhost:3000/payments/success",
                        "http://localhost:3000/payments/fail"
                )
        );
    }

    @Test
    void createPaymentRequest_acceptsLowercasePaymentkeyAlias() throws Exception {
        CreatePaymentRequest request = new ObjectMapper().readValue(
                """
                        {
                          "orderId": 10,
                          "tossOrderId": "delivery-order-10",
                          "paymentkey": "payment-key-123",
                          "amount": 18000
                        }
                        """,
                CreatePaymentRequest.class
        );

        assertThat(request.paymentKey()).isEqualTo("payment-key-123");
    }

    @Test
    void createPaymentRequest_acceptsSnakeCasePaymentKeyAlias() throws Exception {
        CreatePaymentRequest request = new ObjectMapper().readValue(
                """
                        {
                          "orderId": 10,
                          "tossOrderId": "delivery-order-10",
                          "payment_key": "payment-key-123"
                        }
                        """,
                CreatePaymentRequest.class
        );

        assertThat(request.paymentKey()).isEqualTo("payment-key-123");
        assertThat(request.amount()).isNull();
    }

    @Test
    void createPayment_publishesOutboxWhenGatewayApproves() {
        when(paymentRepository.existsByOrderIdAndStatus(10L, PaymentStatus.COMPLETED)).thenReturn(false);
        when(orderClient.getOrder(10L)).thenReturn(new OrderInternalResponse(
                10L, 1L, "user@test.com", 3L, "store", 18000, "CREATED"
        ));
        when(paymentRepository.findByOrderId(10L)).thenReturn(Optional.empty());
        when(paymentGateway.supports(PaymentMethod.CARD)).thenReturn(true);
        when(paymentGateway.providerName()).thenReturn("MOCK");
        when(paymentGateway.approve(any())).thenReturn(
                PaymentGatewayResult.approved("tx-123", LocalDateTime.of(2026, 4, 25, 10, 0), PaymentMethod.CARD)
        );
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            ReflectionTestUtils.setField(payment, "id", 99L);
            return payment;
        });

        ApiResponse<PaymentDetailResponse> response = paymentService.createPayment(
                1L,
                "user@test.com",
                "CUSTOMER",
                new CreatePaymentRequest(10L, "delivery-order-10", "payment-key-123", 18000)
        );

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().paymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(response.getData().provider()).isEqualTo("MOCK");
        assertThat(response.getData().providerTransactionId()).isEqualTo("tx-123");
        assertThat(response.getData().paymentMethod()).isEqualTo(PaymentMethod.CARD);
        verify(outboxRepository).save(any());
    }

    @Test
    void createPayment_doesNotPublishOutboxWhenGatewayRejects() {
        when(paymentRepository.existsByOrderIdAndStatus(11L, PaymentStatus.COMPLETED)).thenReturn(false);
        when(orderClient.getOrder(11L)).thenReturn(new OrderInternalResponse(
                11L, 1L, "user@test.com", 3L, "store", 21000, "CREATED"
        ));
        when(paymentRepository.findByOrderId(11L)).thenReturn(Optional.empty());
        when(paymentGateway.supports(PaymentMethod.CARD)).thenReturn(true);
        when(paymentGateway.providerName()).thenReturn("MOCK");
        when(paymentGateway.approve(any())).thenReturn(
                PaymentGatewayResult.failed("한도 초과")
        );
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse<PaymentDetailResponse> response = paymentService.createPayment(
                1L,
                "user@test.com",
                "CUSTOMER",
                new CreatePaymentRequest(11L, "delivery-order-11", "payment-key-456", 21000)
        );

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().paymentStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(response.getData().failureReason()).isEqualTo("한도 초과");
        verify(outboxRepository, never()).save(any());
    }

    @Test
    void createPayment_usesOrderAmountWhenRequestAmountIsMissing() {
        when(paymentRepository.existsByOrderIdAndStatus(12L, PaymentStatus.COMPLETED)).thenReturn(false);
        when(orderClient.getOrder(12L)).thenReturn(new OrderInternalResponse(
                12L, 1L, "user@test.com", 3L, "store", 22000, "CREATED"
        ));
        when(paymentRepository.findByOrderId(12L)).thenReturn(Optional.empty());
        when(paymentGateway.supports(PaymentMethod.KAKAO_PAY)).thenReturn(true);
        when(paymentGateway.providerName()).thenReturn("MOCK");
        when(paymentGateway.approve(any())).thenReturn(
                PaymentGatewayResult.approved("tx-789", LocalDateTime.of(2026, 4, 25, 11, 0), PaymentMethod.KAKAO_PAY)
        );
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            ReflectionTestUtils.setField(payment, "id", 100L);
            return payment;
        });

        ApiResponse<PaymentDetailResponse> response = paymentService.createPayment(
                1L,
                "user@test.com",
                "CUSTOMER",
                new CreatePaymentRequest(
                        12L,
                        "delivery-order-12",
                        null,
                        PaymentMethod.KAKAO_PAY,
                        "kakao-tid-789",
                        "pg-token-789",
                        null
                )
        );

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().amount()).isEqualTo(22000);
        assertThat(response.getData().paymentMethod()).isEqualTo(PaymentMethod.KAKAO_PAY);
        verify(outboxRepository).save(any());
    }

    @Test
    void createPayment_reusesFailedPaymentForRetry() {
        Payment failedPayment = Payment.builder()
                .orderId(13L)
                .customerId(1L)
                .customerEmail("user@test.com")
                .amount(23000)
                .merchantOrderId("delivery-order-13")
                .paymentKey("old-payment-key")
                .paymentMethod(PaymentMethod.CARD)
                .status(PaymentStatus.FAILED)
                .provider("MOCK")
                .failureReason("한도 초과")
                .createdAt(LocalDateTime.of(2026, 4, 25, 9, 0))
                .build();
        ReflectionTestUtils.setField(failedPayment, "id", 101L);

        when(paymentRepository.existsByOrderIdAndStatus(13L, PaymentStatus.COMPLETED)).thenReturn(false);
        when(orderClient.getOrder(13L)).thenReturn(new OrderInternalResponse(
                13L, 1L, "user@test.com", 3L, "store", 23000, "CREATED"
        ));
        when(paymentRepository.findByOrderId(13L)).thenReturn(Optional.of(failedPayment));
        when(paymentGateway.supports(PaymentMethod.CARD)).thenReturn(true);
        when(paymentGateway.providerName()).thenReturn("MOCK");
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentGateway.approve(any())).thenReturn(
                PaymentGatewayResult.approved("tx-retry", LocalDateTime.of(2026, 4, 25, 12, 0), PaymentMethod.CARD)
        );

        ApiResponse<PaymentDetailResponse> response = paymentService.createPayment(
                1L,
                "user@test.com",
                "CUSTOMER",
                new CreatePaymentRequest(13L, "delivery-order-13", "new-payment-key", 23000)
        );

        assertThat(response.getData().paymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(response.getData().providerTransactionId()).isEqualTo("tx-retry");
        assertThat(response.getData().paymentKey()).isEqualTo("new-payment-key");
        verify(outboxRepository).save(any());
    }

    @Test
    void createPayment_rejectsWhenPaymentIsAlreadyPending() {
        Payment pendingPayment = Payment.builder()
                .orderId(14L)
                .customerId(1L)
                .customerEmail("user@test.com")
                .amount(24000)
                .merchantOrderId("delivery-order-14")
                .paymentKey("payment-key-in-progress")
                .paymentMethod(PaymentMethod.CARD)
                .status(PaymentStatus.PENDING)
                .provider("MOCK")
                .createdAt(LocalDateTime.of(2026, 4, 25, 9, 0))
                .build();

        when(paymentRepository.existsByOrderIdAndStatus(14L, PaymentStatus.COMPLETED)).thenReturn(false);
        when(orderClient.getOrder(14L)).thenReturn(new OrderInternalResponse(
                14L, 1L, "user@test.com", 3L, "store", 24000, "CREATED"
        ));
        when(paymentRepository.findByOrderId(14L)).thenReturn(Optional.of(pendingPayment));
        when(paymentGateway.supports(PaymentMethod.CARD)).thenReturn(true);
        when(paymentGateway.providerName()).thenReturn("MOCK");

        assertThatThrownBy(() -> paymentService.createPayment(
                1L,
                "user@test.com",
                "CUSTOMER",
                new CreatePaymentRequest(14L, "delivery-order-14", "another-payment-key", 24000)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 결제가 진행 중인 주문입니다.");

        verify(paymentRepository, never()).saveAndFlush(any());
        verify(paymentGateway, never()).approve(any());
    }
}
