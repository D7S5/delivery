package com.delivery.payment.gateway;

import com.delivery.payment.entity.PaymentMethod;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@ConditionalOnProperty(prefix = "payment.gateway", name = "mode", havingValue = "mock")
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentGatewayResult approve(PaymentApprovalCommand command) {
        if (isBlank(command.paymentKey())) {
            return PaymentGatewayResult.failed("paymentKey가 없어 승인할 수 없습니다.");
        }

        return PaymentGatewayResult.approved(command.paymentKey(), LocalDateTime.now(), PaymentMethod.CARD);
    }

    @Override
    public String providerName() {
        return "TOSS_PAYMENTS";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
