package com.trailsafe.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticFileHandler implements HttpHandler {
    private final Path publicDir;

    public StaticFileHandler(String publicDirPath) {
        this.publicDir = Path.of(publicDirPath).toAbsolutePath().normalize();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path == null || path.equals("/") || path.isEmpty()) {
            path = "/index.html";
        }

        // Prevent directory traversal
        Path targetPath = publicDir.resolve(path.substring(1)).normalize();
        if (!targetPath.startsWith(publicDir)) {
            sendResponse(exchange, 403, "Access Denied".getBytes(), "text/plain");
            return;
        }

        if (!Files.exists(targetPath) || Files.isDirectory(targetPath)) {
            // Fallback to index.html for SPA
            targetPath = publicDir.resolve("index.html");
        }

        if (!Files.exists(targetPath)) {
            sendResponse(exchange, 404, "File Not Found".getBytes(), "text/plain");
            return;
        }

        String mimeType = getMimeType(targetPath.toString());
        byte[] bytes = Files.readAllBytes(targetPath);
        sendResponse(exchange, 200, bytes, mimeType);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, byte[] bytes, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String getMimeType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}
