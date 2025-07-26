package com.hotel_management.booking_api.services;

import com.hotel_management.booking_api.dto.Room;
import com.hotel_management.booking_api.entity.RoomEntity;
import com.hotel_management.booking_api.mapper.RoomMapper;
import com.hotel_management.booking_api.repository.RoomRepository;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;

  public List<Room> getAllRooms() {
    return roomRepository.findAll().stream().map(RoomMapper::toDto).collect(Collectors.toList());
  }

  public Optional<Room> getRoomById(Long id) {
    return roomRepository.findById(id).map(RoomMapper::toDto);
  }

  public Room createRoom(Room room) {
    RoomEntity saved = roomRepository.save(RoomMapper.fromCreateRequest(room));
    return RoomMapper.toDto(saved);
  }

  public Optional<Room> updateRoom(Long id, Room updatedRoom) {
    return roomRepository
        .findById(id)
        .map(
            entity -> {
              entity.setName(updatedRoom.getName());
              entity.setCapacity(updatedRoom.getCapacity());
              entity.setRoomType(updatedRoom.getRoomType());
              return RoomMapper.toDto(roomRepository.save(entity));
            });
  }

  public boolean deleteRoom(Long id) {
    if (roomRepository.existsById(id)) {
      roomRepository.deleteById(id);
      return true;
    }
    return false;
  }
}
