package com.example.printexpress.data.model;

/** One row in the "My orders" list. */
public class OrderSummary {
    private final long orderId;
    private final String productName;
    private final String iconName;
    private final int quantity;
    private final String status;
    private final String fulfilmentType;
    private final String scheduledDate;
    private final double totalAmount;
    private final String createdAt;
    private final String customerName;

    public OrderSummary(long orderId, String productName, String iconName, int quantity, String status,
                        String fulfilmentType, String scheduledDate, double totalAmount, String createdAt,
                        String customerName) {
        this.orderId = orderId;
        this.productName = productName;
        this.iconName = iconName;
        this.quantity = quantity;
        this.status = status;
        this.fulfilmentType = fulfilmentType;
        this.scheduledDate = scheduledDate;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.customerName = customerName;
    }

    public long getOrderId() { return orderId; }
    public String getProductName() { return productName; }
    public String getIconName() { return iconName; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public String getFulfilmentType() { return fulfilmentType; }
    public String getScheduledDate() { return scheduledDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getCreatedAt() { return createdAt; }
    public String getCustomerName() { return customerName; }
}
