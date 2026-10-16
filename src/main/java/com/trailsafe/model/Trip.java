package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Trip {
    private int tripId;
    private int userId;
    private int routeId;
    private String startDate;
    private String endDate;
    private String status; // Planned, Ongoing, Completed

    public Trip() {}

    public Trip(int tripId, int userId, int routeId, String startDate, String endDate, String status) {
        this.tripId = tripId;
        this.userId = userId;
        this.routeId = routeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("tripId", tripId);
        m.put("userId", userId);
        m.put("routeId", routeId);
        m.put("startDate", startDate);
        m.put("endDate", endDate);
        m.put("status", status);
        return m;
    }
}
