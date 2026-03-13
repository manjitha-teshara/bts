package org.bts.app.dto;

import java.time.LocalDate;

public class BookingRequestDTO {
    private String origin;
    private String destination;
    private int passengerCount;
    private LocalDate travelDate;
    private boolean priceConfirmation;

    public String getOrigin() {
        return origin;
    }

    public BookingRequestDTO setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public String getDestination() {
        return destination;
    }

    public BookingRequestDTO setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public BookingRequestDTO setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
        return this;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public BookingRequestDTO setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
        return this;
    }

    public boolean isPriceConfirmation() {
        return priceConfirmation;
    }

    public BookingRequestDTO setPriceConfirmation(boolean priceConfirmation) {
        this.priceConfirmation = priceConfirmation;
        return this;
    }
}
