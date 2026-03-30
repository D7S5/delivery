package com.example.riderservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RiderLocationUpdateRequest {
    private Double lat;
    private Double lng;
}
