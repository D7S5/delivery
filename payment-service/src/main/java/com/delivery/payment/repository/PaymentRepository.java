package com.delivery.payment.repository;

import com.delivery.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCustomerIdOrderByIdDesc(Long customerId);

    boolean existsByOrderId(Long aLong);
}
