package com.delivery.user.controller;

import com.delivery.common.ApiResponse;
import com.delivery.user.dto.*;
import com.delivery.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                            HttpServletResponse response){
        return authService.login(request, response);
    }

    @PostMapping("/refesh")
    public ApiResponse<JwtResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        return authService.refresh(request, response);
    }
}