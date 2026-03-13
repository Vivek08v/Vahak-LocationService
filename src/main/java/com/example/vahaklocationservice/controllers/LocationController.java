package com.example.vahaklocationservice.controllers;

import com.example.vahaklocationservice.dto.NearbyDriversRequestDTO;
import com.example.vahaklocationservice.dto.SaveDriverLocationRequestDto;
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

    private final StringRedisTemplate stringRedisTemplate;

    private final String DRIVER_GEO_OPS_KEY = "drivers";

    private static final Double SEARCH_RADIUS = 1.725;

    public LocationController(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostMapping("/drivers")
    public ResponseEntity<Boolean> saveDriverLocation(@RequestBody SaveDriverLocationRequestDto saveDriverLocationRequestDto){
        try{
            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
            geoOps.add(
                    DRIVER_GEO_OPS_KEY,
                    new RedisGeoCommands.GeoLocation<>(
                            saveDriverLocationRequestDto.getDriverId(),
                            new Point(
                                    saveDriverLocationRequestDto.getLongitude(),
                                    saveDriverLocationRequestDto.getLatitude()
                            )
                    )
            );
            return new ResponseEntity<>(true, HttpStatus.CREATED);
        }
        catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/nearby/drivers")
    public ResponseEntity<List<String>> getNearByDrivers(@RequestBody NearbyDriversRequestDTO nearbyDriversRequestDTO){
        try{
            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
            Distance radius = new Distance(SEARCH_RADIUS, Metrics.KILOMETERS);
            Circle within = new Circle(
                    new Point(
                            nearbyDriversRequestDTO.getLongitude(),
                            nearbyDriversRequestDTO.getLatitude()
                    ),
                    radius
            );

            GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius(DRIVER_GEO_OPS_KEY, within);
            List<String> drivers = new ArrayList<>();
            for(GeoResult<RedisGeoCommands.GeoLocation<String>> result : results){
                drivers.add(result.getContent().getName());
            }
            return new ResponseEntity<>(drivers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
