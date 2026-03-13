package org.bts.app.dto;

import java.time.LocalDate;

public class TripDetailsDTO {
    private String origin;

    private String destination;

    private LocalDate travelDate;

    public String getOrigin() {
        return origin;
    }

    public TripDetailsDTO setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public String getDestination() {
        return destination;
    }

    public TripDetailsDTO setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public TripDetailsDTO setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
        return this;
    }
}
