package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class EmergencyService {
    private int serviceId;
    private String serviceName;
    private String phone;
    private String serviceType; // Rescue, medical, police, etc.

    public EmergencyService() {}

    public EmergencyService(int serviceId, String serviceName, String phone, String serviceType) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.phone = phone;
        this.serviceType = serviceType;
    }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("serviceId", serviceId);
        m.put("serviceName", serviceName);
        m.put("phone", phone);
        m.put("serviceType", serviceType);
        return m;
    }
}
