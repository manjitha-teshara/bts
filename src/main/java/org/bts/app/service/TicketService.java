package org.bts.app.service;

import org.bts.app.dto.AvailabilityRequestDTO;
import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.BookingRequestDTO;
import org.bts.app.dto.BookingResponseDTO;

public interface TicketService {
    AvailabilityResponseDTO checkAvailability(AvailabilityRequestDTO requestDTO);

    BookingResponseDTO reserveTicket(BookingRequestDTO requestDTO);

}
