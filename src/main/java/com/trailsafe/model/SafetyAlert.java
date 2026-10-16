package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class SafetyAlert {
    private int alertId;
    private int adminId;
    private int routeId;
    private String message;
    private String severity; // Low, Medium, High
    private String issuedAt;

    public SafetyAlert() {}

    public SafetyAlert(int alertId, int adminId, int routeId, String message, String severity, String issuedAt) {
        this.alertId = alertId;
        this.adminId = adminId;
        this.routeId = routeId;
        this.message = message;
        this.severity = severity;
        this.issuedAt = issuedAt;
    }

    public int getAlertId() { return alertId; }
    public void setAlertId(int alertId) { this.alertId = alertId; }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getIssuedAt() { return issuedAt; }
    public void setIssuedAt(String issuedAt) { this.issuedAt = issuedAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("alertId", alertId);
        m.put("adminId", adminId);
        m.put("routeId", routeId);
        m.put("message", message);
        m.put("severity", severity);
        m.put("issuedAt", issuedAt);
        return m;
    }
}
