package com.example.printexpress.data.model;

/** A size or a material. optionType is "SIZE" or "MATERIAL". */
public class ProductOption {
    private final long optionId;
    private final String optionType;
    private final String optionName;
    private final double extraPrice;

    public ProductOption(long optionId, String optionType, String optionName, double extraPrice) {
        this.optionId = optionId;
        this.optionType = optionType;
        this.optionName = optionName;
        this.extraPrice = extraPrice;
    }

    public long getOptionId() { return optionId; }
    public String getOptionType() { return optionType; }
    public String getOptionName() { return optionName; }
    public double getExtraPrice() { return extraPrice; }
}
