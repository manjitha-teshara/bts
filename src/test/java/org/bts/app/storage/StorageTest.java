package org.bts.app.storage;

import org.bts.app.model.Seat;
import org.bts.app.model.SeatStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {

    @Test
    void generateSegments_forwardRoute_returnsCorrectSegments() {
        List<String> segments = Storage.generateSegments("A", "C");
        assertEquals(2, segments.size());
        assertEquals("A-B", segments.get(0));
        assertEquals("B-C", segments.get(1));
    }

    @Test
    void generateSegments_backwardRoute_returnsCorrectSegments() {
        List<String> segments = Storage.generateSegments("D", "B");
        assertEquals(2, segments.size());
        assertEquals("D-C", segments.get(0));
        assertEquals("C-B", segments.get(1));
    }

    @Test
    void generateSegments_invalidOrigin_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Storage.generateSegments("X", "B")
        );
        assertEquals("Invalid station", exception.getMessage());
    }

    @Test
    void generateSegments_invalidDestination_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Storage.generateSegments("A", "Y")
        );
        assertEquals("Invalid station", exception.getMessage());
    }

    @Test
    void priceWithRouteInitialization_returnsExpectedPrices() {
        Map<String, Map<String, Double>> prices = Storage.priceWithRouteInitialization();
        
        assertNotNull(prices);
        assertTrue(prices.containsKey("A"));
        assertTrue(prices.containsKey("B"));
        assertTrue(prices.containsKey("C"));

        assertEquals(50.0, prices.get("A").get("B"));
        assertEquals(100.0, prices.get("A").get("C"));
        assertEquals(150.0, prices.get("A").get("D"));

        assertEquals(50.0, prices.get("B").get("C"));
        assertEquals(100.0, prices.get("B").get("D"));

        assertEquals(50.0, prices.get("C").get("D"));
    }

    @Test
    void seatsInitialization_returnsCorrectNumberOfSeatsAndSegments() {
        ConcurrentHashMap<String, Seat> seats = Storage.seatsInitialization();

        assertNotNull(seats);
        assertEquals(40, seats.size());

        Seat seat1A = seats.get("1A");
        assertNotNull(seat1A);
        assertEquals("1A", seat1A.getSeatId());
        assertEquals("A", seat1A.getRow());
        assertEquals(1, seat1A.getColumn());

        assertEquals(6, seat1A.getSeatSegments().size());
        assertTrue(seat1A.getSeatSegments().containsKey("A-B"));
        assertTrue(seat1A.getSeatSegments().containsKey("B-C"));
        assertTrue(seat1A.getSeatSegments().containsKey("C-D"));
        assertTrue(seat1A.getSeatSegments().containsKey("B-A"));
        assertTrue(seat1A.getSeatSegments().containsKey("C-B"));
        assertTrue(seat1A.getSeatSegments().containsKey("D-C"));

        assertEquals(SeatStatus.AVAILABLE, seat1A.getSegmentStatus("A-B"));
    }
}
