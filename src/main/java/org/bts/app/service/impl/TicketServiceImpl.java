package org.bts.app.service.impl;

import org.bts.app.dto.AvailabilityRequestDTO;
import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.BookingRequestDTO;
import org.bts.app.dto.BookingResponseDTO;
import org.bts.app.dto.TripDetailsDTO;
import org.bts.app.service.TicketService;
import java.util.Arrays;
import java.util.UUID;

public class TicketServiceImpl implements TicketService {
    @Override
    public AvailabilityResponseDTO checkAvailability(AvailabilityRequestDTO requestDTO) {
        AvailabilityResponseDTO response = new AvailabilityResponseDTO();
        response.setAvailableSeats(Arrays.asList("A1", "A2", "B1", "B2"));
        response.setTotalPrice(50.0 * requestDTO.getPassengerCount());
        response.setCurrency("USD");
        return response;
    }

    @Override
    public BookingResponseDTO reserveTicket(BookingRequestDTO requestDTO) {
        BookingResponseDTO response = new BookingResponseDTO();
        response.setTicketNumber("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        TripDetailsDTO tripDetails = new TripDetailsDTO();
        tripDetails.setOrigin(requestDTO.getOrigin());
        tripDetails.setDestination(requestDTO.getDestination());
        tripDetails.setTravelDate(requestDTO.getTravelDate());
        
        response.setTripDetails(tripDetails);
        response.setAssignedSeats(Arrays.asList("A1", "A2").subList(0, Math.min(2, requestDTO.getPassengerCount())));
        response.setTotalPrice(50.0 * requestDTO.getPassengerCount());
        response.setCurrency("USD");
        return response;
    }
}
