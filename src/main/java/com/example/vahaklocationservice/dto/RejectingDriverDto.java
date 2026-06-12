package com.example.vahaklocationservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectingDriverDto {

    private Long bookingId;

    private Long driverId;
}
