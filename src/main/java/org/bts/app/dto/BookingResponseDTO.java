package org.bts.app.dto;

import org.bts.app.model.Seat;

import java.util.List;

public record BookingResponseDTO(String bookedId, TripDetailsDTO tripDetails, List<Seat> assignedSeats, Double totalPrice) {
    }
