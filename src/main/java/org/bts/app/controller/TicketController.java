package org.bts.app.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bts.app.Utils.JsonUtils;
import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.BookingRequestDTO;
import org.bts.app.dto.BookingResponseDTO;
import org.bts.app.service.TicketService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TicketController implements HttpHandler {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if(path.equals("/api/v1/tickets/availability") && method.equals("GET")) {
                handleAvailability(exchange);
            } else if (path.equals("/api/v1/tickets/reserve") && method.equals("POST")) {
                handleReservation(exchange);
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }


    private void handleAvailability(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());

        String origin = params.get("origin");
        String destination = params.get("destination");
        int passengerCount = Integer.parseInt(params.get("passengerCount"));
        String travelDate = params.get("travelDate");

        AvailabilityResponseDTO responseDTO = service.checkAvailability(passengerCount, origin, destination, travelDate);

        String responseJson = JsonUtils.toJson(responseDTO);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseJson.getBytes().length);

        OutputStream os = exchange.getResponseBody();
        os.write(responseJson.getBytes());
        os.close();

    }

    private void handleReservation(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

        BookingRequestDTO requestDTO = JsonUtils.fromJson(body, BookingRequestDTO.class);

        BookingResponseDTO responseDTO = service.bookTicket(requestDTO);

        String responseJson = JsonUtils.toJson(responseDTO);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseJson.getBytes().length);

        OutputStream os = exchange.getResponseBody();
        os.write(responseJson.getBytes());
        os.close();
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
                params.put(keyValue[0], keyValue[1]);
            }
        }

        return params;
    }
    }
