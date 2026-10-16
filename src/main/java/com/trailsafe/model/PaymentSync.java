package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentSync {
    private int syncId;
    private int paymentId;
    private String syncStatus; // Pending, Completed
    private String lastSyncedAt;

    public PaymentSync() {}

    public PaymentSync(int syncId, int paymentId, String syncStatus, String lastSyncedAt) {
        this.syncId = syncId;
        this.paymentId = paymentId;
        this.syncStatus = syncStatus;
        this.lastSyncedAt = lastSyncedAt;
    }

    public int getSyncId() { return syncId; }
    public void setSyncId(int syncId) { this.syncId = syncId; }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public String getLastSyncedAt() { return lastSyncedAt; }
    public void setLastSyncedAt(String lastSyncedAt) { this.lastSyncedAt = lastSyncedAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("syncId", syncId);
        m.put("paymentId", paymentId);
        m.put("syncStatus", syncStatus);
        m.put("lastSyncedAt", lastSyncedAt);
        return m;
    }
}
