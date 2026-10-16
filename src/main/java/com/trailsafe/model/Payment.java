package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Payment {
    private int paymentId;
    private int userId;
    private int tripId;
    private double amount;
    private String paymentDate;
    private String status; // Success, Failed, Pending

    public Payment() {}

    public Payment(int paymentId, int userId, int tripId, double amount, String paymentDate, String status) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.tripId = tripId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("paymentId", paymentId);
        m.put("userId", userId);
        m.put("tripId", tripId);
        m.put("amount", amount);
        m.put("paymentDate", paymentDate);
        m.put("status", status);
        return m;
    }
}
