package com.delivery.payment.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.gateway")
public record TossPaymentsProperties(
        String mode,
        String baseUrl,
        String approvePath,
        String clientKey,
        String secretKey,
        String successUrl,
        String failUrl
) {
}
