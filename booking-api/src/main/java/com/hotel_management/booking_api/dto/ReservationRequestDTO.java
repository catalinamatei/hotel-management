package com.hotel_management.booking_api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ReservationRequestDTO {

  @NotNull private Long roomId;

  @NotNull private Long customerId;

  @NotNull @Future private LocalDate startDate;

  @NotNull @Future private LocalDate endDate;
}
