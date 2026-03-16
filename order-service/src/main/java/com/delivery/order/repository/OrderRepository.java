package com.delivery.order.repository;

import com.delivery.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOOrderByIdDesc(Long customerId);
    Optional<Order> findByIdAndCustomerId(Long id, Long customerId);
}
