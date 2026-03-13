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

        List<Seat> seats = getAvailableSeats(passengerCount, origin, destination);
        Double totalPrice = 0.0;
        if(seats.isEmpty()) {
            seats = Collections.emptyList();
        }
        else {
            totalPrice = getTotalPrice(passengerCount, origin, destination);
        }
        return new AvailabilityResponseDTO(seats, totalPrice);
    }

    @Override
    public BookingResponseDTO bookTicket(BookingRequestDTO requestDTO) {


        String bookedId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        List<Seat> seats = reserveAvailableSeats(
                requestDTO.getPassengerCount(),
                requestDTO.getOrigin(),
                requestDTO.getDestination(),
                bookedId
        );

        Double totalPrice = getTotalPrice(requestDTO.getPassengerCount(),
                requestDTO.getOrigin(), requestDTO.getDestination()
        );

        TripDetailsDTO tripDetails = new TripDetailsDTO(requestDTO.getOrigin(), requestDTO.getDestination(), requestDTO.getTravelDate());

        if (seats.isEmpty()) {
            seats = Collections.emptyList();

            return new BookingResponseDTO(bookedId, tripDetails, seats, totalPrice);
        }

        return new BookingResponseDTO(bookedId, tripDetails, seats, totalPrice);
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

                    // reserve all segments
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

        Double routePrice;

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
