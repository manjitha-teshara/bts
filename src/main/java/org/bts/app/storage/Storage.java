package org.bts.app.storage;

import org.bts.app.model.Seat;
import org.bts.app.model.SeatSegment;
import org.bts.app.model.SeatStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Storage {

    private static final List<String> STATIONS = List.of("A", "B", "C", "D");

    public static List<String> generateSegments(String origin, String destination) {

        List<String> segments = new ArrayList<>();

        int startIndex = STATIONS.indexOf(origin);
        int endIndex = STATIONS.indexOf(destination);

        if (startIndex == -1 || endIndex == -1) {
            throw new IllegalArgumentException("Invalid station");
        }

        if (startIndex < endIndex) {
            for (int i = startIndex; i < endIndex; i++) {
                segments.add(STATIONS.get(i) + "-" + STATIONS.get(i + 1));
            }
        } else {
            for (int i = startIndex; i > endIndex; i--) {
                segments.add(STATIONS.get(i) + "-" + STATIONS.get(i - 1));
            }
        }

        return segments;
    }

    public static  Map<String, Map<String, Double>> priceWithRouteInitialization() {
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

    public static ConcurrentHashMap<String, Seat> seatsInitialization() {
        ConcurrentHashMap<String, Seat> seats = new ConcurrentHashMap<>();

        List<String> allSegments = new ArrayList<>();
        for (int i = 0; i < STATIONS.size() - 1; i++) {
            allSegments.add(STATIONS.get(i) + "-" + STATIONS.get(i + 1));
            allSegments.add(STATIONS.get(i + 1) + "-" + STATIONS.get(i));
        }

        for (char row = 'A'; row <= 'J'; row++) {
            for (int i = 1; i <= 4; i++) {
                String seatId = i + "" + row;

                Seat seat = new Seat();
                seat.setSeatId(seatId);
                seat.setRow(String.valueOf(row));
                seat.setColumn(i);

                for (String segment : allSegments) {
                    seat.getSeatSegments().put(segment, new SeatSegment(SeatStatus.AVAILABLE, ""));
                }

                seats.put(seatId, seat);
            }
        }
        return seats;
    }
}
