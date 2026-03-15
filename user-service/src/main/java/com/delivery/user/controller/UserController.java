package com.delivery.user.controller;

import com.delivery.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/users/hello")
    public ApiResponse<String> hello() {
        return new ApiResponse<>(true, "user-service", "ok");
    }
}