package com.trailsafe.data;

import com.trailsafe.model.*;
import com.trailsafe.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe Data Manager maintaining relational integrity for all 15 tables.
 * Seeds data according to Chapter 4.4.4 Object Diagram and persists state.
 */
public class DatabaseManager {
    private static DatabaseManager instance;

    // Relational entity stores (Table -> Map<ID, Entity>)
    public final Map<Integer, Admin> admins = new ConcurrentHashMap<>();
    public final Map<Integer, User> users = new ConcurrentHashMap<>();
    public final Map<Integer, EmergencyContact> emergencyContacts = new ConcurrentHashMap<>();
    public final Map<Integer, TrekkingRoute> routes = new ConcurrentHashMap<>();
    public final Map<Integer, Trip> trips = new ConcurrentHashMap<>();
    public final Map<Integer, Checkpoint> checkpoints = new ConcurrentHashMap<>();
    public final Map<Integer, RouteFacility> routeFacilities = new ConcurrentHashMap<>();
    public final Map<Integer, WeatherInfo> weatherInfos = new ConcurrentHashMap<>();
    public final Map<Integer, SafetyAlert> safetyAlerts = new ConcurrentHashMap<>();
    public final Map<Integer, LocationShare> locationShares = new ConcurrentHashMap<>();
    public final Map<Integer, Payment> payments = new ConcurrentHashMap<>();
    public final Map<Integer, PaymentSync> paymentSyncs = new ConcurrentHashMap<>();
    public final Map<Integer, EmergencyService> emergencyServices = new ConcurrentHashMap<>();
    public final Map<Integer, SOSRequest> sosRequests = new ConcurrentHashMap<>();
    public final Map<Integer, Feedback> feedbacks = new ConcurrentHashMap<>();

    // Primary key auto-increment generators
    private final AtomicInteger adminIdGen = new AtomicInteger(10);
    private final AtomicInteger userIdGen = new AtomicInteger(200);
    private final AtomicInteger contactIdGen = new AtomicInteger(1400);
    private final AtomicInteger routeIdGen = new AtomicInteger(400);
    private final AtomicInteger tripIdGen = new AtomicInteger(300);
    private final AtomicInteger checkpointIdGen = new AtomicInteger(500);
    private final AtomicInteger facilityIdGen = new AtomicInteger(900);
    private final AtomicInteger weatherIdGen = new AtomicInteger(1000);
    private final AtomicInteger alertIdGen = new AtomicInteger(1100);
    private final AtomicInteger shareIdGen = new AtomicInteger(800);
    private final AtomicInteger paymentIdGen = new AtomicInteger(600);
    private final AtomicInteger syncIdGen = new AtomicInteger(700);
    private final AtomicInteger serviceIdGen = new AtomicInteger(1300);
    private final AtomicInteger sosIdGen = new AtomicInteger(1200);
    private final AtomicInteger feedbackIdGen = new AtomicInteger(1500);

    private final Path persistPath = Path.of("database", "data_store.json");

    private DatabaseManager() {
        seedInitialData();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Seeds initial records matching Chapter 4.4.4 Object diagram and realistic trek data.
     */
    private void seedInitialData() {
        // 1. Admin
        admins.put(1, new Admin(1, "TrailSafe Control Officer", "admin@trailsafe.com", "admin123"));

        // 2. Users
        users.put(101, new User(101, "Roshan", "roshan@gmail.com", "9876543210", "password123"));
        users.put(102, new User(102, "Priya Sharma", "priya@example.com", "9822011223", "pass123"));
        users.put(103, new User(103, "Aarav Patel", "aarav@example.com", "9890123456", "pass123"));

        // 3. Emergency Contacts
        emergencyContacts.put(1301, new EmergencyContact(1301, 101, "Amit", "9876543210", "Friend"));
        emergencyContacts.put(1302, new EmergencyContact(1302, 102, "Ramesh Sharma", "9822998877", "Father"));
        emergencyContacts.put(1303, new EmergencyContact(1303, 103, "Neha Patel", "9890998811", "Sister"));

        // 4. Trekking Routes
        routes.put(301, new TrekkingRoute(301, "Harishchandragad", "Medium", 12.50));
        routes.put(302, new TrekkingRoute(302, "Rajmachi Fort Trail", "Easy", 14.00));
        routes.put(303, new TrekkingRoute(303, "Kalsubai Peak Trek", "Hard", 6.60));
        routes.put(304, new TrekkingRoute(304, "Sinhagad Valley Path", "Easy", 5.20));
        routes.put(305, new TrekkingRoute(305, "Sandhan Valley Canyon", "Hard", 9.80));

        // 5. Checkpoints
        checkpoints.put(401, new Checkpoint(401, 301, "Base Camp", 19.9250, 73.7950));
        checkpoints.put(402, new Checkpoint(402, 301, "Tolar Khind Pass", 19.3872, 73.7820));
        checkpoints.put(403, new Checkpoint(403, 301, "Harishchandreshwar Temple", 19.3900, 73.7780));
        checkpoints.put(404, new Checkpoint(404, 301, "Konkan Kada Cliff Edge", 19.3930, 73.7740));

        checkpoints.put(405, new Checkpoint(405, 302, "Udhewadi Base Village", 18.8285, 73.3980));
        checkpoints.put(406, new Checkpoint(406, 302, "Shrivardhan View Point", 18.8310, 73.4020));
        checkpoints.put(407, new Checkpoint(407, 302, "Manaranjan Fort Gate", 18.8260, 73.3940));

        checkpoints.put(408, new Checkpoint(408, 303, "Bari Village Trailhead", 19.6015, 73.7042));
        checkpoints.put(409, new Checkpoint(409, 303, "Iron Ladder Checkpoint 1", 19.6010, 73.7090));
        checkpoints.put(410, new Checkpoint(410, 303, "Kalsubai Summit (Highest Peak)", 19.6003, 73.7145));

        // 6. Route Facilities
        routeFacilities.put(801, new RouteFacility(801, 301, "Water Point", "Water point", "Km 3"));
        routeFacilities.put(802, new RouteFacility(802, 301, "Forest Rest Shelter", "Shelter", "Tolar Khind"));
        routeFacilities.put(803, new RouteFacility(803, 301, "First-Aid Post", "Medical", "Base Camp"));

        routeFacilities.put(804, new RouteFacility(804, 302, "Fresh Spring Water Point", "Water point", "Km 5"));
        routeFacilities.put(805, new RouteFacility(805, 302, "Village Homestay Shelter", "Shelter", "Udhewadi"));

        routeFacilities.put(806, new RouteFacility(806, 303, "Emergency Medical Tent", "Medical", "Bari Base"));
        routeFacilities.put(807, new RouteFacility(807, 303, "Hydration & Snack Shed", "Refreshment", "Midway Ladder"));

        // 7. Weather Info
        weatherInfos.put(901, new WeatherInfo(901, 301, "Cloudy", 22.0, "2026-10-16"));
        weatherInfos.put(902, new WeatherInfo(902, 302, "Sunny & Breezy", 26.5, "2026-10-16"));
        weatherInfos.put(903, new WeatherInfo(903, 303, "Mist & Strong Winds", 18.5, "2026-10-16"));
        weatherInfos.put(904, new WeatherInfo(904, 304, "Clear Skies", 28.0, "2026-10-16"));
        weatherInfos.put(905, new WeatherInfo(905, 305, "Humid / Rocky shade", 24.0, "2026-10-16"));

        // 8. Emergency Services
        emergencyServices.put(1201, new EmergencyService(1201, "Mountain Rescue Team", "112", "Rescue"));
        emergencyServices.put(1202, new EmergencyService(1202, "Forest Police Rapid Response", "100", "Police"));
        emergencyServices.put(1203, new EmergencyService(1203, "Sahyadri Wilderness First Responders", "108", "Medical"));

        // 9. Safety Alerts
        safetyAlerts.put(1001, new SafetyAlert(1001, 1, 301, "Heavy Rain Alert: Slippery rocks near Tolar Khind. Trek with high traction shoes and poles.", "High", "2026-10-15 07:00:00"));
        safetyAlerts.put(1002, new SafetyAlert(1002, 1, 303, "High wind speeds reported near ridge stairs. Trek in paired groups.", "Medium", "2026-10-16 08:30:00"));

        // 10. Trips
        trips.put(201, new Trip(201, 101, 301, "2026-10-15", "2026-10-18", "Planned"));
        trips.put(202, new Trip(202, 102, 302, "2026-10-20", "2026-10-21", "Planned"));

        // 11. Location Share
        locationShares.put(701, new LocationShare(701, 201, 19.9210, 73.8030, "2026-10-15 09:30:00", "Active"));

        // 12. Payment
        payments.put(501, new Payment(501, 101, 201, 500.00, "2026-10-15", "Success"));
        payments.put(502, new Payment(502, 102, 202, 350.00, "2026-10-16", "Success"));

        // 13. Payment Sync
        paymentSyncs.put(601, new PaymentSync(601, 501, "Completed", "2026-10-15"));
        paymentSyncs.put(602, new PaymentSync(602, 502, "Completed", "2026-10-16"));

        // 14. SOS Request
        sosRequests.put(1101, new SOSRequest(1101, 101, 201, 1201, "2026-10-15 11:45:00", 19.9300, 73.8100, "Sent"));

        // 15. Feedback
        feedbacks.put(1401, new Feedback(1401, 101, 5, "Great experience! Route checkpoints and SOS feature provided immense peace of mind.", "2026-10-15"));
    }

    // --- Authentication ---
    public User loginUser(String email, String password) {
        for (User u : users.values()) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public Admin loginAdmin(String email, String password) {
        for (Admin a : admins.values()) {
            if (a.getEmail().equalsIgnoreCase(email) && a.getPassword().equals(password)) {
                return a;
            }
        }
        return null;
    }

    public synchronized User registerUser(String name, String email, String phone, String password,
                                         String contactName, String contactPhone, String relationship) {
        for (User u : users.values()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return null; // email already exists
            }
        }
        int newUserId = userIdGen.incrementAndGet();
        User user = new User(newUserId, name, email, phone, password);
        users.put(newUserId, user);

        if (contactName != null && !contactName.trim().isEmpty()) {
            int newContactId = contactIdGen.incrementAndGet();
            EmergencyContact contact = new EmergencyContact(newContactId, newUserId, contactName,
                    (contactPhone != null ? contactPhone : phone),
                    (relationship != null ? relationship : "Family"));
            emergencyContacts.put(newContactId, contact);
        }
        return user;
    }

    public EmergencyContact getEmergencyContactForUser(int userId) {
        for (EmergencyContact ec : emergencyContacts.values()) {
            if (ec.getUserId() == userId) {
                return ec;
            }
        }
        return null;
    }

    // --- Routes and composite data ---
    public List<Map<String, Object>> getAllRoutesWithDetails() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (TrekkingRoute route : routes.values()) {
            Map<String, Object> map = route.toMap();

            // Checkpoints
            List<Map<String, Object>> cps = new ArrayList<>();
            for (Checkpoint cp : checkpoints.values()) {
                if (cp.getRouteId() == route.getRouteId()) {
                    cps.add(cp.toMap());
                }
            }
            map.put("checkpoints", cps);

            // Facilities
            List<Map<String, Object>> facs = new ArrayList<>();
            for (RouteFacility f : routeFacilities.values()) {
                if (f.getRouteId() == route.getRouteId()) {
                    facs.add(f.toMap());
                }
            }
            map.put("facilities", facs);

            // Weather
            WeatherInfo weather = null;
            for (WeatherInfo w : weatherInfos.values()) {
                if (w.getRouteId() == route.getRouteId()) {
                    weather = w;
                    break;
                }
            }
            map.put("weather", weather != null ? weather.toMap() : null);

            // Alerts
            List<Map<String, Object>> alerts = new ArrayList<>();
            for (SafetyAlert a : safetyAlerts.values()) {
                if (a.getRouteId() == route.getRouteId()) {
                    alerts.add(a.toMap());
                }
            }
            map.put("alerts", alerts);

            result.add(map);
        }
        return result;
    }

    // --- Trip Booking & Payment ---
    public synchronized Map<String, Object> bookTripAndPay(int userId, int routeId, String startDate,
                                                          String endDate, double amount, boolean isOffline) {
        int tripId = tripIdGen.incrementAndGet();
        Trip trip = new Trip(tripId, userId, routeId, startDate, endDate, "Planned");
        trips.put(tripId, trip);

        int paymentId = paymentIdGen.incrementAndGet();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String status = isOffline ? "Pending" : "Success";
        Payment payment = new Payment(paymentId, userId, tripId, amount, today, status);
        payments.put(paymentId, payment);

        PaymentSync sync = null;
        if (!isOffline) {
            int syncId = syncIdGen.incrementAndGet();
            sync = new PaymentSync(syncId, paymentId, "Completed", today);
            paymentSyncs.put(syncId, sync);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("trip", trip.toMap());
        response.put("payment", payment.toMap());
        if (sync != null) response.put("sync", sync.toMap());
        response.put("route", routes.get(routeId) != null ? routes.get(routeId).toMap() : null);
        return response;
    }

    public synchronized Map<String, Object> updatePaymentStatus(int paymentId, String status) {
        Payment p = payments.get(paymentId);
        if (p == null) return null;
        p.setStatus(status);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        PaymentSync sync = null;
        if ("Success".equalsIgnoreCase(status)) {
            for (PaymentSync ps : paymentSyncs.values()) {
                if (ps.getPaymentId() == paymentId) {
                    ps.setSyncStatus("Completed");
                    ps.setLastSyncedAt(today);
                    sync = ps;
                    break;
                }
            }
            if (sync == null) {
                int syncId = syncIdGen.incrementAndGet();
                sync = new PaymentSync(syncId, paymentId, "Completed", today);
                paymentSyncs.put(syncId, sync);
            }
            Trip t = trips.get(p.getTripId());
            if (t != null) {
                t.setStatus("Planned");
            }
        } else if ("Failed".equalsIgnoreCase(status)) {
            Trip t = trips.get(p.getTripId());
            if (t != null) {
                t.setStatus("Payment Failed");
            }
        }
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("payment", p.toMap());
        if (sync != null) res.put("sync", sync.toMap());
        return res;
    }

    public synchronized Map<String, Object> syncOfflinePayment(int paymentId) {
        Payment p = payments.get(paymentId);
        if (p == null) return null;
        p.setStatus("Success");
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        int syncId = syncIdGen.incrementAndGet();
        PaymentSync sync = new PaymentSync(syncId, paymentId, "Completed", today);
        paymentSyncs.put(syncId, sync);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("payment", p.toMap());
        res.put("sync", sync.toMap());
        return res;
    }

    public List<Map<String, Object>> getUserTrips(int userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Trip t : trips.values()) {
            if (t.getUserId() == userId) {
                Map<String, Object> item = t.toMap();
                TrekkingRoute r = routes.get(t.getRouteId());
                item.put("route", r != null ? r.toMap() : null);

                // Find corresponding payment
                for (Payment p : payments.values()) {
                    if (p.getTripId() == t.getTripId()) {
                        item.put("payment", p.toMap());
                        // Find sync
                        for (PaymentSync ps : paymentSyncs.values()) {
                            if (ps.getPaymentId() == p.getPaymentId()) {
                                item.put("sync", ps.toMap());
                                break;
                            }
                        }
                        break;
                    }
                }
                list.add(item);
            }
        }
        return list;
    }

    // --- Location Sharing ---
    public synchronized LocationShare updateLocationShare(int tripId, double lat, double lng, String status) {
        for (LocationShare ls : locationShares.values()) {
            if (ls.getTripId() == tripId) {
                ls.setLatitude(lat);
                ls.setLongitude(lng);
                ls.setStatus(status != null ? status : "Active");
                ls.setSharedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                return ls;
            }
        }
        int newShareId = shareIdGen.incrementAndGet();
        LocationShare ls = new LocationShare(newShareId, tripId, lat, lng,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                status != null ? status : "Active");
        locationShares.put(newShareId, ls);
        return ls;
    }

    public LocationShare getLocationShare(int tripId) {
        for (LocationShare ls : locationShares.values()) {
            if (ls.getTripId() == tripId) {
                return ls;
            }
        }
        return null;
    }

    // --- SOS Request ---
    public synchronized SOSRequest sendSOSRequest(int userId, int tripId, int serviceId, double lat, double lng) {
        int newSosId = sosIdGen.incrementAndGet();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        SOSRequest req = new SOSRequest(newSosId, userId, tripId, serviceId, time, lat, lng, "Sent");
        sosRequests.put(newSosId, req);
        return req;
    }

    public synchronized boolean updateSOSStatus(int sosId, String status) {
        SOSRequest req = sosRequests.get(sosId);
        if (req != null) {
            req.setStatus(status);
            return true;
        }
        return false;
    }

    public List<Map<String, Object>> getAllSOSRequestsWithDetails() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SOSRequest req : sosRequests.values()) {
            Map<String, Object> map = req.toMap();
            User u = users.get(req.getUserId());
            map.put("userName", u != null ? u.getName() : "Unknown");
            map.put("userPhone", u != null ? u.getPhone() : "N/A");
            EmergencyService s = emergencyServices.get(req.getServiceId());
            map.put("serviceName", s != null ? s.getServiceName() : "Emergency Line");
            map.put("servicePhone", s != null ? s.getPhone() : "112");
            Trip t = trips.get(req.getTripId());
            if (t != null) {
                TrekkingRoute r = routes.get(t.getRouteId());
                map.put("routeName", r != null ? r.getRouteName() : "Trek Trail");
            }
            list.add(map);
        }
        return list;
    }

    // --- Safety Alerts ---
    public synchronized SafetyAlert issueSafetyAlert(int adminId, int routeId, String message, String severity) {
        int alertId = alertIdGen.incrementAndGet();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        SafetyAlert alert = new SafetyAlert(alertId, adminId, routeId, message, severity, time);
        safetyAlerts.put(alertId, alert);
        return alert;
    }

    public List<Map<String, Object>> getAllSafetyAlerts() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SafetyAlert a : safetyAlerts.values()) {
            Map<String, Object> map = a.toMap();
            TrekkingRoute r = routes.get(a.getRouteId());
            map.put("routeName", r != null ? r.getRouteName() : "All Routes");
            list.add(map);
        }
        return list;
    }

    // --- Feedback ---
    public synchronized Feedback submitFeedback(int userId, int rating, String comments) {
        int feedbackId = feedbackIdGen.incrementAndGet();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Feedback fb = new Feedback(feedbackId, userId, rating, comments, today);
        feedbacks.put(feedbackId, fb);
        return fb;
    }

    public List<Map<String, Object>> getAllFeedbacks() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Feedback fb : feedbacks.values()) {
            Map<String, Object> map = fb.toMap();
            User u = users.get(fb.getUserId());
            map.put("userName", u != null ? u.getName() : "Anonymous Trekker");
            list.add(map);
        }
        return list;
    }

    // --- Admin monitoring ---
    public List<Map<String, Object>> getAllUsersWithContacts() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (User u : users.values()) {
            Map<String, Object> map = u.toMap();
            EmergencyContact ec = getEmergencyContactForUser(u.getUserId());
            map.put("emergencyContact", ec != null ? ec.toMap() : null);
            list.add(map);
        }
        return list;
    }

    public List<Map<String, Object>> getAllPayments() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Payment p : payments.values()) {
            Map<String, Object> map = p.toMap();
            User u = users.get(p.getUserId());
            map.put("userName", u != null ? u.getName() : "N/A");
            Trip t = trips.get(p.getTripId());
            if (t != null) {
                TrekkingRoute r = routes.get(t.getRouteId());
                map.put("routeName", r != null ? r.getRouteName() : "Trek");
            }
            list.add(map);
        }
        return list;
    }
}
