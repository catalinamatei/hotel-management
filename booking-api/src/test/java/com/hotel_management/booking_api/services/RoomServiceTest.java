package com.hotel_management.booking_api.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.hotel_management.booking_api.dto.Room;
import com.hotel_management.booking_api.enums.RoomType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoomServiceTest {

  @Autowired private RoomService roomService;

  @Test
  void shouldCreateAndRetrieveRoom() {
    Room room = new Room(null, RoomType.SINGLE, 1, "Room 101", "Description");
    Room created = roomService.createRoom(room);

    assertThat(created.getId()).isNotNull();
    assertThat(created.getName()).isEqualTo("Room 101");

    Optional<Room> found = roomService.getRoomById(created.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("Room 101");
  }

  @Test
  void shouldUpdateRoom() {
    Room room = new Room(null, RoomType.DOUBLE, 2, "Room 102", "Description");
    Room created = roomService.createRoom(room);

    Room update = new Room(null, RoomType.DOUBLE, 3, "Room 102 Updated", "Description");
    Optional<Room> updated = roomService.updateRoom(created.getId(), update);

    assertThat(updated).isPresent();
    assertThat(updated.get().getCapacity()).isEqualTo(3);
    assertThat(updated.get().getName()).isEqualTo("Room 102 Updated");
  }

  @Test
  void shouldDeleteRoom() {
    Room room = new Room(null, RoomType.SUITE, 4, "Suite 103", "Description");
    Room created = roomService.createRoom(room);

    boolean deleted = roomService.deleteRoom(created.getId());
    assertThat(deleted).isTrue();

    Optional<Room> found = roomService.getRoomById(created.getId());
    assertThat(found).isEmpty();
  }

  @Test
  void shouldReturnAllRooms() {
    roomService.createRoom(new Room(null, RoomType.SINGLE, 1, "R201", "Description"));
    roomService.createRoom(new Room(null, RoomType.DOUBLE, 2, "R202", "Description"));

    List<Room> rooms = roomService.getAllRooms();

    assertThat(rooms).hasSizeGreaterThanOrEqualTo(2);
    assertThat(rooms).extracting(Room::getName).contains("R201", "R202");
  }
}
