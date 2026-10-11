package com.example.printexpress.ui.common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.printexpress.R;
import com.example.printexpress.util.StatusHelper;

/**
 * Reusable order progress tracker:
 *
 *   ●━━━━●━━━━●━━━━○━━━━○
 *  Pending Processing Printing Ready Completed
 *
 * Built in code: one column per step = [line, dot, line] + label.
 */
public class OrderStatusView extends LinearLayout {

    public OrderStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        if (isInEditMode()) setStatus("PRINTING");
    }

    public void setStatus(String status) {
        removeAllViews();
        int current = StatusHelper.stepIndex(status);   // -1 when cancelled
        int done = ContextCompat.getColor(getContext(), R.color.px_primary);
        int todo = ContextCompat.getColor(getContext(), R.color.px_outline);
        int steps = StatusHelper.ORDER_STEPS.length;

        for (int i = 0; i < steps; i++) {
            boolean reached = i <= current;

            LinearLayout column = new LinearLayout(getContext());
            column.setOrientation(VERTICAL);
            column.setGravity(Gravity.CENTER_HORIZONTAL);
            addView(column, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));

            // Top row: line - dot - line
            LinearLayout track = new LinearLayout(getContext());
            track.setGravity(Gravity.CENTER_VERTICAL);
            column.addView(track, new LayoutParams(LayoutParams.MATCH_PARENT, dp(22)));

            View left = line(i == 0 ? 0 : (reached ? done : todo));
            View right = line(i == steps - 1 ? 0 : (i < current ? done : todo));
            View dot = new View(getContext());
            dot.setBackgroundResource(R.drawable.bg_circle);
            dot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(reached ? done : todo));
            int size = i == current ? dp(18) : dp(12);

            track.addView(left, new LayoutParams(0, dp(3), 1f));
            track.addView(dot, new LayoutParams(size, size));
            track.addView(right, new LayoutParams(0, dp(3), 1f));

            TextView label = new TextView(getContext());
            label.setText(StatusHelper.label(StatusHelper.ORDER_STEPS[i]));
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            label.setGravity(Gravity.CENTER);
            label.setMaxLines(1);
            label.setTextColor(ContextCompat.getColor(getContext(),
                    reached ? R.color.px_text_primary : R.color.px_text_hint));
            if (i == current) label.setTypeface(Typeface.DEFAULT_BOLD);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.topMargin = dp(6);
            column.addView(label, lp);
        }
    }

    private View line(int color) {
        View view = new View(getContext());
        view.setBackgroundColor(color);
        return view;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
