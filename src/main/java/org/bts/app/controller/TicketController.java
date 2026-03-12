package org.bts.app.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bts.app.Utils.JsonUtils;
import org.bts.app.dto.AvailabilityRequestDTO;
import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.BookingRequestDTO;
import org.bts.app.dto.BookingResponseDTO;
import org.bts.app.service.TicketService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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
            if(path.equals("/api/v1/tickets/availability") && method.equals("POST")) {
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
        InputStream requestBody = exchange.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

        AvailabilityRequestDTO requestDTO = JsonUtils.fromJson(body, AvailabilityRequestDTO.class);

        AvailabilityResponseDTO responseDTO = service.checkAvailability(requestDTO);

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
    }
