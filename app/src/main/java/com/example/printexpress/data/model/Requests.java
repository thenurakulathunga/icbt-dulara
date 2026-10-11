package com.example.printexpress.data.model;

/**
 * Data the screens pass to the repositories when they SAVE something.
 * Kept together in one file because they are tiny.
 */
public final class Requests {

    private Requests() {
    }

    public static class Register {
        public final String fullName;
        public final String email;
        public final String phone;
        public final String password;

        public Register(String fullName, String email, String phone, String password) {
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.password = password;
        }
    }

    public static class Profile {
        public final String fullName;
        public final String email;
        public final String phone;

        public Profile(String fullName, String email, String phone) {
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
        }
    }

    public static class AddressBody {
        public final String label;
        public final String addressLine;
        public final String city;
        public final String postalCode;
        public final boolean isDefault;

        public AddressBody(String label, String addressLine, String city, String postalCode, boolean isDefault) {
            this.label = label;
            this.addressLine = addressLine;
            this.city = city;
            this.postalCode = postalCode;
            this.isDefault = isDefault;
        }
    }

    /** Built step by step on the order form, then saved with "Place order". */
    public static class Order {
        public long productId;
        public Long sizeOptionId;
        public Long materialOptionId;
        public Long designId;
        public String customText;
        public int quantity;
        public String fulfilmentType;   // PICKUP or DELIVERY
        public Long addressId;
        public String scheduledDate;    // yyyy-MM-dd
        public String promoCode;
        public String notes;
    }
}
