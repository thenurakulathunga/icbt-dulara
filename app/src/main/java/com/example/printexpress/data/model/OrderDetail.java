package com.example.printexpress.data.model;

import java.util.List;

/** Everything shown on the order tracking screen. */
public class OrderDetail {
    private final long orderId;
    private final String status;
    private final String fulfilmentType;
    private final String scheduledDate;
    private final String deliveryAddress;
    private final String promoCode;
    private final double subtotal;
    private final double discountAmount;
    private final double deliveryFee;
    private final String notes;
    private final String createdAt;
    private final List<OrderItem> items;

    public OrderDetail(long orderId, String status, String fulfilmentType, String scheduledDate,
                       String deliveryAddress, String promoCode, double subtotal, double discountAmount,
                       double deliveryFee, String notes, String createdAt, List<OrderItem> items) {
        this.orderId = orderId;
        this.status = status;
        this.fulfilmentType = fulfilmentType;
        this.scheduledDate = scheduledDate;
        this.deliveryAddress = deliveryAddress;
        this.promoCode = promoCode;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.deliveryFee = deliveryFee;
        this.notes = notes;
        this.createdAt = createdAt;
        this.items = items;
    }

    public long getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public String getFulfilmentType() { return fulfilmentType; }
    public String getScheduledDate() { return scheduledDate; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getPromoCode() { return promoCode; }
    public double getSubtotal() { return subtotal; }
    public double getDiscountAmount() { return discountAmount; }
    public double getDeliveryFee() { return deliveryFee; }
    public String getNotes() { return notes; }
    public String getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }

    /** total = subtotal - discount + delivery (calculated, not stored) */
    public double getTotalAmount() {
        return subtotal - discountAmount + deliveryFee;
    }

    /** Orders can be changed only before printing begins. */
    public boolean isCanChange() {
        return "PENDING".equals(status) || "PROCESSING".equals(status);
    }

    public boolean isDelivery() {
        return "DELIVERY".equals(fulfilmentType);
    }
}
