package com.example.riderservice.scheduler;

import com.example.riderservice.entity.AssignmentStatus;
import com.example.riderservice.entity.DeliveryAssignment;
import com.example.riderservice.repository.DeliveryAssignmentRepository;
import com.example.riderservice.service.DispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DispatchScheduler {

    private final DeliveryAssignmentRepository deliveryAssignmentRepository;
    private final DispatchService dispatchService;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void expireAssignmentsAndRedispatch() {
        List<DeliveryAssignment> expiredAssignments =
                deliveryAssignmentRepository.findByStatusAndExpiresAtBefore(
                        AssignmentStatus.ASSIGNED,
                        LocalDateTime.now()
                );

        for (DeliveryAssignment assignment : expiredAssignments) {
            assignment.expire();
            dispatchService.dispatch(assignment.getOrderReceiveId());
        }
    }
}