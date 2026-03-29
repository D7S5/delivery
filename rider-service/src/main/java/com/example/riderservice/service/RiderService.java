package com.example.riderservice.service;

import com.example.riderservice.dto.OnlineRequest;
import com.example.riderservice.dto.RiderLocationRequest;
import com.example.riderservice.entity.Rider;
import com.example.riderservice.entity.RiderStatus;
import com.example.riderservice.repository.RiderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;

    @Transactional
    public void updateLocation(Long userId, RiderLocationRequest request) {
        Rider rider = riderRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("라이더를 찾을 수 없습니다."));

        rider.updateLocation(request.lat(), request.lng());
    }

    @Transactional
    public void changeOnline(Long userId, OnlineRequest request) {
        Rider rider = riderRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("라이더를 찾을 수 없습니다."));
        rider.updateLocation(request.lat(), request.lng());
        rider.changeStatus(RiderStatus.ONLINE);
    }

    @Transactional
    public void changeOffline(Long userId) {
        Rider rider = riderRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("라이더를 찾을 수 없습니다."));
        rider.changeStatus(RiderStatus.OFFLINE);
    }
}
