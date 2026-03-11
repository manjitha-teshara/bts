package org.bts.app.model;

public enum SeatStatus {

    AVAILABLE, //seat is free and can be reserved or booked
    RESERVED, //seat is temporarily held for a user but not yet confirmed
    BOOKED //seat is fully confirmed and cannot be taken by others

}
