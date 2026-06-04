package com.example.vahaklocationservice.services;

import com.example.vahaklocationservice.dto.DriverLocationDto;
import com.example.vahaklocationservice.dto.NearbyDriversRequestDTO;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class LocationServiceImpl implements LocationService{

    private final StringRedisTemplate stringRedisTemplate;

    private final String DRIVER_GEO_OPS_KEY = "drivers";

    private static final Double SEARCH_RADIUS = 1.725;

    public LocationServiceImpl(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Boolean saveDriverLocation(String driverId, Double longitude, Double latitude) {
        try{
            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
            geoOps.add(
                    DRIVER_GEO_OPS_KEY,
                    new RedisGeoCommands.GeoLocation<>(driverId, new Point(longitude, latitude))
            );
            return true;
        }
        catch (Exception e){
            System.out.println(e);
        }
        return false;
    }

    @Override
    public List<DriverLocationDto> getNearByDrivers(NearbyDriversRequestDTO nearbyDriversRequestDTO) {

        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
        Distance radius = new Distance(SEARCH_RADIUS, Metrics.KILOMETERS);
        Circle within = new Circle(new Point(nearbyDriversRequestDTO.getLongitude(), nearbyDriversRequestDTO.getLatitude()), radius);

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius(DRIVER_GEO_OPS_KEY, within);

        List<DriverLocationDto> drivers = new ArrayList<>();
        for(GeoResult<RedisGeoCommands.GeoLocation<String>> result : results){
            Boolean wasReserved = reserveTheDriver(Long.valueOf(result.getContent().getName()), nearbyDriversRequestDTO.getBookingId());
            if(!wasReserved) continue;

            Point point = geoOps.position(DRIVER_GEO_OPS_KEY, result.getContent().getName()).getFirst();
            DriverLocationDto driverLocation = DriverLocationDto.builder()
                    .driverId(result.getContent().getName())
                    .longitude(point.getX())
                    .latitude(point.getY())
                    .build();
            drivers.add(driverLocation);
        }
        return drivers;
    }

    public Boolean reserveTheDriver(Long driverId, Long bookingId){
        Boolean success =
                stringRedisTemplate.opsForValue()
                        .setIfAbsent(
                                "reservation:driver:"+driverId.toString(),
                                bookingId.toString(),
                                Duration.ofSeconds(60)
                        );
        return success;
    }
}
