package com.delivery.user.controller;

import com.delivery.common.ApiResponse;
import com.delivery.user.dto.LoginRequest;
import com.delivery.user.dto.LoginResponse;
import com.delivery.user.dto.SignUpRequest;
import com.delivery.user.dto.UserResponse;
import com.delivery.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<UserResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        return authService.signUp(request);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }
}
