package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Feedback {
    private int feedbackId;
    private int userId;
    private int rating; // 1 to 5
    private String comments;
    private String createdAt;

    public Feedback() {}

    public Feedback(int feedbackId, int userId, int rating, String comments, String createdAt) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.rating = rating;
        this.comments = comments;
        this.createdAt = createdAt;
    }

    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("feedbackId", feedbackId);
        m.put("userId", userId);
        m.put("rating", rating);
        m.put("comments", comments);
        m.put("createdAt", createdAt);
        return m;
    }
}
