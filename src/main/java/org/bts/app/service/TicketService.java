package org.bts.app.service;

import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.BookingRequestDTO;
import org.bts.app.dto.BookingResponseDTO;

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
     * @param travelDate     The date of travel.
     * @return {@link AvailabilityResponseDTO} containing availability details.
     */
    AvailabilityResponseDTO checkAvailability(int passengerCount, String origin, String destination, String travelDate);

    /**
     * Attempts to book a ticket for the specified route and passengers.
     *
     * @param requestDTO {@link BookingRequestDTO} containing booking details.
     * @return {@link BookingResponseDTO} with the result of the booking.
     */
    BookingResponseDTO bookTicket(BookingRequestDTO requestDTO);

}
