package com.example.riderservice.repository;

import com.example.riderservice.entity.OrderReceive;
import com.example.riderservice.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderReceiveRepository extends JpaRepository<OrderReceive, Long> {

    List<OrderReceive> findByStatus(OrderStatus status);
}
