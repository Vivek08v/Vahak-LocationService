package com.example.vahaklocationservice.dto;

import com.example.vahakentityservice.models.BookingStatus;
import lombok.*;
import org.springframework.data.annotation.AccessType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveDriverDetailsDto {
    private Long driverId;
    private String driverStatus;
    // add vehicle Type etc later
}
