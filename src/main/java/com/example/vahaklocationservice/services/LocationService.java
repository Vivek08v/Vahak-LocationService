package com.example.vahaklocationservice.services;

import com.example.vahaklocationservice.dto.DriverLocationDto;
import com.example.vahaklocationservice.dto.NearbyDriversRequestDTO;
import com.example.vahaklocationservice.dto.SaveDriverDetailsDto;

import java.util.List;

public interface LocationService {

    Boolean saveDriverLocation(String driverId, Double longitude, Double latitude);

    List<DriverLocationDto> getNearByDrivers(NearbyDriversRequestDTO nearbyDriversRequestDTO);

    Boolean saveDriverDetails(SaveDriverDetailsDto saveDriverDetailsDto);

    Boolean reserveTheDriver(List<DriverLocationDto> drivers, Long bookingId);

    Boolean unreserveTheDriver(Long driverId, Long bookingId);
}
