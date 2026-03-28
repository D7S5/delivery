package com.example.riderservice.dto;

import com.example.riderservice.entity.AssignmentStatus;
import com.example.riderservice.entity.DeliveryAssignment;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public record RiderAssignmentResponse(
        Long id,
        Long assignmentId,
        Long orderReceiveId,
        Long orderId,
        String storeName,
        String deliveryAddress,
        String requestMessage,
        Integer totalAmount,
        String status,
        LocalDateTime assignedAt,
        LocalDateTime expiresAt,
        List<RiderAssignmentItemResponse> items
) {

    @SuppressWarnings("unchecked")
    public static RiderAssignmentResponse from(DeliveryAssignment assignment, Map<String, Object> order) {
        List<Map<String, Object>> itemMaps =
                (List<Map<String, Object>>) order.getOrDefault("items", Collections.emptyList());

        List<RiderAssignmentItemResponse> items = itemMaps.stream()
                .map(item -> new RiderAssignmentItemResponse(
                        item.get("menuId") == null ? null : Long.valueOf(String.valueOf(item.get("menuId"))),
                        String.valueOf(item.get("menuName")),
                        item.get("menuPrice") == null ? 0 : Integer.valueOf(String.valueOf(item.get("menuPrice"))),
                        item.get("quantity") == null ? 0 : Integer.valueOf(String.valueOf(item.get("quantity")))
                ))
                .toList();

        Object rawStatus = order.get("status");
        String status = rawStatus != null ? String.valueOf(rawStatus) : assignment.getStatus().name();

        return new RiderAssignmentResponse(
                assignment.getId(),                // 프론트 available 목록에서 accept 시 사용 가능
                assignment.getId(),
                assignment.getOrderReceiveId(),
                assignment.getOrderId(),
                stringValue(order.get("storeName")),
                stringValue(order.get("deliveryAddress")),
                stringValue(order.get("requestMessage")),
                intValue(order.get("totalAmount")),
                convertStatus(status, assignment.getStatus()),
                assignment.getAssignedAt(),
                assignment.getExpiresAt(),
                items
        );
    }

    private static String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static Integer intValue(Object value) {
        return value == null ? 0 : Integer.valueOf(String.valueOf(value));
    }

    private static String convertStatus(String orderStatus, AssignmentStatus assignmentStatus) {
        if (assignmentStatus == AssignmentStatus.ACCEPTED) {
            return "DELIVERING";
        }
        return orderStatus;
    }
}