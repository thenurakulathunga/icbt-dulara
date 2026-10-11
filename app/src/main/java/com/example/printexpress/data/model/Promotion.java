package com.example.printexpress.data.model;

public class Promotion {
    private final long promotionId;
    private final String title;
    private final String description;
    private final String promoCode;
    private final int discountPercent;
    private final int minQuantity;
    private final String startDate;
    private final String endDate;

    public Promotion(long promotionId, String title, String description, String promoCode,
                     int discountPercent, int minQuantity, String startDate, String endDate) {
        this.promotionId = promotionId;
        this.title = title;
        this.description = description;
        this.promoCode = promoCode;
        this.discountPercent = discountPercent;
        this.minQuantity = minQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getPromotionId() { return promotionId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPromoCode() { return promoCode; }
    public int getDiscountPercent() { return discountPercent; }
    public int getMinQuantity() { return minQuantity; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }

    /** Text shown in the staff "send offer" list. */
    @Override
    public String toString() {
        return title + " (" + promoCode + ")";
    }
}
