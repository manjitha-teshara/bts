package org.bts.app.service;

import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.BookingRequestDTO;
import org.bts.app.dto.BookingResponseDTO;

public interface TicketService {

    AvailabilityResponseDTO checkAvailability(int passengerCount, String origin, String destination, String travelDate);

    BookingResponseDTO bookTicket(BookingRequestDTO requestDTO);

}
