package com.hotel_management.booking_api.repository;

import com.hotel_management.booking_api.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
}