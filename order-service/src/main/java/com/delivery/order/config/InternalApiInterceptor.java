package com.delivery.order.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InternalApiInterceptor implements HandlerInterceptor {

    @Value("${internal.service-token}")
    private String internalServiceToken;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();

        if (!url.startsWith("/api/orders/internal/") && !url.matches("/api/orders/\\d+/paid")) {
            return true;
        }

        String token = request.getHeader("X-Internal-Service-Token");

        if (!internalServiceToken.equals(token)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                    {
                      "success": false,
                      "data": null,
                      "message": "내부 서비스 전용 API입니다."
                    }
                    """);
            return false;
        }
        return true;
    }
}
