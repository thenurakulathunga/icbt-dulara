package com.example.printexpress.data.model;

/** A notification stored in the database (type: ORDER, PROMOTION or SYSTEM). */
public class AppNotification {
    private final long notificationId;
    private final String title;
    private final String message;
    private final String type;
    private boolean isRead;
    private final String createdAt;

    public AppNotification(long notificationId, String title, String message, String type,
                           boolean isRead, String createdAt) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public long getNotificationId() { return notificationId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }

    public void setRead(boolean read) { isRead = read; }
}
