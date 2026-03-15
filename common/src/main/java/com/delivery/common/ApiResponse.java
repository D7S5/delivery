package com.delivery.common;

public record ApiResponse<T>(
        boolean success,
        T data,
        String message
) {
}