package org.bts.app.dto;

import org.bts.app.model.Seat;

import java.util.List;

public record AvailabilityResponseDTO(
        List<Seat> availableSeats,
        Double totalPrice
) {}
