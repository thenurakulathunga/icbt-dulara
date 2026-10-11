package com.example.printexpress.util;

/**
 * Price rules in ONE place. Used for the live price on the order form AND by
 * OrderRepository, which recalculates the real price when the order is saved:
 *
 *   unit price = base price + size extra + material extra
 *   subtotal   = unit price x quantity
 *   discount   = subtotal x promo % / 100
 *   total      = subtotal - discount + delivery fee
 */
public final class PriceCalculator {

    public static final double DELIVERY_FEE = 350.00;

    private PriceCalculator() {
    }

    public static double unitPrice(double basePrice, double sizeExtra, double materialExtra) {
        return round(basePrice + sizeExtra + materialExtra);
    }

    public static double subtotal(double unitPrice, int quantity) {
        return round(unitPrice * quantity);
    }

    public static double discount(double subtotal, int discountPercent) {
        return round(subtotal * discountPercent / 100.0);
    }

    public static double deliveryFee(boolean isDelivery) {
        return isDelivery ? DELIVERY_FEE : 0;
    }

    public static double total(double subtotal, double discount, double deliveryFee) {
        return round(subtotal - discount + deliveryFee);
    }

    /** Round to 2 decimal places (cents). */
    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
