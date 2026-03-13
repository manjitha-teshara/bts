package org.bts.app.dto;

import java.time.LocalDate;

public record TripDetailsDTO(String origin, String destination, LocalDate travelDate) {
}
