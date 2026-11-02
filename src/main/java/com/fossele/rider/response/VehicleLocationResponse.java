package com.fossele.rider.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLocationResponse {

    private String vehicleName;
    private String averageDistance;

    private Point point;
    private List<String> hash;
}
