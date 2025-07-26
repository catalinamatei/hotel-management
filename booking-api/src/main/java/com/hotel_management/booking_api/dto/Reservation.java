package com.hotel_management.booking_api.dto;

import com.hotel_management.booking_api.entity.RoomEntity;
import com.hotel_management.booking_api.enums.ReservationStatus;
import com.hotel_management.booking_api.enums.RoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

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