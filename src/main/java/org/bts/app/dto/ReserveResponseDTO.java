package org.bts.app.dto;

import org.bts.app.model.Seat;
import java.util.List;
/**
 * DTO for representing the response of the Seat Reservation API.
 *
 * @param bookedId Unique booking identifier generated for the reservation.
 * @param tripDetails Details of the trip including origin, destination.
 * @param assignedSeats List of seats successfully reserved for the passengers.
 * @param totalPrice Total price for the reservation based on passenger count and route.
 */
public record ReserveResponseDTO(String bookedId, TripDetailsDTO tripDetails, List<Seat> assignedSeats, Double totalPrice) {
    }
