package org.bts.app.dto;

import java.util.List;

public class BookingResponseDTO {
    private String ticketNumber;
    private TripDetailsDTO tripDetails;
    private List<String> assignedSeats;
    private double totalPrice;
    private String currency;
}
