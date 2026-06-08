package com.example.vahaklocationservice.services;

import com.example.vahakentityservice.models.BookingStatus;
import com.example.vahaklocationservice.dto.DriverLocationDto;
import com.example.vahaklocationservice.dto.NearbyDriversRequestDTO;
import com.example.vahaklocationservice.dto.SaveDriverDetailsDto;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class LocationServiceImpl implements LocationService{

    private final StringRedisTemplate stringRedisTemplate;

    private final String DRIVER_GEO_OPS_KEY = "location:drivers";

    private static final Double SEARCH_RADIUS = 10.0;

    public LocationServiceImpl(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Boolean saveDriverDetails(SaveDriverDetailsDto saveDriverDetailsDto) {
        // this will store details like:
        // status
        // vehicle: Bike/Car
        // rating (maybe)
        try{
            HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();
            hashOps.put(
                    "location:driver-details:"+saveDriverDetailsDto.getDriverId().toString(),
                    "status",
                    saveDriverDetailsDto.getDriverStatus()
            );
        }
        catch (Exception e){
            System.out.println("Error in saveDriverDetails: " + e);
            return false;
        }
        return true;
    }

    @Override
    public Boolean saveDriverLocation(String driverId, Double longitude, Double latitude) {
        try{
            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
            geoOps.add(
                    DRIVER_GEO_OPS_KEY,
                    new RedisGeoCommands.GeoLocation<>(driverId, new Point(longitude, latitude))
            );
        }
        catch (Exception e){
            System.out.println("Error in saveDriverLocation: "+e);
            return false;
        }
        return true;
    }

    @Override
    public List<DriverLocationDto> getNearByDrivers(NearbyDriversRequestDTO nearbyDriversRequestDTO) {
        List<DriverLocationDto> drivers = new ArrayList<>();
        try{
            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
            Distance radius = new Distance(SEARCH_RADIUS, Metrics.KILOMETERS);
            Circle within = new Circle(new Point(nearbyDriversRequestDTO.getLongitude(), nearbyDriversRequestDTO.getLatitude()), radius);

            GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius(DRIVER_GEO_OPS_KEY, within);

            for(GeoResult<RedisGeoCommands.GeoLocation<String>> result : results){
                // filter if already reserved or ON_RIDE/RESERVED (Status)
                // -> actually reserved is being checked below
                // continue if not scheduled
                String driverId = result.getContent().getName();
                HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();

                Boolean resExists = stringRedisTemplate.hasKey("location:reservation:driver:"+driverId);
                String status = hashOps.get("location:driver-details:"+driverId, "status");
                if(resExists || !"AVAILABLE".equals(status)) continue;

                Point point = geoOps.position(DRIVER_GEO_OPS_KEY, result.getContent().getName()).getFirst();
                DriverLocationDto driverLocation = DriverLocationDto.builder()
                        .driverId(result.getContent().getName())
                        .longitude(point.getX())
                        .latitude(point.getY())
                        .build();
                drivers.add(driverLocation);
            }
        } catch (Exception e) {
            System.out.println("Error in getNearByDrivers: "+ e);
            return null;
        }
        return drivers;
    }

    @Override
    public Boolean reserveTheDriver(List<DriverLocationDto> drivers, Long bookingId){
        if(drivers.isEmpty()) return false;
        Boolean atLeastOneReserved = false;
        try{
            for(DriverLocationDto driver: drivers){
                Long driverId = Long.valueOf(driver.getDriverId());

                Boolean reserved =
                        stringRedisTemplate.opsForValue()
                                .setIfAbsent(
                                        "location:reservation:driver:"+driverId.toString(),
                                        bookingId.toString(),
                                        Duration.ofSeconds(60)
                                );

                if(reserved){
                    atLeastOneReserved = true;
                    stringRedisTemplate.opsForSet().add(
                            "location:reservation:booking:" + bookingId,
                            driverId.toString()
                    );
                }
            }
            stringRedisTemplate.expire(
                    "location:reservation:booking:" + bookingId,
                    Duration.ofSeconds(60)
            );
        }
        catch (Exception e){
            System.out.println("Error in reserveTheDrivers: " + e);
            return false;
        }
        return atLeastOneReserved;
    }

    @Override
    public Boolean unreserveTheDriver(Long driverId, Long bookingId){

        try{
            Set<String> reservedDrivers = stringRedisTemplate.opsForSet()
                    .members("location:reservation:booking:" + bookingId);
            stringRedisTemplate.delete("location:reservation:booking:" + bookingId);

            if(reservedDrivers==null) {
                System.out.println("reserveDrivers array is null");
                return false;
            }
            for(String resDriverId: reservedDrivers){
                stringRedisTemplate.delete("location:reservation:driver:"+resDriverId);
            }

            HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();
            hashOps.put(
                    "location:driver-details:"+driverId.toString(),
                    "status",
                    "IN_RIDE"
            );
        } catch (Exception e) {
            System.out.println("Error in unreserveTheDrivers: "+e);
            return false;
        }
        return true;
    }
}
