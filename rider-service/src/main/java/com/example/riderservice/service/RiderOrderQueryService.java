package com.example.riderservice.service;

import com.delivery.common.ApiResponse;
import com.example.riderservice.client.StoreOrderQueryClient;
import com.example.riderservice.dto.RiderAssignmentResponse;
import com.example.riderservice.entity.AssignmentStatus;
import com.example.riderservice.entity.DeliveryAssignment;
import com.example.riderservice.entity.Rider;
import com.example.riderservice.repository.DeliveryAssignmentRepository;
import com.example.riderservice.repository.RiderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RiderOrderQueryService {

    private final DeliveryAssignmentRepository deliveryAssignmentRepository;
    private final RiderRepository riderRepository;
    private final StoreOrderQueryClient storeOrderQueryClient;

    @Transactional(readOnly = true)
    public ApiResponse<List<RiderAssignmentResponse>> getAvailableOrders(Long riderUserId, String role) {
        validateRider(role);

        Rider rider = riderRepository.findByUserId(riderUserId)
                .orElseThrow(() -> new IllegalArgumentException("라이더가 없습니다."));

        List<RiderAssignmentResponse> responses = deliveryAssignmentRepository
                .findByRiderIdAndStatus(rider.getId(), AssignmentStatus.ASSIGNED)
                .stream()
                .map(this::toResponse)
                .sorted(Comparator.comparing(RiderAssignmentResponse::assignmentId).reversed())
                .toList();

        return new ApiResponse<>(true, responses, "배차 가능한 주문 조회 성공");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<RiderAssignmentResponse>> getMyOrders(Long riderUserId, String role) {
        validateRider(role);

        Rider rider = riderRepository.findByUserId(riderUserId)
                .orElseThrow(() -> new IllegalArgumentException("라이더가 없습니다."));

        List<RiderAssignmentResponse> responses = deliveryAssignmentRepository
                .findByRiderIdAndStatus(rider.getId(), AssignmentStatus.ACCEPTED)
                .stream()
                .map(this::toResponse)
                .sorted(Comparator.comparing(RiderAssignmentResponse::assignmentId).reversed())
                .toList();

        return new ApiResponse<>(true, responses, "내 배달 목록 조회 성공");
    }

    private RiderAssignmentResponse toResponse(DeliveryAssignment assignment) {
        Map<String, Object> order = storeOrderQueryClient.getOrder(orderAssignmentOrderReceiveId(assignment));

        return RiderAssignmentResponse.from(assignment, order);
    }

    private Long orderAssignmentOrderReceiveId(DeliveryAssignment assignment) {
        return assignment.getOrderReceiveId();
    }

    private void validateRider(String role) {
        if (!"RIDER".equals(role)) {
            throw new IllegalArgumentException("라이더만 접근할 수 있습니다.");
        }
    }
}
