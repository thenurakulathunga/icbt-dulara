package com.example.printexpress.data.model;

public class OrderItem {
    private final String productName;
    private final String iconName;
    private final String sizeName;
    private final String materialName;
    private final String designTitle;
    private final String customText;
    private final int quantity;
    private final double unitPrice;

    public OrderItem(String productName, String iconName, String sizeName, String materialName,
                     String designTitle, String customText, int quantity, double unitPrice) {
        this.productName = productName;
        this.iconName = iconName;
        this.sizeName = sizeName;
        this.materialName = materialName;
        this.designTitle = designTitle;
        this.customText = customText;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getProductName() { return productName; }
    public String getIconName() { return iconName; }
    public String getSizeName() { return sizeName; }
    public String getMaterialName() { return materialName; }
    public String getDesignTitle() { return designTitle; }
    public String getCustomText() { return customText; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
}
