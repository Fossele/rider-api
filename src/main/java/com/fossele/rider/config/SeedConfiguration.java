package com.fossele.rider.config;

import com.fossele.rider.entity.GeoLocation;
import com.fossele.rider.entity.User;
import com.fossele.rider.service.GeoLocationService;
import com.fossele.rider.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class SeedConfiguration implements CommandLineRunner {

    private final GeoLocationService geoLocationService;
    private final UserService userService;

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

        geoLocationService.add(
          GeoLocation.builder()
                  .longitude(2.3328)
                  .latitude(2.3456)
                  .vehicleName("Taxi centre")
                  .build()
        );

        userService.createUser(User.builder()
                        .name("rachel")
                        .email("rachel@gmail.com")
                .build());

        userService.createUser(User.builder()
                .name("rapy")
                .email("rapy@gmail.com")
                .build());
    }
}
