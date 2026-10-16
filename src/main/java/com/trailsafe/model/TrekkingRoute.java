package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class TrekkingRoute {
    private int routeId;
    private String routeName;
    private String difficulty; // Easy, Medium, Hard
    private double distance;   // in km

    public TrekkingRoute() {}

    public TrekkingRoute(int routeId, String routeName, String difficulty, double distance) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.difficulty = difficulty;
        this.distance = distance;
    }

    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("routeId", routeId);
        m.put("routeName", routeName);
        m.put("difficulty", difficulty);
        m.put("distance", distance);
        return m;
    }
}
