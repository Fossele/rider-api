package com.fossele.rider.controller;

import com.fossele.rider.entity.Ride;
import com.fossele.rider.response.RideMetricsResponse;
import com.fossele.rider.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
@Slf4j
public class RideController {

    private final GeoLocationService geoLocationService;

    @PostMapping("/request")
    public ResponseEntity<Ride> requestRide(
            @RequestParam UUID riderId,
            @RequestParam double longitude,
            @RequestParam double latitude) {
        log.info("riding will be done soon");
        Ride assignedRide = geoLocationService.createRideRequest(riderId, longitude, latitude);
        log.info("ride with id {} assigned", assignedRide.getId());
        return ResponseEntity.ok(assignedRide);
    }


    @GetMapping("/{rideId}/metrics")
    public ResponseEntity<RideMetricsResponse> getRideStatusAndMetrics(@PathVariable UUID rideId) {
        RideMetricsResponse metrics = geoLocationService.getRideMetrics(rideId);
        log.info("rideId {} with riderId {}", rideId, metrics.getRiderId());
        return ResponseEntity.ok(metrics);
    }

    @PostMapping("/{rideId}/complete")
    public ResponseEntity<Ride> completeRide(@PathVariable UUID rideId) {
        Ride completedRide = geoLocationService.completeRide(rideId);
        log.info("rideId with id {} completed", completedRide.getId());
        return ResponseEntity.ok(completedRide);
    }

    @PostMapping("/{rideId}/cancel")
    public ResponseEntity<Ride> cancelRide(@PathVariable UUID rideId) {
        Ride cancelledRide = geoLocationService.cancelRide(rideId);
        log.info("rideId with id {} cancelled", cancelledRide.getId());
        return ResponseEntity.ok(cancelledRide);
    }
}