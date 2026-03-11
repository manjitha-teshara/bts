package org.bts.app.dto;

import java.util.List;

public class BookingResponseDTO {
    private String ticketNumber;
    private TripDetailsDTO tripDetails;
    private List<String> assignedSeats;
    private double totalPrice;
    private String currency;

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public TripDetailsDTO getTripDetails() { return tripDetails; }
    public void setTripDetails(TripDetailsDTO tripDetails) { this.tripDetails = tripDetails; }

    public List<String> getAssignedSeats() { return assignedSeats; }
    public void setAssignedSeats(List<String> assignedSeats) { this.assignedSeats = assignedSeats; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
