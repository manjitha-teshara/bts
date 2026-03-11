package org.bts.app.dto;

import java.time.LocalDate;

public class BookingRequestDTO {
    private String origin;
    private String destination;
    private int passengerCount;
    private LocalDate travelDate;
    private boolean priceConfirmation;

}
