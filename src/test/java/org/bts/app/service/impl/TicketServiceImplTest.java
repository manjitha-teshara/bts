package org.bts.app.service.impl;

import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.ReserveRequestDTO;
import org.bts.app.dto.ReserveResponseDTO;
import org.bts.app.exception.InvalidRequestException;
import org.bts.app.exception.RouteNotFoundException;
import org.bts.app.exception.SeatUnavailableException;
import org.bts.app.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketServiceImplTest {

    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        // Because TicketServiceImpl uses static Storage, it's shared across tests.
        // In a real Spring app, this would be injected, but for Vanilla Java, we just instantiate it.
        ticketService = new TicketServiceImpl();
    }

    @Test
    @DisplayName("Should successfully check availability for a valid route")
    void shouldCheckAvailabilitySuccessfully() {
        // Act
        // Route A to C price is 100.0 per seat
        AvailabilityResponseDTO response = ticketService.checkAvailability(2, "A", "C");

        // Assert
        assertNotNull(response, "Response should not be null");
        assertFalse(response.availableSeats().isEmpty(), "Seats should be available for a fresh system");
        assertEquals(2, response.availableSeats().size(), "Should return exactly the requested number of seats");
        assertEquals(200.0, response.totalPrice(), "Price should be 2 * 100.0 for A -> C");
    }

    @Test
    @DisplayName("Should throw RouteNotFoundException for an invalid route")
    void shouldThrowExceptionForInvalidRoute() {
        // Act & Assert
        RouteNotFoundException exception = assertThrows(
                RouteNotFoundException.class,
                () -> ticketService.checkAvailability(1, "A", "X")
        );

        assertTrue(exception.getMessage().contains("Invalid route"));
    }

    @Test
    @DisplayName("Should throw InvalidRequestException for invalid passenger count")
    void shouldThrowExceptionForInvalidPassengerCount() {
        // Act & Assert
        assertThrows(
                InvalidRequestException.class,
                () -> ticketService.checkAvailability(0, "A", "C"),
                "Should reject 0 passengers"
        );
        
        assertThrows(
                InvalidRequestException.class,
                () -> ticketService.checkAvailability(-5, "A", "C"),
                "Should reject negative passengers"
        );
    }

    @Test
    @DisplayName("Should throw SeatUnavailableException when passenger count exceeds bus capacity")
    void shouldThrowExceptionWhenExceedingCapacity() {
         // Act & Assert
         assertThrows(
                 SeatUnavailableException.class,
                 () -> ticketService.checkAvailability(41, "A", "C"), // Bus has 40 seats
                 "Should reject requests exceeding total bus capacity"
         );
    }

    @Test
    @DisplayName("Should successfully reserve tickets and return required details")
    void shouldReserveTicketSuccessfully() {
        // Arrange
        // Route B to C price is 50.0 per seat
        ReserveRequestDTO request = new ReserveRequestDTO("B", "C", 3, true);

        // Act
        ReserveResponseDTO response = ticketService.reserveTicket(request);

        // Assert
        assertNotNull(response.bookedId(), "Booking ID should be generated");
        assertTrue(response.bookedId().startsWith("TKT-"), "Booking ID should start with TKT-");
        assertEquals(3, response.assignedSeats().size(), "Should assign 3 seats");
        assertEquals("B", response.tripDetails().origin());
        assertEquals("C", response.tripDetails().destination());
        assertEquals(150.0, response.totalPrice(), "Price should be 3 * 50.0");
    }

    @Test
    @DisplayName("Should throw InvalidRequestException if required reservation fields are missing")
    void shouldThrowExceptionForMissingReservationFields() {
        // Arrange
        ReserveRequestDTO nullOriginRequest = new ReserveRequestDTO(null, "C", 1, true);
        ReserveRequestDTO emptyDestinationRequest = new ReserveRequestDTO("A", "  ", 1, true);

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> ticketService.reserveTicket(nullOriginRequest));
        assertThrows(InvalidRequestException.class, () -> ticketService.reserveTicket(emptyDestinationRequest));
    }

    @Test
    @DisplayName("Should throw InvalidRequestException if price is not confirmed")
    void shouldThrowExceptionIfPriceNotConfirmed() {
        // Arrange
        ReserveRequestDTO unconfirmedRequest = new ReserveRequestDTO("A", "C", 1, false);

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> ticketService.reserveTicket(unconfirmedRequest));
        assertEquals("Price confirmation is required", exception.getMessage());
    }
}
