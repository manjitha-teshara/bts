package org.bts.app.storage;

import org.bts.app.model.Seat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Storage {

    public static Map<String, Map<String, Integer[]>> segmentStatusIndexInitialization() {

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

        for (char row = 'A'; row <= 'J'; row++) {
            for (int i = 1; i <= 4; i++) {
                String seatId = row + "" + i;

                Seat seat = new Seat();
                seat.setSeatId(seatId);
                seat.setRow(String.valueOf(row));
                seat.setColumn(i);
                
                seats.put(seatId, seat);
            }
        }
        return seats;
    }
}
