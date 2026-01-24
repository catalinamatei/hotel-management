package com.hotel_management.booking_api.mapper;

import com.hotel_management.booking_api.dto.ReservationResponseDTO;
import com.hotel_management.booking_api.entity.ReservationEntity;

public class ReservationMapper {

  public static ReservationResponseDTO toResponseDTO(ReservationEntity entity) {
    if (entity == null) return null;
    ReservationResponseDTO dto = new ReservationResponseDTO();
    dto.setId(entity.getId());
    dto.setRoomId(entity.getRoom().getId());
    dto.setRoomName(entity.getRoom().getName());
    dto.setCustomerId(entity.getCustomer().getId());
    dto.setCustomerName(
        entity.getCustomer().getFirstName() + " " + entity.getCustomer().getLastName());
    dto.setStartDate(entity.getStartDate());
    dto.setEndDate(entity.getEndDate());
    return dto;
  }
}
