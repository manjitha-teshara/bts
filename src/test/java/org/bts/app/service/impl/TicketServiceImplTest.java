package org.bts.app.service.impl;

import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.exception.InvalidRequestException;
import org.bts.app.exception.RouteNotFoundException;
import org.bts.app.exception.SeatUnavailableException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketServiceImplTest {

    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new TicketServiceImpl();
        ticketService.resetSystem();
    }

    @AfterEach
    void tearDown() {
        ticketService.resetSystem();
    }

    @Test
    void checkAvailability_forExceedPassengerCount_throwsException() {
        int excessiveCount = 50; // Total capacity is 40 (10 rows * 4 cols)
        
        SeatUnavailableException exception = assertThrows(
                SeatUnavailableException.class,
                () -> ticketService.checkAvailability(excessiveCount, "A", "B")
        );
        
        assertEquals("Passenger count exceeds total bus capacity", exception.getMessage());
    }

    @Test
    void checkAvailability_originAndDestinationInvalid_throwsException() {
        int passengerCount = 2;
        String invalidOrigin = "X";
        String invalidDestination = "Y";

        RouteNotFoundException exception = assertThrows(
                RouteNotFoundException.class,
                () -> ticketService.checkAvailability(passengerCount, invalidOrigin, invalidDestination)
        );

        assertTrue(exception.getMessage().contains("Invalid route"));
    }

    @Test
    void checkAvailability_thereIsNoAvailableSeatInBetweenGivenRange_throwsException() {
        for (int i = 0; i < 40; i++) {
            // Bypass checkAvailability and directly reserve to simulate a full bus
            ticketService.reserveTicket(new org.bts.app.dto.ReserveRequestDTO(
                    "B", "C", 1, 50.0
            ));
        }

        SeatUnavailableException exception = assertThrows(
                SeatUnavailableException.class,
                () -> ticketService.checkAvailability(1, "A", "D")
        );
        
        assertEquals("No availability for 1 passengers from A to D", exception.getMessage());
    }

    @Test
    void checkAvailability_validRequest_returnsAvailableSeatsAndPrice() {
        int passengerCount = 2;
        String origin = "A";
        String destination = "C"; // Price A->B (50) + B->C (50) = 100 per passenger

        AvailabilityResponseDTO response = ticketService.checkAvailability(passengerCount, origin, destination);

        assertNotNull(response);
        assertFalse(response.availableSeats().isEmpty());
        assertEquals(2, response.availableSeats().size());
        assertEquals(200.0, response.totalPrice()); // 100 * 2 passengers
    }

    @Test
    void checkAvailability_negativePassengerCount_throwsException() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> ticketService.checkAvailability(-1, "A", "B")
        );
        assertEquals("Passenger count must be greater than zero", exception.getMessage());
    }

    @Test
    void checkAvailability_nullOriginOrDestination_throwsException() {
        // for null origin
        assertThrows(
                InvalidRequestException.class,
                () -> ticketService.checkAvailability(1, null, "B")
        );

        // for null destination
        assertThrows(
                InvalidRequestException.class,
                () -> ticketService.checkAvailability(1, "A", null)
        );
    }
    
    // reserveTicket Tests

    @Test
    void reserveTicket_validRequest_returnsBookingDetails() {
        org.bts.app.dto.ReserveRequestDTO request = new org.bts.app.dto.ReserveRequestDTO("A", "C", 2, 200.0);

        org.bts.app.dto.ReserveResponseDTO response = ticketService.reserveTicket(request);

        assertNotNull(response);
        assertNotNull(response.ticketNumber());
        assertTrue(response.ticketNumber().startsWith("TKT-"));
        assertEquals("A", response.tripDetails().origin());
        assertEquals("C", response.tripDetails().destination());
        assertEquals(2, response.assignedSeats().size());
        assertEquals(200.0, response.totalPrice());
    }

    @Test
    void reserveTicket_withoutPriceConfirmation_throwsException() {
        org.bts.app.dto.ReserveRequestDTO request = new org.bts.app.dto.ReserveRequestDTO("A", "C", 2, 100.0);

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> ticketService.reserveTicket(request)
        );
        assertEquals("Price confirmation is required to reserve seats", exception.getMessage());
    }

    @Test
    void reserveTicket_insufficientSeats_throwsException() {
        // Book all seats from A to C
        for (int i = 0; i < 40; i++) {
            ticketService.reserveTicket(new org.bts.app.dto.ReserveRequestDTO("A", "C", 1, 100.0));
        }

        // try to book one more
        org.bts.app.dto.ReserveRequestDTO request = new org.bts.app.dto.ReserveRequestDTO("A", "C", 1, 100.0);

        SeatUnavailableException exception = assertThrows(
                SeatUnavailableException.class,
                () -> ticketService.reserveTicket(request)
        );
        assertEquals("Not enough seats available for this route.", exception.getMessage());
    }

    // resetSystem Tests

    @Test
    void resetSystem_clearsAllReservations() {
        ticketService.reserveTicket(new org.bts.app.dto.ReserveRequestDTO("A", "B", 1, 50.0));
        // ensure availability is reduced (40 total - 1 booked = 39)
        assertThrows(
                SeatUnavailableException.class,
                () -> ticketService.checkAvailability(40, "A", "B")
        );

        ticketService.resetSystem();

        AvailabilityResponseDTO afterReset = ticketService.checkAvailability(40, "A", "B");
        assertEquals(40, afterReset.availableSeats().size());
    }
}
