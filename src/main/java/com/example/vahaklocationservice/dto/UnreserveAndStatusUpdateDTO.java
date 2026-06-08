package com.example.vahaklocationservice.dto;

import com.example.vahakentityservice.models.BookingStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnreserveAndStatusUpdateDTO {

    private Long driverId;

    private BookingStatus bookingStatus;

    private Long bookingId;
}
