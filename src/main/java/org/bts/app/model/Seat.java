package org.bts.app.model;

import java.util.Arrays;

/**
 * Represents a single seat on a bus.
 * The seat's availability is segmented to allow overlapping bookings
 * for different parts of a journey.
 * 
 * Thread-Safety: Modification of the seat's segment status is thread-safe.
 * State mutation methods are synchronized to prevent race conditions during booking.
 */
public class Seat {
    private String seatId; // e.g., A-1, A-2
    private String row;    // e.g., A, B, C...
    private int column;    // e.g., 1, 2, 3...

    private final SeatStatus[] segmentStatus = new SeatStatus[6];
    private final String[] reservationIds = new String[6];

    /**
     * Constructs a seat and initializes all segments to AVAILABLE.
     */
    public Seat() {
        Arrays.fill(segmentStatus, SeatStatus.AVAILABLE);
    }

    public String getSeatId() {
        return seatId;
    }

    public Seat setSeatId(String seatId) {
        this.seatId = seatId;
        return this;
    }

    public String getRow() {
        return row;
    }

    public Seat setRow(String row) {
        this.row = row;
        return this;
    }

    public int getColumn() {
        return column;
    }

    public Seat setColumn(int column) {
        this.column = column;
        return this;
    }

    /**
     * Retrieves a copy of the segment statuses to prevent external mutation.
     * @return Array of SeatStatuses for each segment.
     */
    public synchronized SeatStatus[] getSegmentStatus() {
        return Arrays.copyOf(segmentStatus, segmentStatus.length);
    }

    /**
     * Retrieves a copy of the reservation IDs to prevent external mutation.
     * @return Array of reservation IDs for each segment.
     */
    public synchronized String[] getReservationIds() {
        return Arrays.copyOf(reservationIds, reservationIds.length);
    }

    /**
     * Checks if the seat is available for the given segment indexes securely.
     *
     * @param segmentIndexes The indexes of the route segments required.
     * @return true if all required segments are AVAILABLE.
     */
    public synchronized boolean isAvailableForSegments(Integer[] segmentIndexes) {
        for (Integer index : segmentIndexes) {
            if (segmentStatus[index] != SeatStatus.AVAILABLE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Attempts to reserve the specified segments for a booking.
     * This operation is thread-safe and atomic at the seat level.
     *
     * @param segmentIndexes The segments to reserve.
     * @param bookingId The ID of the reservation.
     * @return true if successful, false if the seat was not available.
     */
    public synchronized boolean reserveSegments(Integer[] segmentIndexes, String bookingId) {
        if (!isAvailableForSegments(segmentIndexes)) {
            return false;
        }

        for (Integer index : segmentIndexes) {
            segmentStatus[index] = SeatStatus.RESERVED;
            reservationIds[index] = bookingId;
        }
        return true;
    }

    /**
     * Rolls back a reservation, freeing up the specified segments.
     * This operation is thread-safe.
     *
     * @param segmentIndexes The segments to free.
     */
    public synchronized void freeSegments(Integer[] segmentIndexes) {
        for (Integer index : segmentIndexes) {
            segmentStatus[index] = SeatStatus.AVAILABLE;
            reservationIds[index] = null;
        }
    }
}
