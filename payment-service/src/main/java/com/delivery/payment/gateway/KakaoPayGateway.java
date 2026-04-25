package com.delivery.payment.gateway;

import com.delivery.payment.entity.PaymentMethod;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;

@Component
@ConditionalOnProperty(prefix = "payment.kakao-pay", name = "enabled", havingValue = "true")
public class KakaoPayGateway implements PaymentGateway {

    private final RestClient restClient;
    private final KakaoPayProperties properties;

    public KakaoPayGateway(RestClient.Builder restClientBuilder, KakaoPayProperties properties) {
        this.properties = properties;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public PaymentGatewayResult approve(PaymentApprovalCommand command) {
        validateConfiguration();

        try {
            KakaoPayApproveResponse response = restClient.post()
                    .uri(properties.baseUrl() + properties.approvePath())
                    .header(HttpHeaders.AUTHORIZATION, "SECRET_KEY " + properties.secretKey())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(new KakaoPayApproveRequest(
                            properties.cid(),
                            command.kakaoTid(),
                            command.merchantOrderId(),
                            String.valueOf(command.customerId()),
                            command.kakaoPgToken()
                    ))
                    .retrieve()
                    .body(KakaoPayApproveResponse.class);

            if (response == null) {
                throw new PaymentGatewayException("카카오페이 응답이 비어 있습니다.");
            }

            if (!command.merchantOrderId().equals(response.partnerOrderId())) {
                throw new PaymentGatewayException("카카오페이 주문번호 검증에 실패했습니다.");
            }

            String transactionKey = response.aid() != null ? response.aid() : response.tid();
            LocalDateTime approvedAt = response.approvedAt() != null
                    ? response.approvedAt().toLocalDateTime()
                    : LocalDateTime.now();

            return PaymentGatewayResult.approved(transactionKey, approvedAt, PaymentMethod.KAKAO_PAY);
        } catch (RestClientResponseException e) {
            return PaymentGatewayResult.failed("카카오페이 승인 호출에 실패했습니다.");
        } catch (PaymentGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new PaymentGatewayException("카카오페이 승인 호출에 실패했습니다.", e);
        }
    }

    @Override
    public String providerName() {
        return "KAKAO_PAY";
    }

    @Override
    public boolean supports(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.KAKAO_PAY;
    }

    private void validateConfiguration() {
        if (isBlank(properties.baseUrl())
                || isBlank(properties.approvePath())
                || isBlank(properties.cid())
                || isBlank(properties.secretKey())) {
            throw new PaymentGatewayException("카카오페이 연동 설정이 올바르지 않습니다.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
