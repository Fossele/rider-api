package com.fossele.rider.controller;

import com.fossele.rider.dto.DriverPingRequest;
import com.fossele.rider.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DriverWebSocketController {

    private final GeoLocationService geoLocationService;

    @MessageMapping("/driver.ping")
    public void handleDriverPing(@Payload DriverPingRequest request) {

        geoLocationService.updateDriverLocation(
                request.getVehicleName(),
                request.getLongitude(),
                request.getLatitude()
        );
    }


}
