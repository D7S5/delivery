package com.example.riderservice.repository;

import com.example.riderservice.entity.AssignmentStatus;
import com.example.riderservice.entity.DeliveryAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, Long> {

    boolean existsByOrderReceiveIdAndStatusIn(Long orderReceiveId, Collection<AssignmentStatus> statuses);

    boolean existsByOrderReceiveIdAndRiderIdAndStatus(Long orderReceiveId, Long riderId, AssignmentStatus status);

    List<DeliveryAssignment> findByStatusAndExpiresAtBefore(AssignmentStatus status, LocalDateTime now);

    Optional<DeliveryAssignment> findTopByOrderReceiveIdAndRiderIdOrderByIdDesc(Long orderReceiveId, Long riderId);

    List<DeliveryAssignment> findByRiderIdAndStatus(Long riderId, AssignmentStatus status);
}
