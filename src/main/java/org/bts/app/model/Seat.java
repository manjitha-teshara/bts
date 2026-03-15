package org.bts.app.model;

import java.util.HashMap;
import java.util.List;
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
     * Retrieves the segment statuses
     * @return Array of SeatStatuses for each segment.
     */
    public synchronized SeatStatus getSegmentStatus(String segment) {
        return seatSegments.get(segment).getStatus();
    }

    /**
     * Updates the seat status for the given segment.
     *
     * @param segment Starting point and destination point. eg: A-B
     * @param status New seat status to set.
     */
    public synchronized void setSegmentStatus(String segment, SeatStatus status) {

        SeatSegment seatSegment = seatSegments.get(segment);
        if (seatSegment != null) {
            seatSegment.setStatus(status);
        }
    }

    /**
     * Retrieves the reservation ID.
     * @return reservation ID for given segment.
     */
    public synchronized String getReservationId(String segment) {
        return seatSegments.get(segment).getReservationId();
    }

    /**
     * Updates the reservation ID for the given segment.
     *
     * @param segment Starting point and destination point. eg: A-B
     * @param reservationId Reservation ID.
     */
    public synchronized void setReservationId(String segment, String reservationId) {
        SeatSegment seatSegment = seatSegments.get(segment);
        if (seatSegment != null) {
            seatSegment.setReservationId(reservationId);
        }
    }

    /**
     * Checks if the seat is available for the given segments.
     *
     * @param segments The route segments required.
     * @return true if the segments are AVAILABLE and not reserved.
     */
    public synchronized boolean isAvailableForSegments(List<String> segments) {
        for (String segment : segments) {
            SeatSegment status = seatSegments.get(segment);
            if (status != null && (status.getStatus() != AVAILABLE || 
                (status.getReservationId() != null && !status.getReservationId().isEmpty()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Attempts to reserve a seat for the given route segments atomically.
     *
     * @param segments The required segments.
     * @param bookingId Unique reservation identifier.
     * @return true if the reservation was successful, otherwise false.
     */
    public synchronized boolean reserveSegments(List<String> segments, String bookingId) {
        if (!isAvailableForSegments(segments)) {
            return false;
        }

        for (String segment : segments) {
            SeatSegment seatSegment = seatSegments.get(segment);
            if (seatSegment != null) {
                seatSegment.setStatus(RESERVED);
                seatSegment.setReservationId(bookingId);
            }
        }
        return true;
    }

    /**
     * Rolls back a reservation, freeing up the specified segments.
     *
     * @param segments The segments to free.
     */
    public synchronized void freeSegments(List<String> segments) {
        for (String segment : segments) {
            SeatSegment status = seatSegments.get(segment);
            if (status != null) {
                status.setStatus(AVAILABLE);
                status.setReservationId("");
            }
        }
    }

    /**
     * Resets the seat to its initial state (all segments AVAILABLE, no reservations).
     */
    public synchronized void reset() {
        for (SeatSegment status : seatSegments.values()) {
            status.setStatus(AVAILABLE);
            status.setReservationId("");
        }
    }
}
