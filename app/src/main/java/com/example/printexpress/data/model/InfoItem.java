package com.example.printexpress.data.model;

/**
 * Anything with a heading and a body text.
 * Guideline and Faq both implement it, so ONE screen (InfoListActivity) can show both.
 */
public interface InfoItem {
    String getHeading();
    String getBody();
}
