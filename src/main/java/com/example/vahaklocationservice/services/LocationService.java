package com.example.vahaklocationservice.services;

import com.example.vahaklocationservice.dto.DriverLocationDto;
import com.example.vahaklocationservice.dto.NearbyDriversRequestDTO;

import java.util.List;

public interface LocationService {

    Boolean saveDriverLocation(String driverId, Double longitude, Double latitude);

    List<DriverLocationDto> getNearByDrivers(NearbyDriversRequestDTO nearbyDriversRequestDTO);
}
