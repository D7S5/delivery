package com.delivery.payment.repository;

import com.delivery.payment.entity.Payment;
import com.delivery.payment.entity.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCustomerIdOrderByIdDesc(Long customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Payment> findByOrderId(Long orderId);

    boolean existsByOrderIdAndStatus(Long orderId, PaymentStatus status);
}
