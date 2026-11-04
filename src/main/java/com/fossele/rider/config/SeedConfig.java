package com.fossele.rider.config;

import com.fossele.rider.entity.GeoLocation;
import com.fossele.rider.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class SeedConfig implements CommandLineRunner {

    private final GeoLocationService geoLocationService;

    @Override
    public void run(String @NonNull ... args) throws  Exception{
        geoLocationService.add(GeoLocation.builder()
                .latitude(3.4322)
                .latitude(5.3344)
                .vehicleName("Taxi west")
                .build());

        geoLocationService.add(GeoLocation.builder()
                .latitude(5.2499)
                .latitude(7.9342)
                .vehicleName("Taxi east")
                .build());

        geoLocationService.add(GeoLocation.builder()
                .latitude(1.47749)
                .latitude(5.48282)
                .vehicleName("Taxi north")
                .build());
    }
}
