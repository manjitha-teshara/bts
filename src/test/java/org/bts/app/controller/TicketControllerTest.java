package org.bts.app.controller;

import com.sun.net.httpserver.HttpServer;
import org.bts.app.Utils.JsonUtils;
import org.bts.app.dto.AvailabilityResponseDTO;
import org.bts.app.dto.ReserveRequestDTO;
import org.bts.app.dto.ReserveResponseDTO;
import org.bts.app.dto.TripDetailsDTO;
import org.bts.app.service.TicketService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TicketControllerTest {

    private static HttpServer server;
    private static TicketService ticketService;
    private static int port;
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeAll
    static void startServer() throws Exception {
        ticketService = mock(TicketService.class);
        TicketController controller = new TicketController(ticketService);

        // Start server on an ephemeral port
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/api/v1/tickets/availability", controller);
        server.createContext("/api/v1/tickets/reserve", controller);
        server.setExecutor(null);
        server.start();

        port = server.getAddress().getPort();
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void handleAvailability_validRequest_returns200() throws Exception {
        AvailabilityResponseDTO mockResponse = new AvailabilityResponseDTO(Collections.emptyList(), 100.0);
        when(ticketService.checkAvailability(2, "A", "B")).thenReturn(mockResponse);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/tickets/availability?origin=A&destination=B&passengerCount=2"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("100.0"));
    }

    @Test
    void handleAvailability_missingParams_returns400() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/tickets/availability?origin=A"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Missing required query parameters"));
    }

    @Test
    void handleReservation_validRequest_returns201() throws Exception {
        ReserveResponseDTO mockResponse = new ReserveResponseDTO("TKT-123", new TripDetailsDTO("A", "B"), Collections.emptyList(), 100.0);
        when(ticketService.reserveTicket(any(ReserveRequestDTO.class))).thenReturn(mockResponse);

        ReserveRequestDTO reqDto = new ReserveRequestDTO("A", "B", 2, 100.0);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/tickets/reserve"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(reqDto)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("TKT-123"));
    }

    @Test
    void handleReservation_invalidBody_returns400() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/tickets/reserve"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Request body cannot be empty"));
    }

    @Test
    void handle_exceptionInService_returns500() throws Exception {
        when(ticketService.checkAvailability(anyInt(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Unexpected Database Error"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/tickets/availability?origin=A&destination=B&passengerCount=2"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
        assertTrue(response.body().contains("Internal Server Error"));
    }
}
