package com.example.printexpress.ui.base;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.printexpress.R;
import com.example.printexpress.data.SessionManager;
import com.example.printexpress.data.Async;
import com.example.printexpress.data.repository.Repositories;
import com.example.printexpress.ui.common.LoadingDialog;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

/**
 * Parent of every screen. Write common code ONCE here instead of in every activity:
 *  - edge-to-edge layout with correct padding for the status / navigation bar
 *  - toolbar with a back arrow
 *  - database work on a background thread, with loading dialog and error messages
 *  - snackbar messages and confirm dialogs
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Repositories repo;
    protected SessionManager session;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // The app is always dark, so always use light (white) status bar icons
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.dark(Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
        repo = Repositories.get(this);
        session = new SessionManager(this);
        loadingDialog = new LoadingDialog(this);
    }

    /** Every screen calls setContentView(binding.getRoot()) - we add the system bar padding here. */
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            int types = WindowInsetsCompat.Type.systemBars();
            if (resizeForKeyboard()) types |= WindowInsetsCompat.Type.ime();
            Insets insets = windowInsets.getInsets(types);
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /** Return false on screens with a bottom navigation bar. */
    protected boolean resizeForKeyboard() {
        return true;
    }

    protected void setupToolbar(MaterialToolbar toolbar, CharSequence title) {
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    // ---------- Database helpers ----------

    /** Id of the logged-in customer. */
    protected long userId() {
        return session.getUserId();
    }

    /**
     * Runs database work in the background; errors are shown in a snackbar.
     *   request(() -> repo.orders.getOrders(userId()), orders -> show(orders));
     */
    protected <T> void request(Async.Task<T> task, Async.OnSuccess<T> onSuccess) {
        request(task, false, onSuccess, this::showMessage);
    }

    /** Same, but shows a "Please wait" dialog while it runs (use it for saving). */
    protected <T> void requestWithLoading(Async.Task<T> task, Async.OnSuccess<T> onSuccess) {
        request(task, true, onSuccess, this::showMessage);
    }

    protected <T> void request(Async.Task<T> task, boolean showLoading,
                               Async.OnSuccess<T> onSuccess, Async.OnError onError) {
        if (showLoading) loadingDialog.show();
        Async.run(task,
                result -> {
                    loadingDialog.dismiss();
                    if (!isScreenClosed()) onSuccess.onSuccess(result);
                },
                message -> {
                    loadingDialog.dismiss();
                    if (!isScreenClosed()) onError.onError(message);
                });
    }

    /** The answer may arrive after the user already left the screen. */
    private boolean isScreenClosed() {
        return isFinishing() || isDestroyed();
    }

    // ---------- UI helpers ----------

    public void showMessage(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.px_surface_high));
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.px_text_primary));
        snackbar.setTextMaxLines(4);
        snackbar.show();
    }

    protected void confirm(String title, String message, String positiveText, Runnable onConfirm) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(positiveText, (dialog, which) -> onConfirm.run())
                .show();
    }

    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        loadingDialog.dismiss();
        super.onDestroy();
    }
}
