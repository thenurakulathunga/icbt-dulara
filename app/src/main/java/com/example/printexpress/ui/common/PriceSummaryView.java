package com.example.printexpress.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.printexpress.databinding.ViewPriceSummaryBinding;
import com.example.printexpress.util.Formatter;

/**
 * Reusable price breakdown card (used on the order form AND the order details screen):
 *   Subtotal / Discount / Delivery fee / Total
 */
public class PriceSummaryView extends LinearLayout {

    private final ViewPriceSummaryBinding b;

    public PriceSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        b = ViewPriceSummaryBinding.inflate(LayoutInflater.from(context), this);
    }

    /** e.g. "100 x LKR 16.00" - pass null to hide. */
    public void setUnitLine(String text) {
        b.textUnitLine.setVisibility(text == null ? GONE : VISIBLE);
        b.textUnitLine.setText(text);
    }

    public void setPrices(double subtotal, double discount, double deliveryFee, double total) {
        b.textSubtotal.setText(Formatter.money(subtotal));
        b.rowDiscount.setVisibility(discount > 0 ? VISIBLE : GONE);
        b.textDiscount.setText("- " + Formatter.money(discount));
        b.textDelivery.setText(deliveryFee > 0 ? Formatter.money(deliveryFee) : "Free");
        b.textTotal.setText(Formatter.money(total));
    }
}
