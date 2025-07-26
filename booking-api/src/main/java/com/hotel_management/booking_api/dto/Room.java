package com.hotel_management.booking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Room {
    private Long id;
    private RoomType roomType;
    private int capacity;
    private String name;
}