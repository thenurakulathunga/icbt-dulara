package com.example.printexpress.util;

import android.content.Context;

import com.example.printexpress.data.Async;
import com.example.printexpress.data.SessionManager;
import com.example.printexpress.data.model.AppNotification;
import com.example.printexpress.data.repository.Repositories;

/**
 * Reads the customer's notifications from the database and shows NEW unread ones
 * as phone alerts. The newest id already shown is remembered, so each alert appears once.
 * Called when the app opens and right after placing / changing an order.
 */
public final class NotificationSync {

    private static final int MAX_ALERTS = 3;

    private NotificationSync() {
    }

    public static void check(Context context) {
        Context app = context.getApplicationContext();
        SessionManager session = new SessionManager(app);
        if (!session.isLoggedIn()) return;

        Async.run(() -> Repositories.get(app).notifications.getNotifications(session.getUserId()), list -> {
            long lastShown = session.getLastNotifiedId();
            long newest = lastShown;
            int shown = 0;
            for (AppNotification n : list) {
                if (!n.isRead() && n.getNotificationId() > lastShown && shown < MAX_ALERTS) {
                    NotificationHelper.show(app, (int) n.getNotificationId(), n.getTitle(), n.getMessage());
                    shown++;
                }
                newest = Math.max(newest, n.getNotificationId());
            }
            session.setLastNotifiedId(newest);
        }, message -> { /* silent - runs in the background */ });
    }
}
