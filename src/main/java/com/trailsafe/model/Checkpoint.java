package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Checkpoint {
    private int checkpointId;
    private int routeId;
    private String name;
    private double latitude;
    private double longitude;

    public Checkpoint() {}

    public Checkpoint(int checkpointId, int routeId, String name, double latitude, double longitude) {
        this.checkpointId = checkpointId;
        this.routeId = routeId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getCheckpointId() { return checkpointId; }
    public void setCheckpointId(int checkpointId) { this.checkpointId = checkpointId; }

    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("checkpointId", checkpointId);
        m.put("routeId", routeId);
        m.put("name", name);
        m.put("latitude", latitude);
        m.put("longitude", longitude);
        return m;
    }
}
