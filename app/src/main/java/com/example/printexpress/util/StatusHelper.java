package com.example.printexpress.util;

import android.content.res.ColorStateList;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.printexpress.R;

/** Colours and labels for order / support statuses - used by every list that shows a status pill. */
public final class StatusHelper {

    /** The normal order journey, used by the tracking progress bar. */
    public static final String[] ORDER_STEPS = {"PENDING", "PROCESSING", "PRINTING", "READY", "COMPLETED"};

    private StatusHelper() {
    }

    public static int colorRes(String status) {
        if (status == null) return R.color.px_text_hint;
        switch (status) {
            case "PENDING":
            case "OPEN":
                return R.color.status_pending;
            case "PROCESSING":
                return R.color.status_processing;
            case "PRINTING":
                return R.color.status_printing;
            case "READY":
            case "ANSWERED":
                return R.color.status_ready;
            case "COMPLETED":
                return R.color.status_completed;
            case "CANCELLED":
            case "CLOSED":
                return R.color.status_cancelled;
            default:
                return R.color.px_text_hint;
        }
    }

    /** Paints a TextView that uses style="@style/PrintXpress.StatusPill". */
    public static void applyPill(TextView pill, String status) {
        pill.setText(label(status));
        pill.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(pill.getContext(), colorRes(status))));
    }

    public static String label(String status) {
        if ("READY".equals(status)) return "Ready";
        return Formatter.capitalize(status);
    }

    /** Position of the status in ORDER_STEPS, or -1 for CANCELLED. */
    public static int stepIndex(String status) {
        for (int i = 0; i < ORDER_STEPS.length; i++) {
            if (ORDER_STEPS[i].equals(status)) return i;
        }
        return -1;
    }

    public static boolean isActive(String status) {
        return !"COMPLETED".equals(status) && !"CANCELLED".equals(status);
    }
}
