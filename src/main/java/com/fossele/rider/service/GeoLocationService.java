package com.fossele.rider.service;

import com.fossele.rider.entity.GeoLocation;
import com.fossele.rider.repository.GeoLocationRepository;
import com.fossele.rider.response.VehicleLocationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static io.lettuce.core.GeoArgs.Unit.km;


@Service
@RequiredArgsConstructor
public class GeoLocationService {

    private final GeoOperations<String, String> geoOperations;
    private final GeoLocationRepository geoLocationRepository;
    public static final String vehicleLocation = "vehicle_location";

    public GeoLocation add(@Valid GeoLocation geoLocation){
        Point point = new Point(geoLocation.getLatitude(), geoLocation.getLongitude());
        geoOperations.add(vehicleLocation, point, geoLocation.getVehicleName());
        return geoLocationRepository.save(geoLocation);
    }

    public List<VehicleLocationResponse> findNearestVehicles(int longitude, int latitude) {
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands
                .GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates()
                .includeDistance().sortAscending().limit(10);

        Circle circle = new Circle(new Point(longitude, latitude), new Distance(km.ordinal(), Metrics.KILOMETERS));


        GeoResults<RedisGeoCommands.GeoLocation<String>> response = geoOperations.radius(vehicleLocation, circle, args);


        List<VehicleLocationResponse> vehicleLocationResponses = new ArrayList<>();
        response.getContent().forEach(data -> {
            vehicleLocationResponses.add(VehicleLocationResponse.builder()
                    .vehicleName(data.getContent().getName())
                    .averageDistance(data.getDistance().toString())
                    .point(data.getContent().getPoint())
                    .hash(geoOperations.hash(vehicleLocation, data.getContent().getName())).build());
        });

        return vehicleLocationResponses;
    }
}
