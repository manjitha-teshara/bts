package org.bts.app.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a request to book bus tickets.
 *
 * @param origin The starting point of the journey.
 * @param destination The ending point of the journey.
 * @param passengerCount The number of passengers booking tickets.
 * @param priceConfirmation Flag indicating if the user confirmed the price.
 */
public record ReserveRequestDTO(
        String origin,
        String destination,
        int passengerCount,
        boolean priceConfirmation
) {}
