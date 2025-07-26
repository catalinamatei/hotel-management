package com.hotel_management.booking_api.enums;

public enum ReservationStatus {
  BOOKED("Room is booked by customer"),
  CANCELED("Customer canceled the reservation"),
  FINALIZE("The booking of the room is finalized");

  private final String description;

  ReservationStatus(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
