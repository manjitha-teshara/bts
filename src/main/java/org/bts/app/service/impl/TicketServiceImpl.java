package org.bts.app.service.impl;

import org.bts.app.dto.AvailabilityRequestDTO;
import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.BookingRequestDTO;
import org.bts.app.dto.BookingResponseDTO;
import org.bts.app.dto.TripDetailsDTO;
import org.bts.app.model.Seat;
import org.bts.app.service.TicketService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TicketServiceImpl implements TicketService {
    @Override
    public AvailabilityResponseDTO checkAvailability(AvailabilityRequestDTO requestDTO) {
        AvailabilityResponseDTO response = new AvailabilityResponseDTO();
        response.setAvailableSeats(getAvailableSeats(requestDTO.getPassengerCount(), requestDTO.getOrigin(), requestDTO.getDestination()));
        response.setTotalPrice(getTotalPrice(requestDTO.getPassengerCount(), requestDTO.getOrigin(), requestDTO.getDestination()));
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
        response.setAssignedSeats(getAvailableSeats(requestDTO.getPassengerCount(), requestDTO.getOrigin(), requestDTO.getDestination()));
        response.setTotalPrice(getTotalPrice(requestDTO.getPassengerCount(), requestDTO.getOrigin(), requestDTO.getDestination()));
        return response;
    }

    private List<Seat> getAvailableSeats(int passengerCount, String origin, String destination ) {
        List<Seat> seats = new ArrayList<>();
        return seats;
    }

    private Number getTotalPrice(int passengerCount, String origin, String destination) {
        Map<String, Map<String, Double>> priceWithRoute = new HashMap<>();

        Map<String, Double> fromA = new HashMap<>();
        fromA.put("B", 50.0); // A --> B
        fromA.put("C", 100.0);
        fromA.put("D", 150.0);
        priceWithRoute.put("A", fromA);

        Map<String, Double> fromB = new HashMap<>();
        fromB.put("C", 50.0); // B --> C
        fromB.put("D", 100.0);
        priceWithRoute.put("B", fromB);

        Map<String, Double> fromC = new HashMap<>();
        fromC.put("D", 50.0); // C --> D
        priceWithRoute.put("C", fromC);

        Double routePrice = null;

        if (priceWithRoute.containsKey(origin) && priceWithRoute.get(origin).containsKey(destination)) {
            routePrice = priceWithRoute.get(origin).get(destination);
        }
        else if (priceWithRoute.containsKey(destination) && priceWithRoute.get(destination).containsKey(origin)) {
            routePrice = priceWithRoute.get(destination).get(origin);
        }
        else {
            throw new IllegalArgumentException("route not found: " + origin + " -> " + destination);
        }
        return routePrice * passengerCount;
    }
}
