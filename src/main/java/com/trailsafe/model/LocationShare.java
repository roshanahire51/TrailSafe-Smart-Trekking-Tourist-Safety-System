package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class LocationShare {
    private int shareId;
    private int tripId;
    private double latitude;
    private double longitude;
    private String sharedAt;
    private String status; // Active, Stopped

    public LocationShare() {}

    public LocationShare(int shareId, int tripId, double latitude, double longitude, String sharedAt, String status) {
        this.shareId = shareId;
        this.tripId = tripId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sharedAt = sharedAt;
        this.status = status;
    }

    public int getShareId() { return shareId; }
    public void setShareId(int shareId) { this.shareId = shareId; }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getSharedAt() { return sharedAt; }
    public void setSharedAt(String sharedAt) { this.sharedAt = sharedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("shareId", shareId);
        m.put("tripId", tripId);
        m.put("latitude", latitude);
        m.put("longitude", longitude);
        m.put("sharedAt", sharedAt);
        m.put("status", status);
        return m;
    }
}
