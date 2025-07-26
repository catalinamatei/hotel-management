package com.hotel_management.booking_api.services;

import com.hotel_management.booking_api.dto.Room;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoomService {

    private final Map<Long, Room> rooms = new HashMap<>();
    private long nextId = 1L;

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public Optional<Room> getRoomById(Long id) {
        return Optional.ofNullable(rooms.get(id));
    }

    public Room createRoom(Room room) {
        room.setId(nextId++);
        rooms.put(room.getId(), room);
        return room;
    }

    public Optional<Room> updateRoom(Long id, Room room) {
        if (rooms.containsKey(id)) {
            room.setId(id);
            rooms.put(id, room);
            return Optional.of(room);
        }
        return Optional.empty();
    }

    public boolean deleteRoom(Long id) {
        return rooms.remove(id) != null;
    }
}
