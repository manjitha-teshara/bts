package org.bts.app.service;

import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.ReserveRequestDTO;
import org.bts.app.dto.ReserveResponseDTO;

/**
 * Service interface for doing bus ticket operations.
 */
public interface TicketService {

    /**
     * check available seat for route.
     *
     * @param passengerCount how many passenger
     * @param origin         start place
     * @param destination    where to go
     * @return AvailabilityResponseDTO has seat details.
     */
    AvailabilityResponseDTO checkAvailability(int passengerCount, String origin, String destination);

    /**
     * try to book ticket for passenger.
     *
     * @param requestDTO ReserveRequestDTO have book details
     * @return ReserveResponseDTO show book result
     */
    ReserveResponseDTO reserveTicket(ReserveRequestDTO requestDTO);

    /**
     * clear all booked seat and make all seat available again.
     */
    void resetSystem();

}
