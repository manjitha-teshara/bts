package org.bts.app.dto;

import java.util.List;

public class AvailabilityResponseDTO {

    private List<String> availableSeats;
    private Number totalPrice;
    private String currency;

    public List<String> getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(List<String> availableSeats) { this.availableSeats = availableSeats; }

    public Number getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Number totalPrice) { this.totalPrice = totalPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
