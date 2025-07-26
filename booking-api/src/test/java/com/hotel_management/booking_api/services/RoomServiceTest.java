package com.hotel_management.booking_api.services;

import com.hotel_management.booking_api.dto.Room;
import com.hotel_management.booking_api.dto.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class RoomServiceTest {

    private RoomService roomService;

    @BeforeEach
    void setUp() {
        roomService = new RoomService();
    }

    @Test
    void shouldCreateRoomSuccessfully() {
        Room room = new Room(null, RoomType.SINGLE, 1, "Room 101");
        Room created = roomService.createRoom(room);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getRoomType()).isEqualTo(RoomType.SINGLE);
        assertThat(created.getCapacity()).isEqualTo(1);
        assertThat(created.getName()).isEqualTo("Room 101");
    }

    @Test
    void shouldRetrieveRoomById() {
        Room created = roomService.createRoom(new Room(null, RoomType.DOUBLE, 2, "Room 102"));
        Optional<Room> found = roomService.getRoomById(created.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Room 102");
    }

    @Test
    void shouldUpdateRoom() {
        Room original = roomService.createRoom(new Room(null, RoomType.SUITE, 3, "Suite 103"));
        Room update = new Room(null, RoomType.SUITE, 4, "Updated Suite 103");

        Optional<Room> updated = roomService.updateRoom(original.getId(), update);

        assertThat(updated).isPresent();
        assertThat(updated.get().getCapacity()).isEqualTo(4);
        assertThat(updated.get().getName()).isEqualTo("Updated Suite 103");
    }

    @Test
    void shouldDeleteRoom() {
        Room room = roomService.createRoom(new Room(null, RoomType.DELUXE, 2, "Deluxe 104"));
        boolean deleted = roomService.deleteRoom(room.getId());

        assertThat(deleted).isTrue();
        assertThat(roomService.getRoomById(room.getId())).isEmpty();
    }

    @Test
    void shouldReturnAllRooms() {
        roomService.createRoom(new Room(null, RoomType.SINGLE, 1, "R201"));
        roomService.createRoom(new Room(null, RoomType.DOUBLE, 2, "R202"));

        assertThat(roomService.getAllRooms()).hasSize(2);
    }
}
