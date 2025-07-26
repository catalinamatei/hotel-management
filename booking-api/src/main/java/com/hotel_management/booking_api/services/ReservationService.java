package com.hotel_management.booking_api.services;

import com.hotel_management.booking_api.dto.Interval;
import com.hotel_management.booking_api.dto.ReservationRequestDTO;
import com.hotel_management.booking_api.dto.ReservationResponseDTO;
import com.hotel_management.booking_api.entity.CustomerEntity;
import com.hotel_management.booking_api.entity.ReservationEntity;
import com.hotel_management.booking_api.entity.RoomEntity;
import com.hotel_management.booking_api.enums.ReservationStatus;
import com.hotel_management.booking_api.repository.CustomerRepository;
import com.hotel_management.booking_api.repository.ReservationRepository;
import com.hotel_management.booking_api.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;

    public ReservationResponseDTO createReservation(ReservationRequestDTO request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        RoomEntity room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found with ID: " + request.getRoomId()));

        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + request.getCustomerId()));

        boolean hasOverlap = !reservationRepository
                .findByRoomIdAndEndDateAfterAndStartDateBefore(room.getId(), request.getStartDate(), request.getEndDate())
                .isEmpty();

        if (hasOverlap) {
            throw new IllegalStateException("Room is already booked for the selected dates");
        }

        ReservationEntity reservation = new ReservationEntity();
        reservation.setRoom(room);
        reservation.setCustomer(customer);
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setReservationStatus(ReservationStatus.BOOKED);

        ReservationEntity saved = reservationRepository.save(reservation);
        return mapToResponseDTO(saved);
    }

    public ReservationResponseDTO getReservationById(Long id) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + id));
        return mapToResponseDTO(reservation);
    }

    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new EntityNotFoundException("Reservation not found with ID: " + id);
        }
        reservationRepository.deleteById(id);
    }

    public List<Interval> getFreeIntervals(Long roomId, LocalDate searchStart, LocalDate searchEnd) {
        if (searchEnd.isBefore(searchStart)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        List<ReservationEntity> reservations = reservationRepository
                .findByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(
                        roomId, searchEnd, searchStart
                );

        List<Interval> freeIntervals = new ArrayList<>();

        LocalDate availableFrom = searchStart;

        for (ReservationEntity reservation : reservations) {
            if (availableFrom.isBefore(reservation.getStartDate())) {
                freeIntervals.add(new Interval(roomId, availableFrom, reservation.getStartDate()));
            }
            availableFrom = reservation.getEndDate().isAfter(availableFrom) ? reservation.getEndDate() : availableFrom;
        }

        if (availableFrom.isBefore(searchEnd)) {
            freeIntervals.add(new Interval(roomId, availableFrom, searchEnd));
        }

        return freeIntervals;
    }

    private ReservationResponseDTO mapToResponseDTO(ReservationEntity entity) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(entity.getId());
        dto.setRoomId(entity.getRoom().getId());
        dto.setRoomName(entity.getRoom().getName());
        dto.setCustomerId(entity.getCustomer().getId());
        dto.setCustomerName(entity.getCustomer().getFirstName() + " " + entity.getCustomer().getLastName());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        return dto;
    }
}
