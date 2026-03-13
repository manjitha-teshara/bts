package org.bts.app.dto;

import java.time.LocalDate;

public class AvailabilityRequestDTO {

    private String origin;
    private String destination;
    private int passengerCount;
    private LocalDate travelDate;// make it optional

    public String getOrigin() {
        return origin;
    }

    public AvailabilityRequestDTO setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public String getDestination() {
        return destination;
    }

    public AvailabilityRequestDTO setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public AvailabilityRequestDTO setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
        return this;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public AvailabilityRequestDTO setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
        return this;
    }
}
