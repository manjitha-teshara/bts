package org.bts.app.dto;

import org.bts.app.model.Seat;

import java.util.List;

public class AvailabilityResponseDTO {

    private List<Seat> availableSeats;

    private Double totalPrice;

    public List<Seat> getAvailableSeats() {
        return availableSeats;
    }

    public AvailabilityResponseDTO setAvailableSeats(List<Seat> availableSeats) {
        this.availableSeats = availableSeats;
        return this;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public AvailabilityResponseDTO setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }
}
