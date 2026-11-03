package com.fossele.rider.response;

import com.fossele.rider.entity.Ride;
import com.fossele.rider.entity.RideStatus;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class RideMetricsResponse {
    private UUID rideId;
    private String riderId;
    private String assignedDriverName;
    private RideStatus status;
    private double pickupLongitude;
    private double pickupLatitude;
    private double dropoffLongitude;
    private double dropoffLatitude;
    private String estimatedDistance; // e.g., "4.5 km"
}