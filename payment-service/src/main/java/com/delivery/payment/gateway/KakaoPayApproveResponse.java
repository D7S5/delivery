package com.delivery.payment.gateway;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoPayApproveResponse(
        String aid,
        String tid,
        String cid,
        @JsonProperty("partner_order_id")
        String partnerOrderId,
        @JsonProperty("partner_user_id")
        String partnerUserId,
        @JsonProperty("payment_method_type")
        String paymentMethodType,
        @JsonProperty("approved_at")
        OffsetDateTime approvedAt
) {
}
