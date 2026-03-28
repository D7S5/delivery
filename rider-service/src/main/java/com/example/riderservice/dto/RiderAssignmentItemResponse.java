package com.example.riderservice.dto;

public record RiderAssignmentItemResponse(
        Long menuId,
        String menuName,
        Integer menuPrice,
        Integer quantity
) {
}