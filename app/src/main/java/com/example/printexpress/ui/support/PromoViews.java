package com.example.printexpress.ui.support;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import com.example.printexpress.data.model.Promotion;
import com.example.printexpress.databinding.ItemPromoBinding;
import com.example.printexpress.util.Formatter;

/** Fills an offer card. Shared by the Home carousel and the Offers screen. */
public final class PromoViews {

    private PromoViews() {
    }

    public static void bind(ItemPromoBinding b, Promotion promo) {
        b.textDiscount.setText(promo.getDiscountPercent() + "% OFF");
        b.textTitle.setText(promo.getTitle());
        b.textDescription.setText(promo.getDescription());
        b.textCode.setText(promo.getPromoCode());
        b.textValid.setText("Valid until " + Formatter.date(promo.getEndDate()));
        b.buttonCopy.setOnClickListener(v -> copyCode(v.getContext(), promo.getPromoCode()));
    }

    public static void copyCode(Context context, String code) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Promo code", code));
        Toast.makeText(context, "Code " + code + " copied", Toast.LENGTH_SHORT).show();
    }
}
