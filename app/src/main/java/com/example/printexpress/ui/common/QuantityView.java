package com.example.printexpress.ui.common;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.printexpress.databinding.ViewQuantityBinding;

/**
 * Reusable quantity stepper:  [ - ]  100  [ + ]
 * The customer can tap the buttons or type a number.
 */
public class QuantityView extends LinearLayout {

    public interface OnQuantityChanged {
        void onChanged(int quantity);
    }

    private final ViewQuantityBinding b;
    private int min = 1;
    private int max = 1000;
    private int step = 1;
    private OnQuantityChanged listener;

    public QuantityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        b = ViewQuantityBinding.inflate(LayoutInflater.from(context), this);
        setOrientation(HORIZONTAL);
        setGravity(android.view.Gravity.CENTER_VERTICAL);
        setBackgroundResource(com.example.printexpress.R.drawable.bg_quantity);

        b.buttonMinus.setOnClickListener(v -> setQuantity(Math.max(min, getQuantity() - step)));
        b.buttonPlus.setOnClickListener(v -> setQuantity(Math.min(max, getQuantity() + step)));
        b.editQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) listener.onChanged(getQuantity());
            }
        });
    }

    /** Big minimums (e.g. 50 business cards) step by the minimum, small ones by 1. */
    public void setRange(int min, int max) {
        this.min = min;
        this.max = max;
        this.step = min >= 10 ? min : 1;
        setQuantity(min);
    }

    public void setQuantity(int quantity) {
        b.editQuantity.setText(String.valueOf(quantity));
        b.editQuantity.setSelection(b.editQuantity.length());
    }

    /** Returns 0 when the field is empty or not a number. */
    public int getQuantity() {
        try {
            return Integer.parseInt(b.editQuantity.getText().toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setOnQuantityChanged(OnQuantityChanged listener) {
        this.listener = listener;
    }
}
