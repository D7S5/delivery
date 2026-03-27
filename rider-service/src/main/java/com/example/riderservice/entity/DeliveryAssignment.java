package com.example.riderservice.entity;

import com.example.riderservice.entity.AssignmentStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class DeliveryAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long orderReceiveId;
    private Long riderId;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;

    private LocalDateTime assignedAt;
    private LocalDateTime respondedAt;
    private LocalDateTime expiresAt;

    @Version
    private Long version;

    @Builder
    public DeliveryAssignment(Long orderId, Long orderReceiveId, Long riderId,
                              AssignmentStatus status, LocalDateTime assignedAt,
                              LocalDateTime expiresAt) {
        this.orderId = orderId;
        this.orderReceiveId = orderReceiveId;
        this.riderId = riderId;
        this.status = status;
        this.assignedAt = assignedAt;
        this.expiresAt = expiresAt;
    }

    public void accept() {
        this.status = AssignmentStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = AssignmentStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = AssignmentStatus.EXPIRED;
        this.respondedAt = LocalDateTime.now();
    }
}