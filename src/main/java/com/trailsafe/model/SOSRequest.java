package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class SOSRequest {
    private int sosId;
    private int userId;
    private int tripId;
    private int serviceId;
    private String timestamp;
    private double latitude;
    private double longitude;
    private String status; // Sent, Acknowledged, Resolved

    public SOSRequest() {}

    public SOSRequest(int sosId, int userId, int tripId, int serviceId, String timestamp, double latitude, double longitude, String status) {
        this.sosId = sosId;
        this.userId = userId;
        this.tripId = tripId;
        this.serviceId = serviceId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public int getSosId() { return sosId; }
    public void setSosId(int sosId) { this.sosId = sosId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("sosId", sosId);
        m.put("userId", userId);
        m.put("tripId", tripId);
        m.put("serviceId", serviceId);
        m.put("timestamp", timestamp);
        m.put("latitude", latitude);
        m.put("longitude", longitude);
        m.put("status", status);
        return m;
    }
}
