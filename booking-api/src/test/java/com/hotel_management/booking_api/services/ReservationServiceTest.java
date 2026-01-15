package com.hotel_management.booking_api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.hotel_management.booking_api.dto.Interval;
import com.hotel_management.booking_api.dto.ReservationRequestDTO;
import com.hotel_management.booking_api.dto.ReservationResponseDTO;
import com.hotel_management.booking_api.entity.CustomerEntity;
import com.hotel_management.booking_api.entity.ReservationEntity;
import com.hotel_management.booking_api.entity.RoomEntity;
import com.hotel_management.booking_api.enums.ReservationStatus;
import com.hotel_management.booking_api.enums.RoomType;
import com.hotel_management.booking_api.repository.CustomerRepository;
import com.hotel_management.booking_api.repository.ReservationRepository;
import com.hotel_management.booking_api.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

  @Mock private ReservationRepository reservationRepository;

  @Mock private RoomRepository roomRepository;

  @Mock private CustomerRepository customerRepository;

  @InjectMocks private ReservationService reservationService;

  private RoomEntity room;
  private CustomerEntity customer;
  private ReservationEntity reservation;
  private ReservationRequestDTO request;

  @BeforeEach
  void setUp() {
    room = new RoomEntity();
    room.setId(1L);
    room.setName("Room 101");
    room.setRoomType(RoomType.SINGLE);
    room.setCapacity(1);

    customer = new CustomerEntity();
    customer.setId(1L);
    customer.setFirstName("John");
    customer.setLastName("Doe");
    customer.setEmail("john@example.com");
    customer.setPassword("encoded");

    reservation = new ReservationEntity();
    reservation.setId(1L);
    reservation.setRoom(room);
    reservation.setCustomer(customer);
    reservation.setStartDate(LocalDate.of(2026, 2, 1));
    reservation.setEndDate(LocalDate.of(2026, 2, 5));
    reservation.setReservationStatus(ReservationStatus.BOOKED);

    request = new ReservationRequestDTO();
    request.setRoomId(1L);
    request.setCustomerId(1L);
    request.setStartDate(LocalDate.of(2026, 2, 1));
    request.setEndDate(LocalDate.of(2026, 2, 5));
  }

  @Test
  void createReservation_withValidData_returnsReservation() {
    when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(reservationRepository.findByRoomIdAndEndDateAfterAndStartDateBefore(
            anyLong(), any(), any()))
        .thenReturn(Collections.emptyList());
    when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(reservation);

    ReservationResponseDTO response = reservationService.createReservation(request);

    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getRoomId()).isEqualTo(1L);
    assertThat(response.getRoomName()).isEqualTo("Room 101");
    assertThat(response.getCustomerName()).isEqualTo("John Doe");
  }

  @Test
  void createReservation_withEndDateBeforeStart_throwsException() {
    request.setStartDate(LocalDate.of(2026, 2, 5));
    request.setEndDate(LocalDate.of(2026, 2, 1));

    assertThatThrownBy(() -> reservationService.createReservation(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("End date must be after start date");
  }

  @Test
  void createReservation_withNonExistentRoom_throwsException() {
    when(roomRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> reservationService.createReservation(request))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Room not found");
  }

  @Test
  void createReservation_withNonExistentCustomer_throwsException() {
    when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
    when(customerRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> reservationService.createReservation(request))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Customer not found");
  }

  @Test
  void createReservation_withOverlappingDates_throwsException() {
    when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(reservationRepository.findByRoomIdAndEndDateAfterAndStartDateBefore(
            anyLong(), any(), any()))
        .thenReturn(List.of(reservation));

    assertThatThrownBy(() -> reservationService.createReservation(request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Room is already booked for the selected dates");
  }

  @Test
  void getReservationById_existingId_returnsReservation() {
    when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

    ReservationResponseDTO response = reservationService.getReservationById(1L);

    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getRoomName()).isEqualTo("Room 101");
  }

  @Test
  void getReservationById_nonExistentId_throwsException() {
    when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> reservationService.getReservationById(99L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Reservation not found");
  }

  @Test
  void getAllReservations_returnsAllReservations() {
    when(reservationRepository.findAll()).thenReturn(List.of(reservation));

    List<ReservationResponseDTO> result = reservationService.getAllReservations();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getRoomName()).isEqualTo("Room 101");
  }

  @Test
  void deleteReservation_existingId_deletesReservation() {
    when(reservationRepository.existsById(1L)).thenReturn(true);

    reservationService.deleteReservation(1L);

    verify(reservationRepository).deleteById(1L);
  }

  @Test
  void deleteReservation_nonExistentId_throwsException() {
    when(reservationRepository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> reservationService.deleteReservation(99L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Reservation not found");
  }

  @Test
  void getFreeIntervals_withNoReservations_returnsFullInterval() {
    LocalDate start = LocalDate.of(2026, 3, 1);
    LocalDate end = LocalDate.of(2026, 3, 10);

    when(reservationRepository
            .findByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(
                anyLong(), any(), any()))
        .thenReturn(Collections.emptyList());

    List<Interval> intervals = reservationService.getFreeIntervals(1L, start, end);

    assertThat(intervals).hasSize(1);
    assertThat(intervals.get(0).getStart()).isEqualTo(start);
    assertThat(intervals.get(0).getEnd()).isEqualTo(end);
  }

  @Test
  void getFreeIntervals_withEndBeforeStart_throwsException() {
    LocalDate start = LocalDate.of(2026, 3, 10);
    LocalDate end = LocalDate.of(2026, 3, 1);

    assertThatThrownBy(() -> reservationService.getFreeIntervals(1L, start, end))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("End date must be after start date");
  }
}
