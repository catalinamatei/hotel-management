package com.hotel_management.booking_api.dto;

import com.hotel_management.booking_api.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@AllArgsConstructor
@Data
public class Room {
    private Long id;
    private RoomType roomType;
    private int capacity;
    private String name;
    private String description;
}