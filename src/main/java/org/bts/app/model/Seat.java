package org.bts.app.model;

import java.util.List;

public class Seat {
    String seatId; // A-1, A-2, A-3

    String row; // A, B, C, D ..., J

    int column; // 1, 2, 3, 4

    private SeatStatus[] segmentStatus = new SeatStatus[6]; //[AVAILABLE, RESERVED, BOOKED]

    private String[] reservationIds = new String[6];

    private String[] bookingIds = new String[6];

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

    public SeatStatus[] getSegmentStatus() {
        return segmentStatus;
    }

    public Seat setSegmentStatus(SeatStatus[] segmentStatus) {
        this.segmentStatus = segmentStatus;
        return this;
    }

    public String[] getReservationIds() {
        return reservationIds;
    }

    public Seat setReservationIds(String[] reservationIds) {
        this.reservationIds = reservationIds;
        return this;
    }

    public String[] getBookingIds() {
        return bookingIds;
    }

    public Seat setBookingIds(String[] bookingIds) {
        this.bookingIds = bookingIds;
        return this;
    }
}
