package org.bts.app;

import com.sun.net.httpserver.HttpServer;
import org.bts.app.controller.TicketController;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/v1/availability", new TicketController());
        server.createContext("/api/v1/tickets/reserve", new TicketController());

        server.setExecutor(null);
        server.start();

        System.out.println("server started on port 8080");
    }
}