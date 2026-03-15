package com.delivery.payment;

import com.delivery.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @GetMapping("/api/payments/hello")
    public ApiResponse<String> hello() {
        return new ApiResponse<>(true, "payment-service", "ok");
    }
}