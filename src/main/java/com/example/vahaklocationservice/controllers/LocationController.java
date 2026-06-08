package com.example.vahaklocationservice.controllers;

import com.example.vahaklocationservice.dto.DriverLocationDto;
import com.example.vahaklocationservice.dto.NearbyDriversRequestDTO;
import com.example.vahaklocationservice.dto.SaveDriverDetailsDto;
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
            System.out.println("Hiii");
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

    @PostMapping("/driver-details")
    public ResponseEntity<Boolean> saveDriverDetails(@RequestBody SaveDriverDetailsDto saveDriverDetailsDto){
        Boolean result = false;
        try{
            result = locationService.saveDriverDetails(saveDriverDetailsDto);
        }
        catch (Exception e){
            System.out.println("Error in saveDriverDetails Controller: "+e);
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /* Change GET -> POST, coz GET cant have body in retrofit */
    @PostMapping("/nearby/drivers")
    public ResponseEntity<List<DriverLocationDto>> getNearByDrivers(@RequestBody NearbyDriversRequestDTO nearbyDriversRequestDTO){
        try{
            // get nearby drivers
            List<DriverLocationDto> drivers = locationService.getNearByDrivers(nearbyDriversRequestDTO);

            // reserve the drivers both D1->B1 and B1->[D1, D2, ...]
            locationService.reserveTheDriver(drivers, nearbyDriversRequestDTO.getBookingId());

            return new ResponseEntity<>(drivers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
