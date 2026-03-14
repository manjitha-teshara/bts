package org.bts.app.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bts.app.Utils.JsonUtils;
import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.BookingRequestDTO;
import org.bts.app.dto.BookingResponseDTO;
import org.bts.app.dto.ErrorResponseDTO;
import org.bts.app.service.TicketService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketController implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger(TicketController.class.getName());
    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (path.equals("/api/v1/tickets/availability") && method.equals("GET")) {
                handleAvailability(exchange);
            } else if (path.equals("/api/v1/tickets/reserve") && method.equals("POST")) {
                handleReservation(exchange);
            } else {
                writeResponse(exchange, 404, new ErrorResponseDTO("Endpoint not found", 404));
            }
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Bad Request: " + e.getMessage());
            writeResponse(exchange, 400, new ErrorResponseDTO(e.getMessage(), 400));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Internal Server Error", e);
            writeResponse(exchange, 500, new ErrorResponseDTO("Internal Server Error", 500));
        }
    }

    private void handleAvailability(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());

        String origin = params.get("origin");
        String destination = params.get("destination");
        String passengerCountStr = params.get("passengerCount");
        String travelDate = params.get("travelDate");

        if (origin == null || destination == null || passengerCountStr == null || travelDate == null) {
            throw new IllegalArgumentException("Missing required query parameters: origin, destination, passengerCount, travelDate");
        }

        int passengerCount;
        try {
            passengerCount = Integer.parseInt(passengerCountStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid passengerCount: must be an integer");
        }

        AvailabilityResponseDTO responseDTO = service.checkAvailability(passengerCount, origin, destination, travelDate);
        writeResponse(exchange, 200, responseDTO);
    }

    private void handleReservation(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

        if (body.isEmpty()) {
            throw new IllegalArgumentException("Request body cannot be empty");
        }

        BookingRequestDTO requestDTO = JsonUtils.fromJson(body, BookingRequestDTO.class);
        
        // Basic validation for DTO fields
        if (requestDTO.getOrigin() == null || requestDTO.getDestination() == null || requestDTO.getPassengerCount() <= 0) {
             throw new IllegalArgumentException("Invalid booking request: origin, destination, and passengerCount are required");
        }

        BookingResponseDTO responseDTO = service.bookTicket(requestDTO);
        writeResponse(exchange, 201, responseDTO);
    }

    private void writeResponse(HttpExchange exchange, int statusCode, Object responseBody) throws IOException {
        String responseJson = JsonUtils.toJson(responseBody);
        byte[] responseBytes = responseJson.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(
                    URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                );
            }
        }
        return params;
    }
}
