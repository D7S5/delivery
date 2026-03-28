package com.example.riderservice.service;

import com.delivery.common.event.DeliveryCompletedEvent;
import com.delivery.common.event.DeliveryStartedEvent;
import com.delivery.common.event.OrderReadyForDeliveryEvent;
import com.example.riderservice.client.OrderServiceClient;
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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DispatchService {

    private final RiderRepository riderRepository;
    private final DeliveryAssignmentRepository deliveryAssignmentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderServiceClient orderServiceClient;
    private final StoreOrderSyncService storeOrderSyncService;

    // 배차 후보 찾는 로직

    @Transactional
    public void dispatch(OrderReadyForDeliveryEvent event) {
        System.out.println("dispatch 호출됨");
        System.out.println("event = " + event);

        // 1. 이미 배정된 주문인지 확인
        boolean alreadyAssigned = deliveryAssignmentRepository.existsByOrderIdAndStatusIn(
                event.orderId(),
                List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.ACCEPTED)
        );

        System.out.println("alreadyAssigned = " + alreadyAssigned);

        if (alreadyAssigned) {
            System.out.println("이미 배정된 주문이라 종료");
            return;
        }

        // 2. 온라인 라이더 한 명 조회
        List<Rider> riders = riderRepository.findByStatus(RiderStatus.ONLINE);

        System.out.println("online riders = " + riders);

        if (riders.isEmpty()) {
            System.out.println("온라인 라이더 없음");
            return;
        }

        // 3. 첫 번째 라이더에게 바로 배정
        Rider rider = riders.get(0);

        DeliveryAssignment assignment = DeliveryAssignment.builder()
                .orderId(event.orderId())
                .orderReceiveId(event.orderReceiveId())
                .riderId(rider.getId())
                .status(AssignmentStatus.ASSIGNED)
                .assignedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        deliveryAssignmentRepository.save(assignment);

        System.out.println("배차 저장 완료 = " + assignment);
    }
//    @Transactional
//    public void dispatch(OrderReadyForDeliveryEvent event) {
//        Map<String, Object> orderStatus = orderServiceClient.getOrderStatus(event.orderId());
//
//        System.out.println(orderStatus);
//        if (!"READY_FOR_DELIVERY".equals(orderStatus.get("status"))) {
//            return;
//        }
//
//        boolean alreadyAssigned = deliveryAssignmentRepository.existsByOrderIdAndStatusIn(
//                event.orderId(),
//                List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.ACCEPTED)
//        );
//
//        System.out.println("=============" + alreadyAssigned);
//        if (alreadyAssigned) {
//            return;
//        }
//
//        List<Rider> riders = riderRepository.findByStatusAndLastLocationUpdatedAtAfter(
//                RiderStatus.ONLINE,
//                LocalDateTime.now().minusMinutes(3)
//        );
//        System.out.println("========================" + riders);
//
//        List<RiderDistance> candidates = riders.stream()
//                .filter(r -> r.getCurrentLat() != null && r.getCurrentLng() != null)
//                .map(r -> new RiderDistance(
//                        r,
//                        DistanceUtils.calculateKm(
//                                event.storeLat(),
//                                event.storeLng(),
//                                r.getCurrentLat(),
//                                r.getCurrentLng()
//                        )
//                ))
//                .filter(rd -> rd.distanceKm <= 3.0)
//                .sorted(Comparator.comparingDouble(rd -> rd.distanceKm))
//                .toList();
//
//        System.out.println("=====================" + candidates);
//        for (RiderDistance candidate : candidates) {
//            Rider rider = candidate.rider;
//
//            boolean rejectedBefore = deliveryAssignmentRepository.existsByOrderIdAndRiderIdAndStatus(
//                    event.orderId(),
//                    rider.getId(),
//                    AssignmentStatus.REJECTED
//            );
//            if (rejectedBefore) {
//                continue;
//            }
//
//            DeliveryAssignment assignment = DeliveryAssignment.builder()
//                    .orderId(event.orderId())
//                    .orderReceiveId(event.orderReceiveId())
//                    .riderId(rider.getId())
//                    .status(AssignmentStatus.ASSIGNED)
//                    .assignedAt(LocalDateTime.now())
//                    .expiresAt(LocalDateTime.now().plusSeconds(30))
//                    .build();
//
//            System.out.println("=================" + assignment);
//
//            deliveryAssignmentRepository.save(assignment);
//            return;
//        }
//    }

    // 배차 수락
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

        storeOrderSyncService.startDelivery(assignment.getOrderReceiveId());
        orderServiceClient.markDelivery(assignment.getOrderId());

        DeliveryStartedEvent event = new DeliveryStartedEvent(
                assignment.getOrderId(),
                assignment.getOrderReceiveId(),
                rider.getId(),
                LocalDateTime.now()
        );
        kafkaTemplate.send("delivery.started", String.valueOf(assignment.getOrderId()), event);
    }

    // 배차 거절
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
    }
//  배달 완료 처리
    @Transactional
    public void completeDelivery(Long riderUserId, Long orderReceiveId) {
        Rider rider = riderRepository.findByUserId(riderUserId)
                .orElseThrow(() -> new IllegalArgumentException("라이더가 없습니다."));

        DeliveryAssignment assignment = deliveryAssignmentRepository
                .findTopByOrderReceiveIdAndRiderIdOrderByIdDesc(orderReceiveId, rider.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 주문에 대한 배차가 없습니다."));

        if (assignment.getStatus() != AssignmentStatus.ACCEPTED) {
            throw new IllegalStateException("수락된 배차만 완료 처리할 수 있습니다.");
        }

        rider.changeStatus(RiderStatus.ONLINE);

        storeOrderSyncService.completeDelivery(orderReceiveId);
        orderServiceClient.markComplete(assignment.getOrderId());

        DeliveryCompletedEvent event = new DeliveryCompletedEvent(
                assignment.getOrderId(),
                assignment.getOrderReceiveId(),
                rider.getId(),
                LocalDateTime.now()
        );
        kafkaTemplate.send("delivery.completed", String.valueOf(assignment.getOrderId()), event);
    }
    private record RiderDistance(Rider rider, double distanceKm) {
    }
}