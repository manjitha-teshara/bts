package org.bts.app.model;

import java.util.HashMap;
import java.util.Map;

import static org.bts.app.model.SeatStatus.AVAILABLE;
import static org.bts.app.model.SeatStatus.RESERVED;

/**
 * Represents a single seat on a bus.
 * The seat's availability is segmented to allow overlapping bookings
 * for different parts of a journey.
 * 
 */
public class Seat {
    private String seatId; // e.g., 1A, 2A
    private String row;    // e.g., A, B, C...
    private int column;    // e.g., 1, 2, 3...

//    private final SeatStatus[] segmentStatus = new SeatStatus[6];
//    private final String[] reservationIds = new String[6];
    Map<String, SeatSegment> seatSegments = new HashMap<>();

    /**
     * constructs a seat and initializes all segments to AVAILABLE.
     */
    public Seat() {
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

    public Map<String, SeatSegment> getSeatSegments() {
        return seatSegments;
    }

    public Seat setSeatSegments(Map<String, SeatSegment> seatSegments) {
        this.seatSegments = seatSegments;
        return this;
    }

    /**
     * Retrieves a copy of the segment statuses to prevent external mutation.
     * @return Array of SeatStatuses for each segment.
     */
    public synchronized SeatStatus getSegmentStatus(String segment) {
        return seatSegments.get(segment).getStatus();
    }

    /**
     * Updates the seat status for the given segment.
     *
     * @param origin Starting point of the segment.
     * @param destination Ending point of the segment.
     * @param status New seat status to set.
     */
    public synchronized void setSegmentStatus(String segment, SeatStatus status) {

        SeatSegment seatSegment = seatSegments.get(segment);
        if (seatSegment != null) {
            seatSegment.setStatus(status);
        }
    }

    /**
     * Retrieves a copy of the reservation IDs to prevent external mutation.
     * @return Array of reservation IDs for each segment.
     */
    public synchronized String getReservationIds(String segment) {
        return seatSegments.get(segment).getReservationId();
    }

    /**
     * Updates the reservation ID for the given segment.
     *
     * @param origin Starting point of the segment.
     * @param destination Ending point of the segment.
     * @param reservationId Reservation identifier.
     */
    public synchronized void setReservationId(String segment, String reservationId) {
        SeatSegment seatSegment = seatSegments.get(segment);
        if (seatSegment != null) {
            seatSegment.setReservationId(reservationId);
        }
    }

    /**
     * Checks the seat is available for the given segment.
     *
     * @param segment The route segment identifier (e.g."A-B").
     * @return true if the segment status is AVAILABLE and not reserved.
     */
    public synchronized boolean isAvailableForSegment(String segment) {
        SeatSegment status = seatSegments.get(segment);

        return status != null
                && status.getStatus() == AVAILABLE
                && (status.getReservationId() == null || status.getReservationId().isEmpty());
    }

    /**
     * Attempts to reserve a seat for the given route segment.
     * The operation is synchronized to ensure thread safety at the seat level.
     *
     * @param origin Starting location of the trip.
     * @param destination Destination location of the trip.
     * @param bookingId Unique reservation identifier.
     * @return true if the reservation was successful, otherwise false.
     */
    public synchronized boolean reserveSegment(String segment, String bookingId) {

        if (!isAvailableForSegment(segment)) {
            return false;
        }

        SeatSegment status = seatSegments.get(segment);

        if (status != null && status.getStatus() == AVAILABLE) {
            status.setStatus(RESERVED);
            status.setReservationId(bookingId);
            return true;
        }

        return false;
    }

    /**
     * Rolls back a reservation, freeing up the specified segments.
     * This operation is thread-safe.
     *
     * @param segmentIndexes The segments to free.
     */
    public synchronized void freeSegment(String segment) {
        setReservationId(segment,"");
        setSegmentStatus(segment, AVAILABLE);
    }

    /**
     * Resets the seat to its initial state (all segments AVAILABLE, no reservations).
     */
    public synchronized void reset() {
        seatSegments.put("A-B", new SeatSegment(AVAILABLE, ""));
        seatSegments.put("B-C", new SeatSegment(AVAILABLE, ""));
        seatSegments.put("C-D", new SeatSegment(AVAILABLE, ""));
        seatSegments.put("D-C", new SeatSegment(AVAILABLE, ""));
        seatSegments.put("C-B", new SeatSegment(AVAILABLE, ""));
        seatSegments.put("B-A", new SeatSegment(AVAILABLE, ""));
    }
}
