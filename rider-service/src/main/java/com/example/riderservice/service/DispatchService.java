package com.example.riderservice.service;

import com.example.riderservice.dto.DispatchResponse;
import com.example.riderservice.entity.*;
import com.example.riderservice.repository.DeliveryAssignmentRepository;
import com.example.riderservice.repository.OrderReceiveRepository;
import com.example.riderservice.repository.RiderRepository;
import com.example.riderservice.util.DistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DispatchService {

    private final OrderReceiveRepository orderReceiveRepository;
    private final RiderRepository riderRepository;
    private final DeliveryAssignmentRepository deliveryAssignmentRepository;

    private static final double MAX_DISPATCH_DISTANCE_KM = 3.0;
    private static final int ASSIGN_EXPIRE_SECONDS = 30;

    @Transactional
    public DispatchResponse markReadyAndDispatch(Long orderReceiveId) {
        OrderReceive order = orderReceiveRepository.findById(orderReceiveId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

        order.markReadyForDelivery();

        DeliveryAssignment assignment = dispatch(orderReceiveId);

        if (assignment == null) {
            return new DispatchResponse(null, orderReceiveId, null, "배차 가능한 라이더가 없습니다.");
        }

        return new DispatchResponse(
                assignment.getId(),
                orderReceiveId,
                assignment.getRiderId(),
                "라이더 배차 요청 완료"
        );
    }

    @Transactional
    public DeliveryAssignment dispatch(Long orderReceiveId) {
        OrderReceive order = orderReceiveRepository.findById(orderReceiveId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

        if (order.getStatus() != OrderStatus.READY_FOR_DELIVERY) {
            return null;
        }

        boolean exists = deliveryAssignmentRepository.existsByOrderReceiveIdAndStatusIn(
                orderReceiveId,
                List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.ACCEPTED)
        );

        if (exists) {
            return null;
        }

        List<Rider> availableRiders = riderRepository.findByStatusAndLastLocationUpdatedAtAfter(
                RiderStatus.ONLINE,
                LocalDateTime.now().minusMinutes(3)
        );

        List<RiderDistance> candidates = availableRiders.stream()
                .filter(r -> r.getCurrentLat() != null && r.getCurrentLng() != null)
                .map(r -> new RiderDistance(
                        r,
                        DistanceUtils.calculateKm(
                                order.getStoreLat(),
                                order.getStoreLng(),
                                r.getCurrentLat(),
                                r.getCurrentLng()
                        )
                ))
                .filter(rd -> rd.distanceKm <= MAX_DISPATCH_DISTANCE_KM)
                .sorted(Comparator.comparingDouble(rd -> rd.distanceKm))
                .toList();

        for (RiderDistance candidate : candidates) {
            Rider rider = candidate.rider;

            boolean rejectedBefore = deliveryAssignmentRepository.existsByOrderReceiveIdAndRiderIdAndStatus(
                    orderReceiveId,
                    rider.getId(),
                    AssignmentStatus.REJECTED
            );

            if (rejectedBefore) {
                continue;
            }

            DeliveryAssignment assignment = DeliveryAssignment.builder()
                    .orderReceiveId(orderReceiveId)
                    .riderId(rider.getId())
                    .status(AssignmentStatus.ASSIGNED)
                    .assignedAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusSeconds(ASSIGN_EXPIRE_SECONDS))
                    .build();

            return deliveryAssignmentRepository.save(assignment);
        }

        return null;
    }

    @Transactional
    public void acceptAssignment(Long riderUserId, Long assignmentId) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("배차 요청이 없습니다."));

        Rider rider = riderRepository.findByUserId(riderUserId)
                .orElseThrow(() -> new IllegalArgumentException("라이더를 찾을 수 없습니다."));

        if (!assignment.getRiderId().equals(rider.getId())) {
            throw new IllegalStateException("본인에게 온 배차만 수락할 수 있습니다.");
        }

        if (assignment.getExpiresAt() != null && assignment.getExpiresAt().isBefore(LocalDateTime.now())) {
            assignment.expire();
            throw new IllegalStateException("배차 응답 시간이 지났습니다.");
        }

        assignment.accept();

        OrderReceive order = orderReceiveRepository.findById(assignment.getOrderReceiveId())
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

        order.startDelivery(rider.getId());
        rider.changeStatus(RiderStatus.DELIVERING);
    }

    @Transactional
    public void rejectAssignment(Long riderUserId, Long assignmentId) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("배차 요청이 없습니다."));

        Rider rider = riderRepository.findByUserId(riderUserId)
                .orElseThrow(() -> new IllegalArgumentException("라이더를 찾을 수 없습니다."));

        if (!assignment.getRiderId().equals(rider.getId())) {
            throw new IllegalStateException("본인에게 온 배차만 거절할 수 있습니다.");
        }

        assignment.reject();

        dispatch(assignment.getOrderReceiveId());
    }

    @Transactional
    public void completeDelivery(Long riderUserId, Long orderReceiveId) {
        Rider rider = riderRepository.findByUserId(riderUserId)
                .orElseThrow(() -> new IllegalArgumentException("라이더를 찾을 수 없습니다."));

        OrderReceive order = orderReceiveRepository.findById(orderReceiveId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

        if (order.getRiderId() == null || !order.getRiderId().equals(rider.getId())) {
            throw new IllegalStateException("본인이 맡은 주문만 완료 처리할 수 있습니다.");
        }

        order.completeDelivery();
        rider.changeStatus(RiderStatus.ONLINE);
    }

    private static class RiderDistance {
        private final Rider rider;
        private final double distanceKm;

        private RiderDistance(Rider rider, double distanceKm) {
            this.rider = rider;
            this.distanceKm = distanceKm;
        }
    }
}
