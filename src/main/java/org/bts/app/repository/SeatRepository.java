package org.bts.app.repository;

import org.bts.app.model.Seat;
import org.bts.app.model.SeatStatus;

import java.util.HashMap;
import java.util.Map;

public interface SeatRepository {

//    private Map<String, Seat> seatsInitialization() {
//        Map<String, Seat> seats = new HashMap<>();
//
//        for(char row='A'; row<='J'; row++){
//            for(int i=1;i<=5;i++){
//                String seatId = row + "" + i;
//
//                Seat seat = new Seat();
//                seat.setSeatId(seatId);
//                seat.setRow(String.valueOf(row));
//                seat.setColumn(i);
//                seat.setStatus(SeatStatus.AVAILABLE);
//
//                seats.put(seatId, seat);
//            }
//        }
//        return seats;
//    }
}
