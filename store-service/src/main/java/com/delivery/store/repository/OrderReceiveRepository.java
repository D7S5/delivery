package com.delivery.store.repository;

import com.delivery.store.entity.OrderReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OrderReceiveRepository extends JpaRepository<OrderReceive, Long> {
}
