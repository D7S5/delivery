package com.example.riderservice.service;

import com.delivery.common.ApiResponse;
import com.example.riderservice.dto.CreateRiderRequest;
import com.example.riderservice.entity.Rider;
import com.example.riderservice.entity.RiderStatus;
import com.example.riderservice.repository.RiderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiderRegistrationService {

    private final RiderRepository riderRepository;

    @Transactional
    public ApiResponse<Long> createRider(CreateRiderRequest request) {
        if (request.userId() == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        riderRepository.findByUserId(request.userId()).ifPresent(r -> {
            throw new IllegalArgumentException("이미 생성된 라이더입니다.");
        });

        Rider rider = new Rider(
                request.userId(),
                request.riderName(),
                RiderStatus.OFFLINE
        );

        Rider saved = riderRepository.save(rider);
        return new ApiResponse<>(true, saved.getId(), "라이더 생성 성공");
    }
}
