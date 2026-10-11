package com.example.printexpress.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;

import com.example.printexpress.R;
import com.example.printexpress.databinding.ViewStateBinding;

/**
 * Reusable view that covers a list while it is loading, empty, or failed.
 *
 *   stateView.showLoading();
 *   stateView.showEmpty(R.drawable.ic_orders, "No orders yet", "Your orders will appear here");
 *   stateView.showError(message, this::loadData);
 *   stateView.hide();
 */
public class StateView extends FrameLayout {

    private final ViewStateBinding b;

    public StateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        b = ViewStateBinding.inflate(LayoutInflater.from(context), this, true);
        hide();
    }

    public void showLoading() {
        setVisibility(VISIBLE);
        b.progress.setVisibility(VISIBLE);
        b.icon.setVisibility(GONE);
        b.title.setVisibility(GONE);
        b.message.setVisibility(GONE);
        b.buttonRetry.setVisibility(GONE);
    }

    public void showEmpty(@DrawableRes int icon, String title, String message) {
        show(icon, title, message, null);
    }

    public void showError(String message, Runnable onRetry) {
        show(R.drawable.ic_wifi_off, "Oops!", message, onRetry);
    }

    public void hide() {
        setVisibility(GONE);
    }

    private void show(int icon, String title, String message, Runnable onRetry) {
        setVisibility(VISIBLE);
        b.progress.setVisibility(GONE);
        b.icon.setVisibility(VISIBLE);
        b.icon.setImageResource(icon);
        b.title.setVisibility(VISIBLE);
        b.title.setText(title);
        b.message.setVisibility(message == null ? GONE : VISIBLE);
        b.message.setText(message);
        b.buttonRetry.setVisibility(onRetry == null ? GONE : VISIBLE);
        b.buttonRetry.setOnClickListener(onRetry == null ? null : (View v) -> onRetry.run());
    }
}
