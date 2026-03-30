package com.delivery.store.repository;

import com.delivery.store.entity.OrderReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface OrderReceiveRepository extends JpaRepository<OrderReceive, Long> {
    Optional<OrderReceive> findByIdAndStoreId(Long id, Long storeId);

    List<OrderReceive> findAllByStoreIdOrderByIdDesc(Long storeId);

    List<OrderReceive> findAllByStoreIdInOrderByIdDesc(List<Long> storeIds);

    Optional<OrderReceive> findByIdAndStoreIdIn(Long orderReceiveId, List<Long> storeIds);
}
