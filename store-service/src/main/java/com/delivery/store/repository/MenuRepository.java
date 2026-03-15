package com.delivery.store.repository;

import com.delivery.store.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByStoreIdAndDeletedFalse(Long storeId);

    Optional<Menu> findByIdAndDeletedFalse(Long id);
}
