package com.delivery.user.service;

import com.delivery.common.ApiResponse;
import com.delivery.user.dto.*;
import com.delivery.user.entity.User;
import com.delivery.user.repository.UserRepository;
import com.delivery.user.security.CookieUtil;
import com.delivery.user.security.CustomUserDetails;
import com.delivery.user.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static java.util.Objects.hash;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;

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

    public ApiResponse<LoginResponse> login(LoginRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.createAccessToken(
                principal.getId(),
                principal.getEmail(),
                principal.getRole());

        String refreshToken = jwtTokenProvider.createRefreshToken(
                principal.getId(),
                principal.getEmail(),
                principal.getRole()
        );

        cookieUtil.addRefreshTokenCookie(response, refreshToken);

        LoginResponse res = new LoginResponse(
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
                res,
                "로그인에 성공했습니다."
        );
    }

    public ApiResponse<JwtResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String oldRefreshToken = cookieUtil.getRefreshToken(request);

        if (oldRefreshToken == null || oldRefreshToken.isBlank()) {
            throw new RuntimeException("Refresh Token missing");
        }

        if (!jwtTokenProvider.validateToken(oldRefreshToken)) {
            throw new RuntimeException("Invalid Refresh Token");
        }

        String userId = jwtTokenProvider.getSubject(oldRefreshToken);
        String email = jwtTokenProvider.getEmail(oldRefreshToken);
        String role = jwtTokenProvider.getRole(oldRefreshToken);

        String newAccessToken = jwtTokenProvider.createAccessToken(Long.valueOf(userId), email, role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(Long.valueOf(userId), email, role);

        cookieUtil.addRefreshTokenCookie(response, newRefreshToken);

        JwtResponse res = new JwtResponse(newAccessToken);

        return new ApiResponse<>(
                true,
                res,
                "refresh"
        );
    }
}
