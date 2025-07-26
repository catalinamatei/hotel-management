package com.hotel_management.booking_api.enums;

public enum RoomType {
    SINGLE("Single room with one bed"),
    DOUBLE("Room with two beds"),
    SUITE("Suite with separate living area"),
    DELUXE("Deluxe room with premium amenities");

    private final String description;

    RoomType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
