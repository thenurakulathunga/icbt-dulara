package com.example.printexpress.data.model;

public class Faq implements InfoItem {
    private final long faqId;
    private final String question;
    private final String answer;

    public Faq(long faqId, String question, String answer) {
        this.faqId = faqId;
        this.question = question;
        this.answer = answer;
    }

    public long getFaqId() { return faqId; }
    @Override public String getHeading() { return question; }
    @Override public String getBody() { return answer; }
}
