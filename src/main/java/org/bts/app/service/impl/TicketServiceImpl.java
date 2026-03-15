package org.bts.app.service.impl;

import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.ReserveRequestDTO;
import org.bts.app.dto.ReserveResponseDTO;
import org.bts.app.dto.TripDetailsDTO;
import org.bts.app.exception.InvalidRequestException;
import org.bts.app.exception.RouteNotFoundException;
import org.bts.app.exception.SeatUnavailableException;
import org.bts.app.model.Seat;
import org.bts.app.service.TicketService;
import org.bts.app.storage.Storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Implementation of the {@link TicketService}.
 *
 * <p>
 * This service handles the core business logic for checking ticket availability
 * and reserving seats.
 * </p>
 */
public class TicketServiceImpl implements TicketService {

    private static final Logger LOGGER = Logger.getLogger(TicketServiceImpl.class.getName());

    private static final ConcurrentHashMap<String, Seat> SEATS = Storage.seatsInitialization();

    private static final Map<String, Map<String, Double>> priceWithRoute = Storage.priceWithRouteInitialization();

    /**
     * Checks the availability of seats for a given route and passenger count.
     * 
     * @param passengerCount The number of requested seats.
     * @param origin         The starting point of the journey.
     * @param destination    The end point of the journey.
     * @return AvailabilityResponseDTO containing available seats (or empty list if
     *         unavailable) and total price.
     * @throws InvalidRequestException if any input parameter is invalid.
     * @throws RouteNotFoundException  if the specified route does not exist.
     */
    @Override
    public AvailabilityResponseDTO checkAvailability(int passengerCount, String origin, String destination) {
        validateInputs(passengerCount, origin, destination);

        List<Seat> seats = getAvailableSeats(passengerCount, origin, destination);
        Double totalPrice = 0.0;
        if (seats.isEmpty()) {
            seats = Collections.emptyList();
            LOGGER.info(String.format("No availability for %d passengers from %s to %s", passengerCount, origin, destination));
        } else {
            totalPrice = getTotalPrice(passengerCount, origin, destination);
        }
        return new AvailabilityResponseDTO(seats, totalPrice);
    }

    /**
     * Books a ticket by attempting to reserve the necessary segments across the
     * required number of seats.
     * If enough seats cannot be successfully reserved, all already reserved seats
     * for this transaction
     * are rolled back to ensure data consistency.
     *
     * @param requestDTO The booking constraints.
     * @return BookingResponseDTO containing the Booking ID and finalized trip
     *         details.
     * @throws InvalidRequestException  if the request parameters are invalid.
     * @throws RouteNotFoundException   if the requested route is not available.
     * @throws SeatUnavailableException if not enough seats could be acquired.
     */
    @Override
    public synchronized ReserveResponseDTO reserveTicket(ReserveRequestDTO requestDTO) {
        validateInputs(requestDTO.passengerCount(), requestDTO.origin(), requestDTO.destination());

        if (!requestDTO.priceConfirmation()) {
            throw new InvalidRequestException("Price confirmation is required to reserve seats");
        }

        String bookedId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        List<Seat> seats = reserveAvailableSeats(
                requestDTO.passengerCount(),
                requestDTO.origin(),
                requestDTO.destination(),
                bookedId);

        if (seats.isEmpty()) {
            LOGGER.warning(String.format("Booking failed for %s from %s to %s due to insufficient seats", bookedId, requestDTO.origin(), requestDTO.destination()));
            throw new SeatUnavailableException("Not enough seats available for this route.");
        }

        Double totalPrice = getTotalPrice(requestDTO.passengerCount(), requestDTO.origin(), requestDTO.destination());

        TripDetailsDTO tripDetails = new TripDetailsDTO(requestDTO.origin(), requestDTO.destination());
        LOGGER.info(String.format("Successfully booked %d seats for %s (Booking ID: %s)", seats.size(), requestDTO.origin() + "->" + requestDTO.destination(), bookedId));
        return new ReserveResponseDTO(bookedId, tripDetails, seats, totalPrice);
    }

    private void validateInputs(int passengerCount, String origin, String destination) {
        if (passengerCount <= 0) {
            throw new InvalidRequestException("Passenger count must be greater than zero");
        }
        if (passengerCount > SEATS.size()) {
            throw new SeatUnavailableException("Passenger count exceeds total bus capacity");
        }
        if (origin == null || origin.trim().isEmpty()) {
            throw new InvalidRequestException("Origin is required");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new InvalidRequestException("Destination is required");
        }
        List<String> segments;
        try {
            segments = Storage.generateSegments(origin, destination);
        } catch (IllegalArgumentException e) {
            throw new RouteNotFoundException("Invalid route: " + origin + " to " + destination);
        }
        
        if (segments.isEmpty()) {
            throw new RouteNotFoundException("Invalid route: " + origin + " to " + destination);
        }
    }

    private List<Seat> getAvailableSeats(int passengerCount, String origin, String destination) {

        List<Seat> seats = new ArrayList<>();
        int passengerAdded = 0;
        List<String> segments = Storage.generateSegments(origin, destination);

        for (Seat seat : SEATS.values()) {
            if (passengerAdded >= passengerCount) {
                return seats;
            }

            if (seat.isAvailableForSegments(segments)) {
                seats.add(seat);
                passengerAdded++;
            }
        }

        // return empty if we couldn't fulfill the entire passenger count
        return passengerAdded == passengerCount ? seats : Collections.emptyList();
    }

    private List<Seat> reserveAvailableSeats(int passengerCount, String origin,
            String destination, String reservationId) {

        List<Seat> reservedSeats = new ArrayList<>();
        int passengerAdded = 0;
        List<String> segments = Storage.generateSegments(origin,destination);

        for (Seat seat : SEATS.values()) {
            if (passengerAdded >= passengerCount) {
                break;
            }

            // reserveSegments is atomic thread safe at the Seat level
            if (seat.reserveSegments(segments, reservationId)) {
                reservedSeats.add(seat);
                passengerAdded++;
            }
        }

        if (passengerAdded < passengerCount) {
            // rollback if we failed to acquire all needed seats
            for (Seat seat : reservedSeats) {
                seat.freeSegments(segments);
            }
            return Collections.emptyList();
        }

        return reservedSeats;
    }

    private Double getTotalPrice(int passengerCount, String origin, String destination) {

        Double routePrice;

        if (priceWithRoute.containsKey(origin) && priceWithRoute.get(origin).containsKey(destination)) {
            routePrice = priceWithRoute.get(origin).get(destination);
        } else if (priceWithRoute.containsKey(destination) && priceWithRoute.get(destination).containsKey(origin)) {
            routePrice = priceWithRoute.get(destination).get(origin);
        } else {
            throw new RouteNotFoundException("Route not found: " + origin + " -> " + destination);
        }
        return routePrice * passengerCount;
    }

    /**
     * Resets all seats to their initial AVAILABLE state.
     * occurring while the system is clear for next day.
     */
    @Override
    public synchronized void resetSystem() {
        for (Seat seat : SEATS.values()) {
            seat.reset();
        }
        LOGGER.info("System has been refreshed. All seats are now available.");
    }
}
