package com.example.printexpress.data;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Runs database work on a background thread, then gives the result back on the UI thread.
 * (Android screens freeze if slow work - database, files - runs on the UI thread.)
 *
 *   Async.run(() -> repo.orders.getOrders(userId),       // background thread
 *             orders -> adapter.setItems(orders),          // UI thread, on success
 *             message -> showMessage(message));            // UI thread, on error
 *
 * One single background thread is used, so database writes never overlap.
 */
public final class Async {

    public interface Task<T> {
        T run() throws Exception;
    }

    public interface OnSuccess<T> {
        void onSuccess(T result);
    }

    public interface OnError {
        void onError(String message);
    }

    private static final ExecutorService BACKGROUND = Executors.newSingleThreadExecutor();
    private static final Handler UI_THREAD = new Handler(Looper.getMainLooper());

    private Async() {
    }

    public static <T> void run(Task<T> task, OnSuccess<T> onSuccess, OnError onError) {
        BACKGROUND.execute(() -> {
            try {
                T result = task.run();
                UI_THREAD.post(() -> onSuccess.onSuccess(result));
            } catch (AppException e) {
                UI_THREAD.post(() -> onError.onError(e.getMessage()));
            } catch (Exception e) {
                Log.e("PrintXpress", "Unexpected error", e);
                UI_THREAD.post(() -> onError.onError("Something went wrong. Please try again."));
            }
        });
    }
}
