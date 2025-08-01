package com.hotel_management.booking_api.controllers;

import com.hotel_management.booking_api.dto.Room;
import com.hotel_management.booking_api.services.RoomService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
  @Autowired private final RoomService roomService;

  @GetMapping
  public List<Room> getAllRooms() {
    return roomService.getAllRooms();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
    return roomService
        .getRoomById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Room> createRoom(@RequestBody Room room) {
    return ResponseEntity.ok(roomService.createRoom(room));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room room) {
    return roomService
        .updateRoom(id, room)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
    if (roomService.deleteRoom(id)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}
