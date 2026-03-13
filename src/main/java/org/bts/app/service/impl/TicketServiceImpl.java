package org.bts.app.service.impl;

import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.BookingRequestDTO;
import org.bts.app.dto.BookingResponseDTO;
import org.bts.app.dto.TripDetailsDTO;
import org.bts.app.model.Seat;
import org.bts.app.service.TicketService;
import org.bts.app.storage.Storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.bts.app.model.SeatStatus.AVAILABLE;
import static org.bts.app.model.SeatStatus.RESERVED;

public class TicketServiceImpl implements TicketService {

    private static final ConcurrentHashMap<String, Seat> SEATS = Storage.seatsInitialization();

    private static final Map<String, Map<String, Integer[]>> segmentStatusIndexes = Storage.segmentStatusIndexInitialization();

    private static final Map<String, Map<String, Double>> priceWithRoute = Storage.priceWithRouteInitialization();

    @Override
    public AvailabilityResponseDTO checkAvailability(int passengerCount, String origin, String destination, String travelDate) {
        AvailabilityResponseDTO response = new AvailabilityResponseDTO();
        List<Seat> seats = getAvailableSeats(passengerCount, origin, destination);
        if(seats.isEmpty()) {
            response.setAvailableSeats(Collections.emptyList());
            response.setTotalPrice(0.0);
        }
        else {
            response.setAvailableSeats(seats);
            response.setTotalPrice(getTotalPrice(passengerCount, origin, destination));
        }
        return response;
    }

    @Override
    public BookingResponseDTO bookTicket(BookingRequestDTO requestDTO) {

        BookingResponseDTO response = new BookingResponseDTO();

        String bookedId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        List<Seat> seats = reserveAvailableSeats(
                requestDTO.getPassengerCount(),
                requestDTO.getOrigin(),
                requestDTO.getDestination(),
                bookedId
        );

        if (seats.isEmpty()) {

            response.setAssignedSeats(Collections.emptyList());
            response.setTotalPrice(0.0);

            return response;
        }

        Double totalPrice = getTotalPrice(requestDTO.getPassengerCount(),
                requestDTO.getOrigin(), requestDTO.getDestination()
        );

        response.setAssignedSeats(seats);
        response.setTotalPrice(totalPrice);
        response.setBookedId(bookedId);

        TripDetailsDTO tripDetails = new TripDetailsDTO();
        tripDetails.setOrigin(requestDTO.getOrigin());
        tripDetails.setDestination(requestDTO.getDestination());
        tripDetails.setTravelDate(requestDTO.getTravelDate());

        response.setTripDetails(tripDetails);

        return response;
    }

    private List<Seat> getAvailableSeats(int passengerCount, String origin, String destination) {

        List<Seat> seats = new ArrayList<>();
        int passengerAdded = 0;

        Integer[] segmentIndexes = segmentStatusIndexes.get(origin).get(destination);

        for (Seat seat : SEATS.values()) {

            if (passengerAdded >= passengerCount) {
                return seats;
            }

            synchronized (seat) {

                boolean available = true;

                for (Integer index : segmentIndexes) {
                    if (seat.getSegmentStatus()[index] != AVAILABLE) {
                        available = false;
                        break;
                    }
                }

                if (available) {
                    seats.add(seat);
                    passengerAdded++;
                }
            }
        }

        return seats;
    }

    private List<Seat> reserveAvailableSeats(int passengerCount, String origin,
                                             String destination, String bookedId) {

        List<Seat> seats = new ArrayList<>();
        int passengerAdded = 0;

        Integer[] segmentIndexes = segmentStatusIndexes.get(origin).get(destination);

        for (Seat seat : SEATS.values()) {

            if (passengerAdded >= passengerCount) {
                break;
            }

            synchronized (seat) {

                boolean available = true;

                for (Integer index : segmentIndexes) {
                    if (seat.getSegmentStatus()[index] != AVAILABLE) {
                        available = false;
                        break;
                    }
                }

                if (available) {

                    // Reserve all segments
                    for (Integer index : segmentIndexes) {
                        seat.getSegmentStatus()[index] = RESERVED;
                        seat.getReservationIds()[index] = bookedId;
                    }

                    seats.add(seat);
                    passengerAdded++;
                }
            }
        }

        if (passengerAdded < passengerCount) {
            rollbackReservation(seats, segmentIndexes);
            return Collections.emptyList();
        }

        return seats;
    }

    private void rollbackReservation(List<Seat> seats, Integer[] segmentIndexes) {

        for (Seat seat : seats) {

            synchronized (seat) {

                for (Integer index : segmentIndexes) {
                    seat.getSegmentStatus()[index] = AVAILABLE;
                    seat.getReservationIds()[index] = null;
                }

            }
        }
    }

    private Double getTotalPrice(int passengerCount, String origin, String destination) {

        Double routePrice = 0.0;

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
