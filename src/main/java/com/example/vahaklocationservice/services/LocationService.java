package com.example.vahaklocationservice.services;

import com.example.vahaklocationservice.dto.DriverLocationDto;

import java.util.List;

public interface LocationService {

    Boolean saveDriverLocation(String driverId, Double longitude, Double latitude);

    List<DriverLocationDto> getNearByDrivers(Double longitude, Double latitude);
}
