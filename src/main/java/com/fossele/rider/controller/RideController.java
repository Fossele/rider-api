package com.fossele.rider.controller;

import com.fossele.rider.entity.Ride;
import com.fossele.rider.response.RideMetricsResponse;
import com.fossele.rider.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private final GeoLocationService geoLocationService;

    @PostMapping("/request")
    public ResponseEntity<Ride> requestRide(
            @RequestParam String riderId,
            @RequestParam double longitude,
            @RequestParam double latitude) {

        Ride assignedRide = geoLocationService.createRideRequest(riderId, longitude, latitude);
        return ResponseEntity.ok(assignedRide);
    }

    @GetMapping("/{rideId}/metrics")
    public ResponseEntity<RideMetricsResponse> getRideStatusAndMetrics(@PathVariable UUID rideId) {
        RideMetricsResponse metrics = geoLocationService.getRideMetrics(rideId);
        return ResponseEntity.ok(metrics);
    }
}