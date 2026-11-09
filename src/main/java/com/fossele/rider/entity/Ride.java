package com.fossele.rider.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "rides")
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Setter
@Getter
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private  UUID riderId;
    private String assignedDriverName;

    private double pickupLongitude;
    private double pickupLatitude;
    private double dropoffLongitude;
    private double dropoffLatitude;

    //distance tracking
    private double estimatedDistanceKm;

    @Enumerated(EnumType.STRING)
    private RideStatus status; // REQUESTED, ACCEPTED, COMPLETED, CANCELLED
}