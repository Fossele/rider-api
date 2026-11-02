package com.fossele.rider.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "geo_location")
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Setter
@Getter
public class GeoLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int longitude;
    private int latitude;
    private String vehicleName;
}
