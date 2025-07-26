package com.hotel_management.booking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Interval {
    private Long roomId;
    private LocalDate start;
    private LocalDate end;
}
