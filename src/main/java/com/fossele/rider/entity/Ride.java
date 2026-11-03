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

    private double pickupLongitude;
    private double pickupLatitude;
    private String riderId;
    private String assignedDriverName;

    @Enumerated(EnumType.STRING)
    private RideStatus status; // REQUESTED, ACCEPTED, COMPLETED, CANCELLED

}