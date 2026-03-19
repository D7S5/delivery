package com.delivery.user.service;

import com.delivery.common.ApiResponse;
import com.delivery.user.dto.LoginRequest;
import com.delivery.user.dto.LoginResponse;
import com.delivery.user.dto.SignUpRequest;
import com.delivery.user.dto.UserResponse;
import com.delivery.user.entity.User;
import com.delivery.user.repository.UserRepository;
import com.delivery.user.security.CustomUserDetails;
import com.delivery.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    @Transactional
    public ApiResponse<UserResponse> signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용중인 이메일 입니다.");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .role(request.role())
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        return new ApiResponse<>(
                true,
                UserResponse.from(savedUser),
                "회원가입이 완료되었습니다."
        );
    }

    public ApiResponse<LoginResponse> login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.createAccessToken(
                principal.getId(),
                principal.getEmail(),
                principal.getRole());

        LoginResponse response = new LoginResponse(
                accessToken,
                new UserResponse(
                        principal.getId(),
                        principal.getEmail(),
                        principal.getName(),
                        principal.getRole()
                )
        );

        return new ApiResponse<>(
                true,
                response,
                "로그인에 성공했습니다."
        );
    }
}
