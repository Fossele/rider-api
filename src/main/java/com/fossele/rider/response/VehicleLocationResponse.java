package com.fossele.rider.response;


import lombok.*;
import org.springframework.data.geo.Point;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class VehicleLocationResponse {

    private String vehicleName;
    private String averageDistance;

    private Point point;
    private List<String> hash;

}
