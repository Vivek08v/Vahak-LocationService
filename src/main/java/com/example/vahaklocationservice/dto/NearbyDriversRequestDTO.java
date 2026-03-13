package com.example.vahaklocationservice.dto;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDriversRequestDTO {
    private Double latitude;
    private Double longitude;
}
