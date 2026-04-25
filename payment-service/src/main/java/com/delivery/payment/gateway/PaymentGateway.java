package com.delivery.payment.gateway;

import com.delivery.payment.entity.PaymentMethod;

public interface PaymentGateway {
    PaymentGatewayResult approve(PaymentApprovalCommand command);

    String providerName();

    boolean supports(PaymentMethod paymentMethod);
}
