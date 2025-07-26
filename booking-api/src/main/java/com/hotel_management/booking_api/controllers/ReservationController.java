package com.hotel_management.booking_api.controllers;

import com.hotel_management.booking_api.dto.Interval;
import com.hotel_management.booking_api.dto.ReservationRequestDTO;
import com.hotel_management.booking_api.dto.ReservationResponseDTO;
import com.hotel_management.booking_api.services.ReservationService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @PostMapping
  public ResponseEntity<ReservationResponseDTO> createReservation(
      @Valid @RequestBody ReservationRequestDTO request) {
    ReservationResponseDTO response = reservationService.createReservation(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ReservationResponseDTO> getReservationById(@PathVariable Long id) {
    ReservationResponseDTO response = reservationService.getReservationById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
    List<ReservationResponseDTO> reservations = reservationService.getAllReservations();
    return ResponseEntity.ok(reservations);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
    reservationService.deleteReservation(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/free-intervals")
  public ResponseEntity<List<Interval>> getFreeIntervals(
      @RequestParam Long roomId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
    List<Interval> freeIntervals = reservationService.getFreeIntervals(roomId, start, end);
    return ResponseEntity.ok(freeIntervals);
  }
}
