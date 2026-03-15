package org.bts.app.model;

public class SeatSegment {
    private SeatStatus status;

    private String reservationId;

    public SeatSegment(SeatStatus seatStatus, String reservationId) {
        this.status = seatStatus;
        this.reservationId = reservationId;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public SeatSegment setStatus(SeatStatus status) {
        this.status = status;
        return this;
    }

    public String getReservationId() {
        return reservationId;
    }

    public SeatSegment setReservationId(String reservationId) {
        this.reservationId = reservationId;
        return this;
    }
}
