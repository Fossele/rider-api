package com.fossele.rider.dto;


import lombok.Data;

@Data
public class DriverPingRequest {
    private String vehicleName;
    private double longitude;
    private double latitude;
}
