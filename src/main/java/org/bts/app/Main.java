package org.bts.app;

import com.sun.net.httpserver.HttpServer;
import org.bts.app.controller.TicketController;
import org.bts.app.service.TicketService;
import org.bts.app.service.impl.TicketServiceImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        TicketService ticketService = new TicketServiceImpl();

        TicketController ticketController = new TicketController(ticketService);

        server.createContext("/api/v1/tickets", ticketController);

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();

        LOGGER.info("server started on port 8081");
    }
}