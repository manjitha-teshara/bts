package org.bts.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeatTest {

    private Seat seat;

    @BeforeEach
    void setUp() {
        seat = new Seat();
        seat.setSeatId("1A").setRow("A").setColumn(1);
    }

    @Test
    @DisplayName("Should correctly initialize with all segments available")
    void shouldInitializeWithAvailableSegments() {
        SeatStatus[] segments = seat.getSegmentStatus();
        
        assertEquals(6, segments.length);
        for (SeatStatus status : segments) {
            assertEquals(SeatStatus.AVAILABLE, status, "Expected all segments to be AVAILABLE initially");
        }
    }

    @Test
    @DisplayName("Should return true when checking availability of unreserved segments")
    void shouldBeAvailableForSegments() {
        // Arrange
        Integer[] requiredSegments = {0, 1, 2};

        // Act
        boolean isAvailable = seat.isAvailableForSegments(requiredSegments);

        // Assert
        assertTrue(isAvailable, "Seat should be available for these segments");
    }

    @Test
    @DisplayName("Should successfully reserve segments and return true")
    void shouldReserveSegmentsSuccessfully() {
        // Arrange
        Integer[] requiredSegments = {1, 2};
        String bookingId = "TKT-123";

        // Act
        boolean result = seat.reserveSegments(requiredSegments, bookingId);

        // Assert
        assertTrue(result, "Reservation should succeed");
        
        SeatStatus[] currentStatus = seat.getSegmentStatus();
        String[] currentIds = seat.getReservationIds();
        
        assertEquals(SeatStatus.RESERVED, currentStatus[1]);
        assertEquals(SeatStatus.RESERVED, currentStatus[2]);
        assertEquals(SeatStatus.AVAILABLE, currentStatus[0], "Unreserved segments should remain available");
        
        assertEquals(bookingId, currentIds[1]);
        assertEquals(bookingId, currentIds[2]);
    }

    @Test
    @DisplayName("Should return false when attempting to reserve already booked segments")
    void shouldFailToReserveIfAlreadyBooked() {
        // Arrange
        Integer[] initialBooking = {1, 2};
        seat.reserveSegments(initialBooking, "FIRST-BOOKING");

        Integer[] overlappingBooking = {2, 3}; // Segment 2 overlaps

        // Act
        boolean result = seat.reserveSegments(overlappingBooking, "SECOND-BOOKING");

        // Assert
        assertFalse(result, "Should prevent overlapping reservations on the same segment");
        
        // Verify state hasn't changed for the new booking attempt
        String[] currentIds = seat.getReservationIds();
        assertEquals("FIRST-BOOKING", currentIds[2], "Original booking should remain intact");
        assertNull(currentIds[3], "Segment 3 should not have been reserved by failed attempt");
    }

    @Test
    @DisplayName("Should correctly free reserved segments")
    void shouldFreeSegments() {
        // Arrange
        Integer[] segmentsToBook = {3, 4};
        seat.reserveSegments(segmentsToBook, "TEMP-BOOKING");

        // Act
        seat.freeSegments(segmentsToBook);

        // Assert
        SeatStatus[] currentStatus = seat.getSegmentStatus();
        String[] currentIds = seat.getReservationIds();

        assertEquals(SeatStatus.AVAILABLE, currentStatus[3]);
        assertEquals(SeatStatus.AVAILABLE, currentStatus[4]);
        assertNull(currentIds[3], "Booking ID should be cleared");
        assertNull(currentIds[4], "Booking ID should be cleared");
    }
}
