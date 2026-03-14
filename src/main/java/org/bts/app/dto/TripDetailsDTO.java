package org.bts.app.dto;
/**
 * Data Transfer Object representing a Trip details.
 *
 * @param origin The starting point of the journey.
 * @param destination The ending point of the journey.
 * */
public record TripDetailsDTO(String origin, String destination) {
}
