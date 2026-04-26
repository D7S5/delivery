package com.delivery.payment.gateway;

import com.delivery.payment.entity.PaymentMethod;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;
import java.util.Base64;

@Component
@ConditionalOnProperty(prefix = "payment.gateway", name = "mode", havingValue = "toss", matchIfMissing = true)
public class TossPaymentGateway implements PaymentGateway {

    private final RestClient restClient;
    private final TossPaymentsProperties properties;

    public TossPaymentGateway(RestClient.Builder restClientBuilder, TossPaymentsProperties properties) {
        this.properties = properties;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public PaymentGatewayResult approve(PaymentApprovalCommand command) {
        validateConfiguration();

        try {
            TossPaymentConfirmResponse response = restClient.post()
                    .uri(properties.baseUrl() + properties.approvePath())
                    .header(HttpHeaders.AUTHORIZATION, createAuthorizationHeader())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(new TossPaymentConfirmRequest(
                            command.paymentKey(),
                            command.merchantOrderId(),
                            command.amount()
                    ))
                    .retrieve()
                    .body(TossPaymentConfirmResponse.class);

            if (response == null) {
                throw new PaymentGatewayException("토스페이먼츠 응답이 비어 있습니다.");
            }

            if (!command.merchantOrderId().equals(response.orderId())) {
                throw new PaymentGatewayException("토스 주문번호 검증에 실패했습니다.");
            }

            String transactionKey = response.lastTransactionKey() != null ? response.lastTransactionKey() : response.paymentKey();
            LocalDateTime approvedAt = response.approvedAt() != null
                    ? response.approvedAt().toLocalDateTime()
                    : LocalDateTime.now();

            return PaymentGatewayResult.approved(transactionKey, approvedAt, resolvePaymentMethod(response));
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                throw new PaymentGatewayException("토스페이먼츠 승인 호출에 실패했습니다.", e);
            }

            TossPaymentErrorResponse error = parseError(e.getResponseBodyAsString());
            String message = error != null && error.message() != null ? error.message() : "토스페이먼츠 승인 호출에 실패했습니다.";
            return PaymentGatewayResult.failed(message);
        } catch (PaymentGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new PaymentGatewayException("토스페이먼츠 승인 호출에 실패했습니다.", e);
        }
    }

    @Override
    public String providerName() {
        return "TOSS_PAYMENTS";
    }

    @Override
    public boolean supports(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.CARD;
    }

    private String createAuthorizationHeader() {
        String encoded = Base64.getEncoder().encodeToString((properties.secretKey() + ":").getBytes());
        return "Basic " + encoded;
    }

    private void validateConfiguration() {
        if (isBlank(properties.baseUrl()) || isBlank(properties.approvePath()) || isBlank(properties.secretKey())) {
            throw new PaymentGatewayException("토스페이먼츠 연동 설정이 올바르지 않습니다.");
        }
    }

    private TossPaymentErrorResponse parseError(String body) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(body, TossPaymentErrorResponse.class);
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private PaymentMethod resolvePaymentMethod(TossPaymentConfirmResponse response) {
        if (response.easyPay() != null && response.easyPay().provider() != null) {
            String provider = response.easyPay().provider().toUpperCase();
            if (provider.contains("KAKAO")) {
                return PaymentMethod.KAKAO_PAY;
            }
            if (provider.contains("TOSS")) {
                return PaymentMethod.TOSS_PAY;
            }
        }

        if (response.method() == null) {
            return PaymentMethod.UNKNOWN;
        }

        return switch (response.method()) {
            case "카드", "CARD" -> PaymentMethod.CARD;
            case "계좌이체", "TRANSFER" -> PaymentMethod.TRANSFER;
            case "가상계좌", "VIRTUAL_ACCOUNT" -> PaymentMethod.VIRTUAL_ACCOUNT;
            case "휴대폰", "MOBILE_PHONE" -> PaymentMethod.MOBILE_PHONE;
            default -> PaymentMethod.UNKNOWN;
        };
    }
}
