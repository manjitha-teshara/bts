package org.bts.app.dto;

import org.bts.app.model.Seat;

import java.util.List;

public class AvailabilityResponseDTO {

    private List<Seat> availableSeats;
    private Number totalPrice;

    public List<Seat> getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(List<Seat> availableSeats) { this.availableSeats = availableSeats; }

    public Number getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Number totalPrice) { this.totalPrice = totalPrice; }

}
