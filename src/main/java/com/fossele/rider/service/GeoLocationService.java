package com.fossele.rider.service;

import com.fossele.rider.entity.GeoLocation;
import com.fossele.rider.repository.GeoLocationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.stereotype.Service;

import java.awt.*;


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
}
