package com.fossele.rider.service;

import com.fossele.rider.entity.GeoLocation;
import com.fossele.rider.entity.Ride;
import com.fossele.rider.entity.RideStatus;
import com.fossele.rider.repository.GeoLocationRepository;
import com.fossele.rider.repository.RideRepository;
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
import java.util.Optional;



@Service
@RequiredArgsConstructor
public class GeoLocationService {

    private final GeoOperations<String, String> geoOperations;
    private final GeoLocationRepository geoLocationRepository;
    private final RideRepository rideRepository;

    public static final String VEHICLE_LOCATION_KEY = "vehicle_location";

    public GeoLocation add(@Valid GeoLocation geoLocation){
        Point point = new Point(geoLocation.getLatitude(), geoLocation.getLongitude());
        geoOperations.add(VEHICLE_LOCATION_KEY, point, geoLocation.getVehicleName());
        return geoLocationRepository.save(geoLocation);
    }

    public void updateDriverLocation(String vehicleName, double longitude, double latitude) {

        Point point = new Point(longitude, latitude);
        geoOperations.add(VEHICLE_LOCATION_KEY, point, vehicleName);

        Optional<GeoLocation> optionalLocation = geoLocationRepository.findByVehicleName(vehicleName);

        GeoLocation geoLocation;

        if (optionalLocation.isPresent()) {
            geoLocation = optionalLocation.get();
            geoLocation.setLongitude(longitude);
            geoLocation.setLatitude(latitude);
        } else {
            geoLocation = GeoLocation.builder()
                    .vehicleName(vehicleName)
                    .longitude(longitude)
                    .latitude(latitude)
                    .build();
        }

        geoLocationRepository.save(geoLocation);
    }

    public void removeDriver(String vehicleName) {
        geoOperations.remove(VEHICLE_LOCATION_KEY, vehicleName);
    }

    public List<VehicleLocationResponse> findNearestVehicles(double longitude, double latitude, double radiusInKm) {
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands
                .GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates()
                .includeDistance().sortAscending().limit(10);

        Circle circle = new Circle(new Point(longitude, latitude), new Distance(radiusInKm, Metrics.KILOMETERS));

        GeoResults<RedisGeoCommands.GeoLocation<String>> response = geoOperations.radius(VEHICLE_LOCATION_KEY, circle, args);

        if (response == null ) {
            return new ArrayList<>();
        }

        List<VehicleLocationResponse> vehicleLocationResponses = new ArrayList<>();
        response.getContent().forEach(data -> {
            vehicleLocationResponses.add(VehicleLocationResponse.builder()
                    .vehicleName(data.getContent().getName())
                    .averageDistance(data.getDistance().toString())
                    .point(data.getContent().getPoint())
                    .hash(geoOperations.hash(VEHICLE_LOCATION_KEY, data.getContent().getName())).build());
        });



        return vehicleLocationResponses;
    }

    public Ride createRideRequest(String riderId, double pickupLongitude, double pickupLatitude) {

        List<VehicleLocationResponse> nearbyDrivers = findNearestVehicles(pickupLongitude, pickupLatitude, 5.0);

        if (nearbyDrivers.isEmpty()) {
            throw new RuntimeException("No available drivers found nearby.");
        }


        VehicleLocationResponse closestDriver = nearbyDrivers.get(0);
        String assignedDriver = closestDriver.getVehicleName();

        // 3. Save the ride record in PostgreSQL with the assigned driver
        Ride ride = Ride.builder()
                .riderId(riderId)
                .pickupLongitude(pickupLongitude)
                .pickupLatitude(pickupLatitude)
                .assignedDriverName(assignedDriver)
                .status(RideStatus.ACCEPTED)
                .build();


        Ride savedRide = rideRepository.save(ride);

        removeDriver(assignedDriver);

        return savedRide;
    }
}
