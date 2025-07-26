package com.hotel_management.booking_api.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Interval {
  private Long roomId;
  private LocalDate start;
  private LocalDate end;
}
