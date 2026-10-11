package com.example.printexpress.data.model;

public class Address {
    private final long addressId;
    private final String label;
    private final String addressLine;
    private final String city;
    private final String postalCode;
    private final boolean isDefault;

    public Address(long addressId, String label, String addressLine, String city, String postalCode, boolean isDefault) {
        this.addressId = addressId;
        this.label = label;
        this.addressLine = addressLine;
        this.city = city;
        this.postalCode = postalCode;
        this.isDefault = isDefault;
    }

    public long getAddressId() { return addressId; }
    public String getLabel() { return label; }
    public String getAddressLine() { return addressLine; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }
    public boolean isDefault() { return isDefault; }

    public String getFullAddress() {
        return addressLine + ", " + city + " " + postalCode;
    }

    /** Text shown in the address dropdown. */
    @Override
    public String toString() {
        return label + " - " + getFullAddress();
    }
}
