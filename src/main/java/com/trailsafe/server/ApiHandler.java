package com.trailsafe.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.trailsafe.data.DatabaseManager;
import com.trailsafe.model.*;
import com.trailsafe.util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ApiHandler implements HttpHandler {
    private final DatabaseManager db = DatabaseManager.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Enable CORS
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        String method = exchange.getRequestMethod().toUpperCase();
        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        Map<String, String> queryParams = parseQueryParams(uri.getQuery());

        try {
            if (path.equals("/api/auth/login") && "POST".equals(method)) {
                handleUserLogin(exchange);
            } else if (path.equals("/api/auth/register") && "POST".equals(method)) {
                handleUserRegister(exchange);
            } else if (path.equals("/api/auth/admin-login") && "POST".equals(method)) {
                handleAdminLogin(exchange);
            } else if (path.equals("/api/routes") && "GET".equals(method)) {
                handleGetRoutes(exchange);
            } else if (path.equals("/api/trips/book") && "POST".equals(method)) {
                handleBookTrip(exchange);
            } else if (path.equals("/api/payments/sync") && "POST".equals(method)) {
                handleSyncPayment(exchange);
            } else if (path.equals("/api/payments/status") && "POST".equals(method)) {
                handleUpdatePaymentStatus(exchange);
            } else if (path.equals("/api/trips") && "GET".equals(method)) {
                handleGetUserTrips(exchange, queryParams);
            } else if (path.equals("/api/location/share") && "POST".equals(method)) {
                handleUpdateLocation(exchange);
            } else if (path.equals("/api/location/share") && "GET".equals(method)) {
                handleGetLocation(exchange, queryParams);
            } else if (path.equals("/api/sos/send") && "POST".equals(method)) {
                handleSendSOS(exchange);
            } else if (path.equals("/api/sos/list") && "GET".equals(method)) {
                handleListSOS(exchange);
            } else if (path.equals("/api/sos/status") && "POST".equals(method)) {
                handleUpdateSOSStatus(exchange);
            } else if (path.equals("/api/alerts") && "GET".equals(method)) {
                handleGetAlerts(exchange);
            } else if (path.equals("/api/alerts/create") && "POST".equals(method)) {
                handleCreateAlert(exchange);
            } else if (path.equals("/api/feedback") && "GET".equals(method)) {
                handleGetFeedback(exchange);
            } else if (path.equals("/api/feedback/submit") && "POST".equals(method)) {
                handleSubmitFeedback(exchange);
            } else if (path.equals("/api/admin/users") && "GET".equals(method)) {
                handleGetAdminUsers(exchange);
            } else if (path.equals("/api/admin/payments") && "GET".equals(method)) {
                handleGetAdminPayments(exchange);
            } else if (path.equals("/api/admin/database") && "GET".equals(method)) {
                handleGetFullDatabase(exchange);
            } else if (path.equals("/api/services") && "GET".equals(method)) {
                handleGetEmergencyServices(exchange);
            } else {
                sendJson(exchange, 404, Map.of("error", "Endpoint not found: " + path));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, Map.of("error", "Internal Server Error", "message", e.getMessage() != null ? e.getMessage() : "Error"));
        }
    }

    private void handleUserLogin(HttpExchange exchange) throws IOException {
        Map<String, Object> body = readJsonBody(exchange);
        String email = (String) body.get("email");
        String password = (String) body.get("password");

        User u = db.loginUser(email, password);
        if (u != null) {
            EmergencyContact contact = db.getEmergencyContactForUser(u.getUserId());
            Map<String, Object> resp = new LinkedHashMap<>(u.toMap());
            resp.put("emergencyContact", contact != null ? contact.toMap() : null);
            sendJson(exchange, 200, Map.of("success", true, "user", resp));
        } else {
            sendJson(exchange, 401, Map.of("success", false, "error", "Invalid email or password"));
        }
    }

    private void handleUserRegister(HttpExchange exchange) throws IOException {
        Map<String, Object> b = readJsonBody(exchange);
        String name = (String) b.get("name");
        String email = (String) b.get("email");
        String phone = (String) b.get("phone");
        String password = (String) b.get("password");
        String contactName = (String) b.get("contactName");
        String contactPhone = (String) b.get("contactPhone");
        String relationship = (String) b.get("relationship");

        if (name == null || email == null || password == null) {
            sendJson(exchange, 400, Map.of("success", false, "error", "Name, email, and password required"));
            return;
        }

        User u = db.registerUser(name, email, phone != null ? phone : "", password, contactName, contactPhone, relationship);
        if (u != null) {
            EmergencyContact contact = db.getEmergencyContactForUser(u.getUserId());
            Map<String, Object> resp = new LinkedHashMap<>(u.toMap());
            resp.put("emergencyContact", contact != null ? contact.toMap() : null);
            sendJson(exchange, 201, Map.of("success", true, "user", resp));
        } else {
            sendJson(exchange, 400, Map.of("success", false, "error", "Email already registered"));
        }
    }

    private void handleAdminLogin(HttpExchange exchange) throws IOException {
        Map<String, Object> body = readJsonBody(exchange);
        String email = (String) body.get("email");
        String password = (String) body.get("password");

        Admin admin = db.loginAdmin(email, password);
        if (admin != null) {
            sendJson(exchange, 200, Map.of("success", true, "admin", admin.toMap()));
        } else {
            sendJson(exchange, 401, Map.of("success", false, "error", "Invalid admin credentials"));
        }
    }

    private void handleGetRoutes(HttpExchange exchange) throws IOException {
        List<Map<String, Object>> routes = db.getAllRoutesWithDetails();
        sendJson(exchange, 200, Map.of("routes", routes));
    }

    private void handleBookTrip(HttpExchange exchange) throws IOException {
        Map<String, Object> body = readJsonBody(exchange);
        int userId = getInt(body, "userId", 101);
        int routeId = getInt(body, "routeId", 301);
        String startDate = (String) body.getOrDefault("startDate", "2026-10-20");
        String endDate = (String) body.getOrDefault("endDate", "2026-10-22");
        double amount = getDouble(body, "amount", 500.0);
        boolean isOffline = Boolean.TRUE.equals(body.get("isOffline"));

        Map<String, Object> res = db.bookTripAndPay(userId, routeId, startDate, endDate, amount, isOffline);
        sendJson(exchange, 201, Map.of("success", true, "booking", res));
    }

    private void handleSyncPayment(HttpExchange exchange) throws IOException {
        Map<String, Object> body = readJsonBody(exchange);
        int paymentId = getInt(body, "paymentId", 0);
        Map<String, Object> res = db.syncOfflinePayment(paymentId);
        if (res != null) {
            sendJson(exchange, 200, Map.of("success", true, "syncResult", res));
        } else {
            sendJson(exchange, 404, Map.of("success", false, "error", "Payment ID not found"));
        }
    }

    private void handleUpdatePaymentStatus(HttpExchange exchange) throws IOException {
        Map<String, Object> body = readJsonBody(exchange);
        int paymentId = getInt(body, "paymentId", 0);
        String status = (String) body.getOrDefault("status", "Success");
        Map<String, Object> res = db.updatePaymentStatus(paymentId, status);
        if (res != null) {
            sendJson(exchange, 200, Map.of("success", true, "result", res));
        } else {
            sendJson(exchange, 404, Map.of("success", false, "error", "Payment not found"));
        }
    }

    private void handleGetUserTrips(HttpExchange exchange, Map<String, String> query) throws IOException {
        int userId = query.containsKey("userId") ? Integer.parseInt(query.get("userId")) : 101;
        List<Map<String, Object>> trips = db.getUserTrips(userId);
        sendJson(exchange, 200, Map.of("trips", trips));
    }

    private void handleUpdateLocation(HttpExchange exchange) throws IOException {
        Map<String, Object> body = readJsonBody(exchange);
        int tripId = getInt(body, "tripId", 201);
        double lat = getDouble(body, "latitude", 19.9210);
        double lng = getDouble(body, "longitude", 73.8030);
        String status = (String) body.getOrDefault("status", "Active");

        LocationShare ls = db.updateLocationShare(tripId, lat, lng, status);
        sendJson(exchange, 200, Map.of("success", true, "locationShare", ls.toMap()));
    }

    private void handleGetLocation(HttpExchange exchange, Map<String, String> query) throws IOException {
        int tripId = query.containsKey("tripId") ? Integer.parseInt(query.get("tripId")) : 201;
        LocationShare ls = db.getLocationShare(tripId);
        if (ls != null) {
            sendJson(exchange, 200, Map.of("locationShare", ls.toMap()));
        } else {
            sendJson(exchange, 404, Map.of("error", "No location share for trip"));
        }
    }

    private void handleSendSOS(HttpExchange exchange) throws IOException {
        Map<String, Object> body = readJsonBody(exchange);
        int userId = getInt(body, "userId", 101);
        int tripId = getInt(body, "tripId", 201);
        int serviceId = getInt(body, "serviceId", 1201);
        double lat = getDouble(body, "latitude", 19.9300);
        double lng = getDouble(body, "longitude", 73.8100);

        SOSRequest sos = db.sendSOSRequest(userId, tripId, serviceId, lat, lng);
        sendJson(exchange, 201, Map.of("success", true, "sos", sos.toMap(), "message", "Emergency Alert Dispatched!"));
    }

    private void handleListSOS(HttpExchange exchange) throws IOException {
        List<Map<String, Object>> list = db.getAllSOSRequestsWithDetails();
        sendJson(exchange, 200, Map.of("sosRequests", list));
    }

    private void handleUpdateSOSStatus(HttpExchange exchange) throws IOException {
        Map<String, Object> body = readJsonBody(exchange);
        int sosId = getInt(body, "sosId", 0);
        String status = (String) body.getOrDefault("status", "Acknowledged");

        boolean ok = db.updateSOSStatus(sosId, status);
        sendJson(exchange, 200, Map.of("success", ok, "status", status));
    }

    private void handleGetAlerts(HttpExchange exchange) throws IOException {
        List<Map<String, Object>> alerts = db.getAllSafetyAlerts();
        sendJson(exchange, 200, Map.of("alerts", alerts));
    }

    private void handleCreateAlert(HttpExchange exchange) throws IOException {
        Map<String, Object> body = readJsonBody(exchange);
        int adminId = getInt(body, "adminId", 1);
        int routeId = getInt(body, "routeId", 301);
        String message = (String) body.getOrDefault("message", "Precautionary safety advisory issued.");
        String severity = (String) body.getOrDefault("severity", "Medium");

        SafetyAlert alert = db.issueSafetyAlert(adminId, routeId, message, severity);
        sendJson(exchange, 201, Map.of("success", true, "alert", alert.toMap()));
    }

    private void handleGetFeedback(HttpExchange exchange) throws IOException {
        List<Map<String, Object>> list = db.getAllFeedbacks();
        sendJson(exchange, 200, Map.of("feedback", list));
    }

    private void handleSubmitFeedback(HttpExchange exchange) throws IOException {
        Map<String, Object> body = readJsonBody(exchange);
        int userId = getInt(body, "userId", 101);
        int rating = getInt(body, "rating", 5);
        String comments = (String) body.getOrDefault("comments", "Great trail!");

        Feedback fb = db.submitFeedback(userId, rating, comments);
        sendJson(exchange, 201, Map.of("success", true, "feedback", fb.toMap()));
    }

    private void handleGetAdminUsers(HttpExchange exchange) throws IOException {
        List<Map<String, Object>> users = db.getAllUsersWithContacts();
        sendJson(exchange, 200, Map.of("users", users));
    }

    private void handleGetAdminPayments(HttpExchange exchange) throws IOException {
        List<Map<String, Object>> payments = db.getAllPayments();
        sendJson(exchange, 200, Map.of("payments", payments));
    }

    private void handleGetEmergencyServices(HttpExchange exchange) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (EmergencyService es : db.emergencyServices.values()) {
            list.add(es.toMap());
        }
        sendJson(exchange, 200, Map.of("services", list));
    }

    private void handleGetFullDatabase(HttpExchange exchange) throws IOException {
        Map<String, List<Map<String, Object>>> dbDump = db.getFullDatabaseDump();
        sendJson(exchange, 200, Map.of(
            "success", true,
            "databaseName", "trailsafe_db",
            "totalTables", dbDump.size(),
            "tables", dbDump
        ));
    }

    // --- Helpers ---
    private Map<String, Object> readJsonBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return JsonUtil.parseObject(json);
        }
    }

    private void sendJson(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String json = JsonUtil.toJson(data);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String pair : query.split("&")) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                map.put(pair.substring(0, idx), pair.substring(idx + 1));
            }
        }
        return map;
    }

    private int getInt(Map<String, Object> map, String key, int def) {
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        if (val instanceof String) {
            try { return Integer.parseInt((String) val); } catch (Exception ignored) {}
        }
        return def;
    }

    private double getDouble(Map<String, Object> map, String key, double def) {
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        if (val instanceof String) {
            try { return Double.parseDouble((String) val); } catch (Exception ignored) {}
        }
        return def;
    }
}
