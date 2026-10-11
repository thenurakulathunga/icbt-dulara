package com.example.printexpress.ui.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.example.printexpress.R;
import com.example.printexpress.databinding.ViewMenuRowBinding;

/**
 * Reusable menu row:  [icon]  Title / subtitle  >
 * Use it in XML:
 *   <com.example.printexpress.ui.common.MenuRowView
 *       app:rowIcon="@drawable/ic_location"
 *       app:rowTitle="@string/my_addresses" />
 */
public class MenuRowView extends LinearLayout {

    private final ViewMenuRowBinding b;

    public MenuRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        b = ViewMenuRowBinding.inflate(LayoutInflater.from(context), this);
        setOrientation(HORIZONTAL);
        setGravity(android.view.Gravity.CENTER_VERTICAL);
        int pad = Math.round(14 * getResources().getDisplayMetrics().density);
        setPadding(pad, pad, pad, pad);
        TypedValue ripple = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, ripple, true);
        setBackgroundResource(ripple.resourceId);
        setClickable(true);
        setFocusable(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuRowView);
        b.icon.setImageResource(a.getResourceId(R.styleable.MenuRowView_rowIcon, R.drawable.ic_info));
        b.title.setText(a.getString(R.styleable.MenuRowView_rowTitle));
        setSubtitle(a.getString(R.styleable.MenuRowView_rowSubtitle));
        if (a.getBoolean(R.styleable.MenuRowView_rowDanger, false)) {
            int red = ContextCompat.getColor(context, R.color.px_error);
            b.title.setTextColor(red);
            b.icon.setImageTintList(ColorStateList.valueOf(red));
            b.chevron.setVisibility(GONE);
        }
        a.recycle();
    }

    public void setSubtitle(String text) {
        b.subtitle.setVisibility(text == null ? GONE : VISIBLE);
        b.subtitle.setText(text);
    }
}
