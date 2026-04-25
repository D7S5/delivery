package com.delivery.payment.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.kakao-pay")
public record KakaoPayProperties(
        boolean enabled,
        String baseUrl,
        String approvePath,
        String cid,
        String secretKey
) {
}
