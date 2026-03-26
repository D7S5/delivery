package com.example.riderservice.controller;

import com.example.riderservice.service.DispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rider/dispatch")
@RequiredArgsConstructor
public class RiderDispatchController {

    private final DispatchService dispatchService;

    @PatchMapping("/{assignmentId}/accept")
    public String accept(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long assignmentId
    ) {
        dispatchService.acceptAssignment(userId, assignmentId);
        return "배차 수락 완료";
    }

    @PatchMapping("/{assignmentId}/reject")
    public String reject(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long assignmentId
    ) {
        dispatchService.rejectAssignment(userId, assignmentId);
        return "배차 거절 완료";
    }

    @PatchMapping("/orders/{orderReceiveId}/complete")
    public String complete(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderReceiveId
    ) {
        dispatchService.completeDelivery(userId, orderReceiveId);
        return "배달 완료";
    }
}
