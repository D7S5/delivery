package com.example.riderservice.service;

import com.delivery.common.ApiResponse;
import com.example.riderservice.dto.RiderLocationRequest;
import com.example.riderservice.dto.RiderStatusResponse;
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

    @Transactional(readOnly = true)
    public ApiResponse<RiderStatusResponse> getMyStatus(Long userId) {
        Rider rider = riderRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("라이더 정보를 찾을 수 없습니다."));

        RiderStatusResponse response = new RiderStatusResponse(
                rider.getId(),
                rider.getUserId(),
                rider.getStatus(),
                rider.getStatus() == RiderStatus.ONLINE
        );

        return new ApiResponse<>(true, response, "라이더 상태 조회 성공");
    }

    @Transactional
    public ApiResponse<Void> setOnline(Long userId, RiderLocationRequest request) {
        Rider rider = riderRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("라이더 정보를 찾을 수 없습니다."));

        rider.setOnline(request.lat(), request.lng());

        return new ApiResponse<>(true, null, "라이더가 온라인 상태로 전환되었습니다.");
    }

    @Transactional
    public ApiResponse<Void> setOffline(Long userId) {
        Rider rider = riderRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("라이더 정보를 찾을 수 없습니다."));

        rider.setOffline();

        return new ApiResponse<>(true, null, "라이더가 오프라인 상태로 전환되었습니다.");
    }
}