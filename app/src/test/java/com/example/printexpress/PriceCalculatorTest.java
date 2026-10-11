package com.example.printexpress;

import static org.junit.Assert.assertEquals;

import com.example.printexpress.util.Formatter;
import com.example.printexpress.util.PriceCalculator;

import org.junit.Test;

/** Unit tests for the live price shown on the order form (OrderRepository uses the same class). */
public class PriceCalculatorTest {

    private static final double DELTA = 0.001;

    @Test
    public void unitPriceAddsExtras() {
        // Standard Business Cards 12.00 + size 0 + gloss laminated 4.00
        assertEquals(16.00, PriceCalculator.unitPrice(12.00, 0, 4.00), DELTA);
    }

    @Test
    public void fullOrderWithPromoAndDelivery() {
        // Same order as RepositoryTest.validOrderIsPricedCorrectly: 600 cards, BULK15, delivery
        double unit = PriceCalculator.unitPrice(12.00, 0, 4.00);
        double subtotal = PriceCalculator.subtotal(unit, 600);
        double discount = PriceCalculator.discount(subtotal, 15);
        double delivery = PriceCalculator.deliveryFee(true);
        assertEquals(9600.00, subtotal, DELTA);
        assertEquals(1440.00, discount, DELTA);
        assertEquals(8510.00, PriceCalculator.total(subtotal, discount, delivery), DELTA);
    }

    @Test
    public void pickupIsFree() {
        assertEquals(0, PriceCalculator.deliveryFee(false), DELTA);
    }

    @Test
    public void discountRoundsToCents() {
        assertEquals(0.17, PriceCalculator.discount(3.33, 5), DELTA);
    }

    @Test
    public void moneyFormat() {
        assertEquals("LKR 8,510.00", Formatter.money(8510));
    }

    @Test
    public void dateFormat() {
        assertEquals("15 Oct 2026", Formatter.date("2026-10-15"));
    }
}
