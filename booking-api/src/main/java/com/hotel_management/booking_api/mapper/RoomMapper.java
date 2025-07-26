package com.hotel_management.booking_api.mapper;

import com.hotel_management.booking_api.dto.Room;
import com.hotel_management.booking_api.entity.RoomEntity;

public class RoomMapper {

  public static RoomEntity toEntity(Room dto) {
    if (dto == null) return null;
    return new RoomEntity(
        dto.getId(), dto.getRoomType(), dto.getCapacity(), dto.getName(), dto.getDescription());
  }

  public static Room toDto(RoomEntity entity) {
    if (entity == null) return null;
    return new Room(
        entity.getId(),
        entity.getRoomType(),
        entity.getCapacity(),
        entity.getName(),
        entity.getDescription());
  }

  public static RoomEntity fromCreateRequest(Room room) {
    RoomEntity entity = new RoomEntity();
    entity.setRoomType(room.getRoomType());
    entity.setCapacity(room.getCapacity());
    entity.setName(room.getName());
    entity.setDescription(room.getDescription());
    return entity;
  }
}
