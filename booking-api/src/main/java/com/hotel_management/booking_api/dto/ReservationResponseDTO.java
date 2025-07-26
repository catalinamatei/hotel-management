package com.hotel_management.booking_api.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ReservationResponseDTO {

  private Long id;
  private Long roomId;
  private String roomName;
  private Long customerId;
  private String customerName;
  private LocalDate startDate;
  private LocalDate endDate;
}
