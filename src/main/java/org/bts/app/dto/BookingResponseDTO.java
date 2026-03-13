package org.bts.app.dto;

import org.bts.app.model.Seat;

import java.util.List;

public class BookingResponseDTO {
    private String bookedId;
    private TripDetailsDTO tripDetails;
    private List<Seat> assignedSeats;
    private Double totalPrice;

    public String getBookedId() {
        return bookedId;
    }

    public BookingResponseDTO setBookedId(String bookedId) {
        this.bookedId = bookedId;
        return this;
    }

    public TripDetailsDTO getTripDetails() {
        return tripDetails;
    }

    public BookingResponseDTO setTripDetails(TripDetailsDTO tripDetails) {
        this.tripDetails = tripDetails;
        return this;
    }

    public List<Seat> getAssignedSeats() {
        return assignedSeats;
    }

    public BookingResponseDTO setAssignedSeats(List<Seat> assignedSeats) {
        this.assignedSeats = assignedSeats;
        return this;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public BookingResponseDTO setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }
}
