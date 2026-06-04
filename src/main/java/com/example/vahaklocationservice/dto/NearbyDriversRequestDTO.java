package com.example.vahaklocationservice.dto;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDriversRequestDTO {
    private Long bookingId;  // this is to reserve the drivers with bookingId
    private Double latitude;
    private Double longitude;
}
