package com.delivery.payment.gateway;

public interface PaymentGateway {
    PaymentGatewayResult approve(PaymentApprovalCommand command);

    String providerName();
}
