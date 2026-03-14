package org.bts.app.dto;

import org.bts.app.model.Seat;

import java.util.List;

/**
 * Data Transfer Object representing the response of the Seat Availability API.
 *
 * @param availableSeats List of seats available for the requested route.
 * @param totalPrice Total price for the given origin, destination, and passenger count.
 */
public record AvailabilityResponseDTO(
        List<Seat> availableSeats,
        Double totalPrice
) {}
