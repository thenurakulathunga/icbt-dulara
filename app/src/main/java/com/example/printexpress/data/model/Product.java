package com.example.printexpress.data.model;

public class Product {
    private final long productId;
    private final long categoryId;
    private final String categoryName;
    private final String iconName;
    private final String name;
    private final String description;
    private final double basePrice;
    private final int minQuantity;
    private final int maxQuantity;
    private final int turnaroundDays;

    public Product(long productId, long categoryId, String categoryName, String iconName, String name,
                   String description, double basePrice, int minQuantity, int maxQuantity, int turnaroundDays) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.iconName = iconName;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.turnaroundDays = turnaroundDays;
    }

    public long getProductId() { return productId; }
    public long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getIconName() { return iconName; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getBasePrice() { return basePrice; }
    public int getMinQuantity() { return minQuantity; }
    public int getMaxQuantity() { return maxQuantity; }
    public int getTurnaroundDays() { return turnaroundDays; }
}
