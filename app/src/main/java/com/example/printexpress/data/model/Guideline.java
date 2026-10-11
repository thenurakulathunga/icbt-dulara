package com.example.printexpress.data.model;

public class Guideline implements InfoItem {
    private final long guidelineId;
    private final String title;
    private final String content;

    public Guideline(long guidelineId, String title, String content) {
        this.guidelineId = guidelineId;
        this.title = title;
        this.content = content;
    }

    public long getGuidelineId() { return guidelineId; }
    @Override public String getHeading() { return title; }
    @Override public String getBody() { return content; }
}
