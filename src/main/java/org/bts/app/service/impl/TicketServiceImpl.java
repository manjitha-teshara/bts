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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.bts.app.model.SeatStatus.AVAILABLE;
import static org.bts.app.model.SeatStatus.RESERVED;

public class TicketServiceImpl implements TicketService {

    private static final ConcurrentHashMap<String, Seat> SEATS = seatsInitialization();

    private static final Map<String, Integer> segmentStatusIndexes = segmentStatusIndexInitialization();

    private static final Map<String, Map<String, Double>> priceWithRoute = priceWithRouteInitialization();

    @Override
    public AvailabilityResponseDTO checkAvailability(AvailabilityRequestDTO requestDTO) {
        AvailabilityResponseDTO response = new AvailabilityResponseDTO();
        String reserveId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        response.setAvailableSeats(getAvailableSeats(requestDTO.getPassengerCount(), requestDTO.getOrigin(), requestDTO.getDestination(), reserveId));
        response.setTotalPrice(getTotalPrice(requestDTO.getPassengerCount(), requestDTO.getOrigin(), requestDTO.getDestination()));
        return response;
    }

    @Override
    public BookingResponseDTO bookTicket(BookingRequestDTO requestDTO) {
        BookingResponseDTO response = new BookingResponseDTO();
        String bookedId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        response.setTicketNumber(bookedId);
        
        TripDetailsDTO tripDetails = new TripDetailsDTO();
        tripDetails.setOrigin(requestDTO.getOrigin());
        tripDetails.setDestination(requestDTO.getDestination());
        tripDetails.setTravelDate(requestDTO.getTravelDate());
        
        response.setTripDetails(tripDetails);
        response.setAssignedSeats(getAvailableSeats(requestDTO.getPassengerCount(), requestDTO.getOrigin(), requestDTO.getDestination(), bookedId));
        response.setTotalPrice(getTotalPrice(requestDTO.getPassengerCount(), requestDTO.getOrigin(), requestDTO.getDestination()));
        return response;
    }

    private List<Seat> getAvailableSeats(int passengerCount, String origin, String destination, String reserveId) {
        List<Seat> seats = new ArrayList<>();
        String segment = origin + "-" + destination;
        int passengerAdded = 0;
        Integer segmentIndex = segmentStatusIndexes.get(segment);
        List<Seat> reservedSeats = new ArrayList<>();

        for (Seat seat : SEATS.values()) {
            if(passengerCount>= passengerAdded) {
                synchronized (seat) {
                    if (seat.getSegmentStatus()[segmentIndex] == AVAILABLE) {

                        seat.getSegmentStatus()[segmentIndex] = RESERVED;
                        seat.getReservationIds()[segmentIndex] = reserveId;

                        reservedSeats.add(seat);
                        passengerAdded++;

                    }
                }
            } else {
                return seats;
            }
        }


        return seats;
    }

    private Number getTotalPrice(int passengerCount, String origin, String destination) {

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


    private int getSegmentStatusIndexInitialization(String origin, String destination) {
        String seatId = origin +
                "-" +
                destination;
        return segmentStatusIndexes.get(seatId);
    }

    private static Map<String, Integer> segmentStatusIndexInitialization() {
        Map<String, Integer> segmentIndexMap = new HashMap<>();
        segmentIndexMap.put("A-B", 0);
        segmentIndexMap.put("B-C", 1);
        segmentIndexMap.put("C-D", 2);

        segmentIndexMap.put("D-C", 3);
        segmentIndexMap.put("C-B", 4);
        segmentIndexMap.put("B-A", 5);
        return segmentIndexMap;
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
            for(int i=1;i<=5;i++){
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
