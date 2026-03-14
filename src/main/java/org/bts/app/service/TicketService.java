package org.bts.app.service;

import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.ReserveRequestDTO;
import org.bts.app.dto.ReserveResponseDTO;

/**
 * Service interface defining the core bus ticketing operations.
 */
public interface TicketService {

    /**
     * Checks if there are enough available seats for a specific route.
     *
     * @param passengerCount The required number of seats.
     * @param origin         The starting location.
     * @param destination    The destination location.
     * @return {@link AvailabilityResponseDTO} containing availability details.
     */
    AvailabilityResponseDTO checkAvailability(int passengerCount, String origin, String destination);

    /**
     * Attempts to book a ticket for the specified route and passengers.
     *
     * @param requestDTO {@link ReserveRequestDTO} containing booking details.
     * @return {@link ReserveResponseDTO} with the result of the booking.
     */
    ReserveResponseDTO reserveTicket(ReserveRequestDTO requestDTO);

}
