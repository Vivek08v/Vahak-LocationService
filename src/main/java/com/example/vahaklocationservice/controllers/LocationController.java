package com.example.vahaklocationservice.controllers;

import com.example.vahaklocationservice.dto.DriverLocationDto;
import com.example.vahaklocationservice.dto.NearbyDriversRequestDTO;
import com.example.vahaklocationservice.dto.SaveDriverLocationRequestDto;
import com.example.vahaklocationservice.services.LocationService;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private LocationService locationService;

    public LocationController(LocationService locationService){
        this.locationService = locationService;
    }

    @PostMapping("/drivers")
    public ResponseEntity<Boolean> saveDriverLocation(@RequestBody SaveDriverLocationRequestDto saveDriverLocationRequestDto){
        try{
            Boolean response = locationService.saveDriverLocation(
                    saveDriverLocationRequestDto.getDriverId(),
                    saveDriverLocationRequestDto.getLongitude(),
                    saveDriverLocationRequestDto.getLatitude());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/nearby/drivers")
    public ResponseEntity<List<DriverLocationDto>> getNearByDrivers(@RequestBody NearbyDriversRequestDTO nearbyDriversRequestDTO){
        try{
            List<DriverLocationDto> drivers = locationService.getNearByDrivers(nearbyDriversRequestDTO.getLongitude(), nearbyDriversRequestDTO.getLatitude());

            return new ResponseEntity<>(drivers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
