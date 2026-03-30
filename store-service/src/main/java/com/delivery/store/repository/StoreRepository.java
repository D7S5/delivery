package com.delivery.store.repository;

import com.delivery.store.entity.OrderReceive;
import com.delivery.store.entity.Store;
import com.delivery.store.entity.StoreStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByStatusNot(StoreStatus status);

    Optional<Store> findByIdAndStatusNot(Long id, StoreStatus status);

    Optional<Store> findByOwnerId(Long id);

    List<Store> findAllByOwnerId(Long userId);



}
