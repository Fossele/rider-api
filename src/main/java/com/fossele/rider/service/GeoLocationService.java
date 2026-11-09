package com.fossele.rider.service;

import com.fossele.rider.entity.GeoLocation;
import com.fossele.rider.entity.Ride;
import com.fossele.rider.entity.RideStatus;
import com.fossele.rider.repository.GeoLocationRepository;
import com.fossele.rider.repository.RideRepository;
import com.fossele.rider.response.RideMetricsResponse;
import com.fossele.rider.response.VehicleLocationResponse;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
public class GeoLocationService {

    private final GeoOperations<String, String> geoOperations;
    private final GeoLocationRepository geoLocationRepository;
    private final RideRepository rideRepository;

    public GeoLocationService(RedisTemplate<String, String> redisTemplate, GeoLocationRepository geoLocationRepository, RideRepository rideRepository) {
        this.geoOperations = redisTemplate.opsForGeo();
        this.geoLocationRepository = geoLocationRepository;
        this.rideRepository = rideRepository;
    }
    public static final String VEHICLE_LOCATION_KEY = "vehicle_location";

    public GeoLocation add(@Valid GeoLocation geoLocation){
        Point point = new Point(geoLocation.getLatitude(), geoLocation.getLongitude());
        log.info("driver with id {} added", geoLocation.getId());
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

    public Ride createRideRequest(UUID riderId, double pickupLongitude, double pickupLatitude) {

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


    public RideMetricsResponse getRideMetrics(UUID rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with ID: " + rideId));

        // Optional: Calculate distance using a basic Haversine formula or a utility method
        double distanceKm = calculateDistance(
                ride.getPickupLatitude(), ride.getPickupLongitude(),
                ride.getDropoffLatitude(), ride.getDropoffLongitude()
        );

        return RideMetricsResponse.builder()
                .rideId(ride.getId())
                .riderId(ride.getRiderId())
                .assignedDriverName(ride.getAssignedDriverName())
                .status(ride.getStatus())
                .pickupLongitude(ride.getPickupLongitude())
                .pickupLatitude(ride.getPickupLatitude())
                .dropoffLongitude(ride.getDropoffLongitude())
                .dropoffLatitude(ride.getDropoffLatitude())
                .estimatedDistance(String.format("%.2f km", distanceKm))
                .build();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // The earth is a sphere so we minimize errors using this formula
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // convert to km
    }

    public Ride completeRide(UUID rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with ID: " + rideId));

        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new IllegalStateException("Ride is already finalized.");
        }

        // 1. Update ride status
        ride.setStatus(RideStatus.COMPLETED);
        Ride savedRide = rideRepository.save(ride);

        // 2. Make driver available again in Redis using their last known DB coordinates
        restoreDriverAvailability(ride.getAssignedDriverName());

        return savedRide;
    }

    public Ride cancelRide(UUID rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with ID: " + rideId));

        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel an already finalized ride.");
        }

        // 1. Update ride status
        ride.setStatus(RideStatus.CANCELLED);
        Ride savedRide = rideRepository.save(ride);

        // 2. Restore driver availability so they can take other rides
        restoreDriverAvailability(ride.getAssignedDriverName());

        return savedRide;
    }


    private void restoreDriverAvailability(String driverName) {
        if (driverName == null) return;

        // Fetch their last known position from PostgreSQL
        geoLocationRepository.findByVehicleName(driverName).ifPresent(location -> {
            Point point = new Point(location.getLongitude(), location.getLatitude());
            // Re-add to Redis geospatial index
            geoOperations.add(VEHICLE_LOCATION_KEY, point, driverName);
        });
    }



}
