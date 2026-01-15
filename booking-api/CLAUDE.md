# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.hotel_management.booking_api.services.RoomServiceTest"

# Run a single test method
./gradlew test --tests "com.hotel_management.booking_api.services.RoomServiceTest.shouldCreateAndRetrieveRoom"

# Format code (Google Java Format via Spotless)
./gradlew spotlessApply

# Check formatting
./gradlew spotlessCheck
```

## Architecture Overview

This is a Spring Boot 3.5 REST API for hotel room booking management, using Java 21.

### Domain Model
- **RoomEntity**: Hotel rooms with type (SINGLE, DOUBLE, SUITE), capacity, and name
- **CustomerEntity**: Customers with firstName, lastName, and unique email
- **ReservationEntity**: Links a room to a customer for a date range, with status tracking

### Key Business Logic
- `ReservationService.createReservation()` validates date ordering and checks for overlapping bookings before creating
- `ReservationService.getFreeIntervals()` calculates available date ranges for a room within a search period

### API Structure
- Base path: `/booking-api` (context path)
- Room endpoints: `/api/room` - CRUD operations
- Reservation endpoints: `/api/reservations` - CRUD plus `/free-intervals` query

### Database
- Production: PostgreSQL on port 7000, database `booking`
- Tests: H2 in-memory with `@ActiveProfiles("test")`

### Code Style
- Uses Lombok (`@Data`, `@RequiredArgsConstructor`) for boilerplate reduction
- Google Java Format enforced via Spotless plugin
- DTOs separate from entities with manual mappers in service classes
