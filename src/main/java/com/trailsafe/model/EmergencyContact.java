package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class EmergencyContact {
    private int contactId;
    private int userId;
    private String name;
    private String phone;
    private String relationship;

    public EmergencyContact() {}

    public EmergencyContact(int contactId, int userId, String name, String phone, String relationship) {
        this.contactId = contactId;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.relationship = relationship;
    }

    public int getContactId() { return contactId; }
    public void setContactId(int contactId) { this.contactId = contactId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("contactId", contactId);
        m.put("userId", userId);
        m.put("name", name);
        m.put("phone", phone);
        m.put("relationship", relationship);
        return m;
    }
}
