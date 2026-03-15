package com.delivery.store;

import com.delivery.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StoreController {

    @GetMapping("/api/stores/hello")
    public ApiResponse<String> hello() {
        return new ApiResponse<>(true, "store-service", "ok");
    }
}