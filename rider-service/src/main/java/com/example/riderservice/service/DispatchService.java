package com.example.riderservice.service;

import com.delivery.common.event.DeliveryCompletedEvent;
import com.delivery.common.event.DeliveryStartedEvent;
import com.delivery.common.event.OrderReadyForDeliveryEvent;
import com.example.riderservice.client.OrderServiceClient;
import com.example.riderservice.dto.OrderStatusResponse;
import com.example.riderservice.entity.AssignmentStatus;
import com.example.riderservice.entity.DeliveryAssignment;
import com.example.riderservice.entity.Rider;
import com.example.riderservice.entity.RiderStatus;
import com.example.riderservice.repository.DeliveryAssignmentRepository;
import com.example.riderservice.repository.RiderRepository;
import com.example.riderservice.util.DistanceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DispatchService {

    private final RiderRepository riderRepository;
    private final DeliveryAssignmentRepository deliveryAssignmentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderServiceClient orderServiceClient;

    @Transactional
    public void dispatch(OrderReadyForDeliveryEvent event) {
        OrderStatusResponse orderStatus = orderServiceClient.getOrderStatus(event.orderId());

        if (!"READY_FOR_DELIVERY".equals(orderStatus.status())) {
            return;
        }

        boolean alreadyAssigned =
                deliveryAssignmentRepository.existsByOrderIdAndStatusIn(
                        event.orderId(),
                        List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.ACCEPTED)
                );

        if (alreadyAssigned) {
            return;
        }

        List<Rider> riders = riderRepository.findByStatusAndLastLocationUpdatedAtAfter(
                RiderStatus.ONLINE,
                LocalDateTime.now().minusMinutes(3)
        );

        List<RiderDistance> candidates = riders.stream()
                .filter(r -> r.getCurrentLat() != null && r.getCurrentLng() != null)
                .map(r -> new RiderDistance(
                        r,
                        DistanceUtils.calculateKm(
                                event.storeLat(),
                                event.storeLng(),
                                r.getCurrentLat(),
                                r.getCurrentLng()
                        )
                ))
                .filter(rd -> rd.distanceKm <= 3.0)
                .sorted(Comparator.comparingDouble(rd -> rd.distanceKm))
                .toList();

        for (RiderDistance candidate : candidates) {
            Rider rider = candidate.rider;

            boolean rejectedBefore =
                    deliveryAssignmentRepository.existsByOrderIdAndRiderIdAndStatus(
                            event.orderId(),
                            rider.getId(),
                            AssignmentStatus.REJECTED
                    );

            if (rejectedBefore) {
                continue;
            }

            DeliveryAssignment assignment = DeliveryAssignment.builder()
                    .orderId(event.orderId())
                    .orderReceiveId(event.orderReceiveId())
                    .riderId(rider.getId())
                    .status(AssignmentStatus.ASSIGNED)
                    .assignedAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusSeconds(30))
                    .build();

            deliveryAssignmentRepository.save(assignment);

            // 여기서 WebSocket/SSE/FCM 등으로 라이더에게 알림 가능
            return;
        }
    }

    @Transactional
    public void completeDelivery(Long riderUserId, Long assignmentId) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("배차가 없습니다."));

        Rider rider = riderRepository.findByUserId(riderUserId)
                .orElseThrow(() -> new IllegalArgumentException("라이더가 없습니다."));

        if (!assignment.getRiderId().equals(rider.getId())) {
            throw new IllegalStateException("본인 배달만 완료할 수 있습니다.");
        }

        rider.changeStatus(RiderStatus.ONLINE);

        DeliveryCompletedEvent event = new DeliveryCompletedEvent(
                assignment.getOrderId(),
                assignment.getOrderReceiveId(),
                rider.getId(),
                LocalDateTime.now()
        );

        kafkaTemplate.send("delivery.completed", String.valueOf(assignment.getOrderId()), event);
    }

    @Transactional
    public void acceptAssignment(Long riderUserId, Long assignmentId) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("배차가 없습니다."));

        Rider rider = riderRepository.findByUserId(riderUserId)
                .orElseThrow(() -> new IllegalArgumentException("라이더가 없습니다."));

        if (!assignment.getRiderId().equals(rider.getId())) {
            throw new IllegalStateException("본인 배차만 수락할 수 있습니다.");
        }

        if (assignment.getExpiresAt().isBefore(LocalDateTime.now())) {
            assignment.expire();
            throw new IllegalStateException("배차 응답 시간이 지났습니다.");
        }

        assignment.accept();
        rider.changeStatus(RiderStatus.DELIVERING);

        DeliveryStartedEvent event = new DeliveryStartedEvent(
                assignment.getOrderId(),
                assignment.getOrderReceiveId(),
                rider.getId(),
                LocalDateTime.now()
        );

        kafkaTemplate.send("delivery.started", String.valueOf(assignment.getOrderId()), event);
    }

    @Transactional
    public void rejectAssignment(Long riderUserId, Long assignmentId) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("배차가 없습니다."));

        Rider rider = riderRepository.findByUserId(riderUserId)
                .orElseThrow(() -> new IllegalArgumentException("라이더가 없습니다."));

        if (!assignment.getRiderId().equals(rider.getId())) {
            throw new IllegalStateException("본인 배차만 거절할 수 있습니다.");
        }

        assignment.reject();

        OrderReadyForDeliveryEvent retryEvent = new OrderReadyForDeliveryEvent(
                assignment.getOrderId(),
                assignment.getOrderReceiveId(),
                null,
                null,
                null,
                null,
                LocalDateTime.now()
        );

        // 여기서는 store 좌표가 필요하니 실전에서는 assignment에 storeLat/storeLng를 같이 넣거나
        // store-service Feign 조회를 추가하는 편이 좋습니다.
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