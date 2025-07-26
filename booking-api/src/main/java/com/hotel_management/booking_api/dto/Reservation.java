package com.hotel_management.booking_api.dto;

import com.hotel_management.booking_api.enums.ReservationStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Reservation {

  private Long id;
  private Room room;
  private ReservationStatus reservationStatus;
  private int customerId;
  private LocalDate startDate;
  private LocalDate endDate;
}
