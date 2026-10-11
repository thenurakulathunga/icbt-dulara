package com.example.printexpress.data.model;

public class SupportRequest {
    private final long requestId;
    private final String subject;
    private final String message;
    private final String status;
    private final String reply;
    private final String createdAt;

    public SupportRequest(long requestId, String subject, String message, String status,
                          String reply, String createdAt) {
        this.requestId = requestId;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.reply = reply;
        this.createdAt = createdAt;
    }

    public long getRequestId() { return requestId; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public String getReply() { return reply; }
    public String getCreatedAt() { return createdAt; }

    /** Text shown in the staff "reply" list. */
    @Override
    public String toString() {
        return "#" + requestId + "  " + subject;
    }
}
