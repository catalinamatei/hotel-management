package com.hotel_management.booking_api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hotel_management.booking_api.config.TestcontainersConfiguration;
import com.hotel_management.booking_api.dto.Interval;
import com.hotel_management.booking_api.dto.ReservationRequestDTO;
import com.hotel_management.booking_api.dto.ReservationResponseDTO;
import com.hotel_management.booking_api.dto.Room;
import com.hotel_management.booking_api.entity.CustomerEntity;
import com.hotel_management.booking_api.enums.RoomType;
import com.hotel_management.booking_api.repository.CustomerRepository;
import com.hotel_management.booking_api.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("integration")
@Transactional
class ReservationServiceIntegrationTest {

  @Autowired private ReservationService reservationService;

  @Autowired private RoomService roomService;

  @Autowired private CustomerRepository customerRepository;

  @Autowired private ReservationRepository reservationRepository;

  private Long roomId;
  private Long customerId;

  @BeforeEach
  void setUp() {
    reservationRepository.deleteAll();
    customerRepository.deleteAll();

    Room room = roomService.createRoom(new Room(null, RoomType.SINGLE, 1, "Room 101", "Test room"));
    roomId = room.getId();

    CustomerEntity customer = new CustomerEntity();
    customer.setFirstName("John");
    customer.setLastName("Doe");
    customer.setEmail("john@example.com");
    customer.setPassword("encoded");
    customer = customerRepository.save(customer);
    customerId = customer.getId();
  }

  @Test
  void createReservation_withValidData_createsReservation() {
    ReservationRequestDTO request = new ReservationRequestDTO();
    request.setRoomId(roomId);
    request.setCustomerId(customerId);
    request.setStartDate(LocalDate.of(2026, 3, 1));
    request.setEndDate(LocalDate.of(2026, 3, 5));

    ReservationResponseDTO response = reservationService.createReservation(request);

    assertThat(response.getId()).isNotNull();
    assertThat(response.getRoomId()).isEqualTo(roomId);
    assertThat(response.getRoomName()).isEqualTo("Room 101");
    assertThat(response.getCustomerName()).isEqualTo("John Doe");
    assertThat(response.getStartDate()).isEqualTo(LocalDate.of(2026, 3, 1));
    assertThat(response.getEndDate()).isEqualTo(LocalDate.of(2026, 3, 5));
  }

  @Test
  void createReservation_withOverlappingDates_throwsException() {
    ReservationRequestDTO first = new ReservationRequestDTO();
    first.setRoomId(roomId);
    first.setCustomerId(customerId);
    first.setStartDate(LocalDate.of(2026, 3, 1));
    first.setEndDate(LocalDate.of(2026, 3, 10));
    reservationService.createReservation(first);

    ReservationRequestDTO overlapping = new ReservationRequestDTO();
    overlapping.setRoomId(roomId);
    overlapping.setCustomerId(customerId);
    overlapping.setStartDate(LocalDate.of(2026, 3, 5));
    overlapping.setEndDate(LocalDate.of(2026, 3, 15));

    assertThatThrownBy(() -> reservationService.createReservation(overlapping))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Room is already booked for the selected dates");
  }

  @Test
  void createReservation_withNonOverlappingDates_succeeds() {
    ReservationRequestDTO first = new ReservationRequestDTO();
    first.setRoomId(roomId);
    first.setCustomerId(customerId);
    first.setStartDate(LocalDate.of(2026, 3, 1));
    first.setEndDate(LocalDate.of(2026, 3, 5));
    reservationService.createReservation(first);

    ReservationRequestDTO second = new ReservationRequestDTO();
    second.setRoomId(roomId);
    second.setCustomerId(customerId);
    second.setStartDate(LocalDate.of(2026, 3, 10));
    second.setEndDate(LocalDate.of(2026, 3, 15));

    ReservationResponseDTO response = reservationService.createReservation(second);
    assertThat(response.getId()).isNotNull();
  }

  @Test
  void getReservationById_existingReservation_returnsReservation() {
    ReservationRequestDTO request = new ReservationRequestDTO();
    request.setRoomId(roomId);
    request.setCustomerId(customerId);
    request.setStartDate(LocalDate.of(2026, 4, 1));
    request.setEndDate(LocalDate.of(2026, 4, 5));
    ReservationResponseDTO created = reservationService.createReservation(request);

    ReservationResponseDTO found = reservationService.getReservationById(created.getId());

    assertThat(found.getId()).isEqualTo(created.getId());
    assertThat(found.getRoomName()).isEqualTo("Room 101");
  }

  @Test
  void getReservationById_nonExistent_throwsException() {
    assertThatThrownBy(() -> reservationService.getReservationById(99999L))
        .isInstanceOf(EntityNotFoundException.class);
  }

  @Test
  void getAllReservations_returnsAllReservations() {
    ReservationRequestDTO request1 = new ReservationRequestDTO();
    request1.setRoomId(roomId);
    request1.setCustomerId(customerId);
    request1.setStartDate(LocalDate.of(2026, 5, 1));
    request1.setEndDate(LocalDate.of(2026, 5, 5));
    reservationService.createReservation(request1);

    ReservationRequestDTO request2 = new ReservationRequestDTO();
    request2.setRoomId(roomId);
    request2.setCustomerId(customerId);
    request2.setStartDate(LocalDate.of(2026, 5, 10));
    request2.setEndDate(LocalDate.of(2026, 5, 15));
    reservationService.createReservation(request2);

    List<ReservationResponseDTO> all = reservationService.getAllReservations();

    assertThat(all).hasSize(2);
  }

  @Test
  void deleteReservation_existingReservation_deletesIt() {
    ReservationRequestDTO request = new ReservationRequestDTO();
    request.setRoomId(roomId);
    request.setCustomerId(customerId);
    request.setStartDate(LocalDate.of(2026, 6, 1));
    request.setEndDate(LocalDate.of(2026, 6, 5));
    ReservationResponseDTO created = reservationService.createReservation(request);

    reservationService.deleteReservation(created.getId());

    assertThatThrownBy(() -> reservationService.getReservationById(created.getId()))
        .isInstanceOf(EntityNotFoundException.class);
  }

  @Test
  void getFreeIntervals_withNoReservations_returnsFullRange() {
    List<Interval> intervals =
        reservationService.getFreeIntervals(
            roomId, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31));

    assertThat(intervals).hasSize(1);
    assertThat(intervals.get(0).getStart()).isEqualTo(LocalDate.of(2026, 7, 1));
    assertThat(intervals.get(0).getEnd()).isEqualTo(LocalDate.of(2026, 7, 31));
  }

  @Test
  void getFreeIntervals_withReservation_returnsGaps() {
    ReservationRequestDTO request = new ReservationRequestDTO();
    request.setRoomId(roomId);
    request.setCustomerId(customerId);
    request.setStartDate(LocalDate.of(2026, 8, 10));
    request.setEndDate(LocalDate.of(2026, 8, 15));
    reservationService.createReservation(request);

    List<Interval> intervals =
        reservationService.getFreeIntervals(
            roomId, LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31));

    assertThat(intervals).hasSize(2);
    assertThat(intervals.get(0).getStart()).isEqualTo(LocalDate.of(2026, 8, 1));
    assertThat(intervals.get(0).getEnd()).isEqualTo(LocalDate.of(2026, 8, 10));
    assertThat(intervals.get(1).getStart()).isEqualTo(LocalDate.of(2026, 8, 15));
    assertThat(intervals.get(1).getEnd()).isEqualTo(LocalDate.of(2026, 8, 31));
  }
}
