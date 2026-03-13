package org.bts.app.service.impl;

import org.bts.app.dto.AvailabilityRequestDTO;
import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.BookingRequestDTO;
import org.bts.app.dto.BookingResponseDTO;
import org.bts.app.dto.TripDetailsDTO;
import org.bts.app.model.Seat;
import org.bts.app.model.SeatStatus;
import org.bts.app.service.TicketService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.bts.app.model.SeatStatus.AVAILABLE;
import static org.bts.app.model.SeatStatus.RESERVED;

public class TicketServiceImpl implements TicketService {

    private static final ConcurrentHashMap<String, Seat> SEATS = seatsInitialization();

    private static final Map<String, Map<String, Integer[]>> segmentStatusIndexes = segmentStatusIndexInitialization();

    private static final Map<String, Map<String, Double>> priceWithRoute = priceWithRouteInitialization();

    @Override
    public AvailabilityResponseDTO checkAvailability(AvailabilityRequestDTO requestDTO) {
        AvailabilityResponseDTO response = new AvailabilityResponseDTO();
        List<Seat> seats = getAvailableSeats(requestDTO.getPassengerCount(), requestDTO.getOrigin(), requestDTO.getDestination());
        if(seats.isEmpty()) {
            response.setAvailableSeats(Collections.emptyList());
            response.setTotalPrice(0.0);
        }
        else {
            response.setAvailableSeats(seats);
            response.setTotalPrice(getTotalPrice(requestDTO.getPassengerCount(), requestDTO.getOrigin(), requestDTO.getDestination()));
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


    private static Map<String, Map<String, Integer[]>> segmentStatusIndexInitialization() {

        Map<String, Map<String, Integer[]>> segmentIndex = new HashMap<>();

        Map<String, Integer[]> fromA = new HashMap<>();
        fromA.put("B", new Integer[]{0});
        fromA.put("C", new Integer[]{0, 1});
        fromA.put("D", new Integer[]{0, 1, 2});
        segmentIndex.put("A", fromA);

        Map<String, Integer[]> fromB = new HashMap<>();
        fromB.put("C", new Integer[]{1});
        fromB.put("D", new Integer[]{1, 2});
        fromB.put("A", new Integer[]{5});
        segmentIndex.put("B", fromB);

        Map<String, Integer[]> fromC = new HashMap<>();
        fromC.put("D", new Integer[]{2});
        fromC.put("B", new Integer[]{4});
        fromC.put("A", new Integer[]{4, 5});
        segmentIndex.put("C", fromC);

        Map<String, Integer[]> fromD = new HashMap<>();
        fromD.put("C", new Integer[]{3});
        fromD.put("B", new Integer[]{3, 4});
        fromD.put("A", new Integer[]{3, 4, 5});
        segmentIndex.put("D", fromD);

        return segmentIndex;
    }

    private static  Map<String, Map<String, Double>> priceWithRouteInitialization() {
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

        return priceWithRoute;
    }

    private static ConcurrentHashMap<String, Seat> seatsInitialization() {
        ConcurrentHashMap<String, Seat> seats = new ConcurrentHashMap<>();

        for(char row='A'; row<='J'; row++){
            for(int i=1;i<5;i++){
                String seatId = row + "" + i;

                Seat seat = new Seat();
                seat.setSeatId(seatId);
                seat.setRow(String.valueOf(row));
                seat.setColumn(i);
                SeatStatus[] segmentStatusInit = {AVAILABLE, AVAILABLE, AVAILABLE, AVAILABLE, AVAILABLE, AVAILABLE};
                seat.setSegmentStatus(segmentStatusInit);
                seats.put(seatId, seat);
            }
        }
        return seats;
    }

}
