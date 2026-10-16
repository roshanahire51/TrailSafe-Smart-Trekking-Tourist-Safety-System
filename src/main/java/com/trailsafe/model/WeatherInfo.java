package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class WeatherInfo {
    private int weatherId;
    private int routeId;
    private String condition;
    private double temperature;
    private String forecastTime;

    public WeatherInfo() {}

    public WeatherInfo(int weatherId, int routeId, String condition, double temperature, String forecastTime) {
        this.weatherId = weatherId;
        this.routeId = routeId;
        this.condition = condition;
        this.temperature = temperature;
        this.forecastTime = forecastTime;
    }

    public int getWeatherId() { return weatherId; }
    public void setWeatherId(int weatherId) { this.weatherId = weatherId; }

    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public String getForecastTime() { return forecastTime; }
    public void setForecastTime(String forecastTime) { this.forecastTime = forecastTime; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("weatherId", weatherId);
        m.put("routeId", routeId);
        m.put("condition", condition);
        m.put("temperature", temperature);
        m.put("forecastTime", forecastTime);
        return m;
    }
}
