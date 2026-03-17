package com.delivery.payment.repository;

import com.delivery.payment.entity.OutboxEvent;
import com.delivery.payment.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findTop100ByStatusOrderByIdAsc(OutboxStatus status);
}
