package com.delivery.order;

import com.delivery.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @GetMapping("/api/orders/hello")
    public ApiResponse<String> hello() {
        return new ApiResponse<>(true, "order-service", "ok");
    }
}