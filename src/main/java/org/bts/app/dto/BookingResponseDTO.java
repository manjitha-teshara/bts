package org.bts.app.dto;

import org.bts.app.model.Seat;

import java.util.List;

public class BookingResponseDTO {
    private String ticketNumber;
    private TripDetailsDTO tripDetails;
    private List<Seat> assignedSeats;
    private Number totalPrice;

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public TripDetailsDTO getTripDetails() { return tripDetails; }
    public void setTripDetails(TripDetailsDTO tripDetails) { this.tripDetails = tripDetails; }

    public List<Seat> getAssignedSeats() { return assignedSeats; }
    public void setAssignedSeats(List<Seat> assignedSeats) { this.assignedSeats = assignedSeats; }

    public Number getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Number totalPrice) { this.totalPrice = totalPrice; }

}
