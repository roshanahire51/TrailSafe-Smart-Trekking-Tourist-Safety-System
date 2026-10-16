package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class RouteFacility {
    private int facilityId;
    private int routeId;
    private String facilityName;
    private String facilityType;
    private String location;

    public RouteFacility() {}

    public RouteFacility(int facilityId, int routeId, String facilityName, String facilityType, String location) {
        this.facilityId = facilityId;
        this.routeId = routeId;
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.location = location;
    }

    public int getFacilityId() { return facilityId; }
    public void setFacilityId(int facilityId) { this.facilityId = facilityId; }

    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getFacilityType() { return facilityType; }
    public void setFacilityType(String facilityType) { this.facilityType = facilityType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("facilityId", facilityId);
        m.put("routeId", routeId);
        m.put("facilityName", facilityName);
        m.put("facilityType", facilityType);
        m.put("location", location);
        return m;
    }
}
