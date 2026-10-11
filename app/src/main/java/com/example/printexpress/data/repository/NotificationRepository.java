package com.example.printexpress.data.repository;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.printexpress.data.db.AppDatabase;
import com.example.printexpress.data.db.Db;
import com.example.printexpress.data.model.AppNotification;

import java.util.List;

/** SQL for the notifications table. */
public class NotificationRepository {

    private final AppDatabase database;

    public NotificationRepository(AppDatabase database) {
        this.database = database;
    }

    public List<AppNotification> getNotifications(long userId) {
        return Db.list(database.getReadableDatabase(),
                "SELECT * FROM notifications WHERE user_id = ? ORDER BY notification_id DESC",
                c -> new AppNotification(Db.lng(c, "notification_id"), Db.str(c, "title"), Db.str(c, "message"),
                        Db.str(c, "type"), Db.bool(c, "is_read"), Db.str(c, "created_at")),
                userId);
    }

    public int countUnread(long userId) {
        return (int) Db.number(database.getReadableDatabase(),
                "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0", userId);
    }

    public void add(long userId, String title, String message, String type) {
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("title", title);
        values.put("message", message);
        values.put("type", type);
        database.getWritableDatabase().insertOrThrow("notifications", null, values);
    }

    /** Sends the same notification to every customer (used for promotions). Returns how many. */
    public int addForAllUsers(String title, String message, String type) {
        SQLiteDatabase db = database.getWritableDatabase();
        db.execSQL("INSERT INTO notifications (user_id, title, message, type) SELECT user_id, ?, ?, ? FROM users",
                new Object[]{title, message, type});
        return (int) Db.number(db, "SELECT COUNT(*) FROM users");
    }

    public void markRead(long userId, long notificationId) {
        ContentValues values = new ContentValues();
        values.put("is_read", 1);
        database.getWritableDatabase().update("notifications", values,
                "user_id = ? AND notification_id = ?", new String[]{"" + userId, "" + notificationId});
    }

    public void markAllRead(long userId) {
        ContentValues values = new ContentValues();
        values.put("is_read", 1);
        database.getWritableDatabase().update("notifications", values, "user_id = ?", new String[]{"" + userId});
    }
}
