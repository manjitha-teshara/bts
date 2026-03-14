package org.bts.app;

import com.sun.net.httpserver.HttpServer;
import org.bts.app.controller.TicketController;
import org.bts.app.service.TicketService;
import org.bts.app.service.impl.TicketServiceImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        TicketService ticketService = new TicketServiceImpl();

        TicketController ticketController = new TicketController(ticketService);

        server.createContext("/api/v1/tickets", ticketController);

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        LOGGER.info("server started on port 8080");

        // Schedule the automated midnight system reset
        scheduleMidnightReset(ticketService);
    }

    private static void scheduleMidnightReset(TicketService ticketService) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable resetTask = () -> {
            try {
                LOGGER.info("Running scheduled end of day system refresh...");
                ticketService.resetSystem();
            } catch (Exception e) {
                LOGGER.severe("Scheduled system refresh failed: " + e.getMessage());
            }
        };

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(now.getZone());
        long initialDelay = java.time.Duration.between(now, nextMidnight).toMillis();
        long period = TimeUnit.DAYS.toMillis(1);

        scheduler.scheduleAtFixedRate(resetTask, initialDelay, period, TimeUnit.MILLISECONDS);
        LOGGER.info("Scheduled automated system refresh initialized. First run in " + (initialDelay / 60000) + " minutes.");
    }
}