package com.hotel_management.booking_api.repository;

import com.hotel_management.booking_api.entity.ReservationEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
  List<ReservationEntity> findByRoomIdAndEndDateAfterAndStartDateBefore(
      Long roomId, LocalDate startDate, LocalDate endDate);

  List<ReservationEntity>
      findByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(
          Long roomId, LocalDate endDate, LocalDate startDate);
}
