package com.trailsafe.server;

import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class TrailSafeServer {
    public static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portEnv.trim());
            } catch (NumberFormatException ignored) {}
        } else if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // Serve static files from "public" directory
            server.createContext("/", new StaticFileHandler("public"));

            // REST API endpoints
            server.createContext("/api/", new ApiHandler());

            server.setExecutor(Executors.newFixedThreadPool(16));
            server.start();

            System.out.println("==================================================================");
            System.out.println("   TrailSafe - Smart Trekking & Tourist Safety System Server");
            System.out.println("==================================================================");
            System.out.println(" Backend: Java (Standard JDK HttpServer)");
            System.out.println(" Database: 15-Table Relational Schema Loaded");
            System.out.println(" Server URL: http://localhost:" + port);
            System.out.println(" Open your browser and navigate to: http://localhost:" + port);
            System.out.println(" Press Ctrl+C in this console to stop the server.");
            System.out.println("==================================================================");

        } catch (IOException e) {
            System.err.println("Failed to start TrailSafe server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
