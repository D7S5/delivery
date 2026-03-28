package com.example.riderservice.repository;

import com.example.riderservice.entity.Rider;
import com.example.riderservice.entity.RiderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, Long> {

    Optional<Rider> findByUserId(Long userId);

    List<Rider> findByStatusAndLastLocationUpdatedAtAfter(RiderStatus status, LocalDateTime threshold);

    List<Rider> findByStatus(RiderStatus riderStatus);
}