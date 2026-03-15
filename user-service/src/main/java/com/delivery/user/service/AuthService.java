package com.delivery.user.service;

import com.delivery.common.ApiResponse;
import com.delivery.user.dto.LoginRequest;
import com.delivery.user.dto.LoginResponse;
import com.delivery.user.dto.SignUpRequest;
import com.delivery.user.dto.UserResponse;
import com.delivery.user.entity.User;
import com.delivery.user.repository.UserRepository;
import com.delivery.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("이메일 혹은 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 혹은 비밀번호가 올바르지 않습니다.");
        }
        String accessToken = jwtTokenProvider.createAccessToken(user);

        LoginResponse response = new LoginResponse(
                accessToken,
                UserResponse.from(user)
        );

        return new ApiResponse<>(
                true,
                response,
                "로그인에 성공했습니다."
        );
    }
}
