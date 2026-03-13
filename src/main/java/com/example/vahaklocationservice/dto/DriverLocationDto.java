package com.example.vahaklocationservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationDto {
    private String driverId;
    private Double latitude;
    private Double longitude;
}
