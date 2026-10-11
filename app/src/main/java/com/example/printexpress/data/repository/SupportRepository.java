package com.example.printexpress.data.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.printexpress.data.AppException;
import com.example.printexpress.data.db.AppDatabase;
import com.example.printexpress.data.db.Db;
import com.example.printexpress.data.model.SupportRequest;
import com.example.printexpress.util.Validator;

import java.util.List;

/** Customer questions to the design / support team. */
public class SupportRepository {

    private final AppDatabase database;
    private final NotificationRepository notifications;

    public SupportRepository(AppDatabase database, NotificationRepository notifications) {
        this.database = database;
        this.notifications = notifications;
    }

    public List<SupportRequest> getRequests(long userId) {
        return Db.list(database.getReadableDatabase(),
                "SELECT * FROM support_requests WHERE user_id = ? ORDER BY request_id DESC",
                SupportRepository::map, userId);
    }

    public List<SupportRequest> send(long userId, String subject, String message) {
        AppException.check(Validator.length(subject, "Subject", 3, 100));
        AppException.check(Validator.length(message, "Message", 10, 1000));

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("subject", subject.trim());
        values.put("message", message.trim());
        database.getWritableDatabase().insertOrThrow("support_requests", null, values);
        return getRequests(userId);
    }

    // ---------- Staff (demo panel) ----------

    public List<SupportRequest> getOpenRequests() {
        return Db.list(database.getReadableDatabase(),
                "SELECT * FROM support_requests WHERE status = 'OPEN' ORDER BY request_id", SupportRepository::map);
    }

    public void reply(long requestId, String reply) {
        AppException.check(Validator.length(reply, "Reply", 2, 1000));
        Long userId = Db.one(database.getReadableDatabase(),
                "SELECT user_id FROM support_requests WHERE request_id = ?", c -> Db.lng(c, "user_id"), requestId);
        if (userId == null) throw new AppException("Support request not found");

        ContentValues values = new ContentValues();
        values.put("reply", reply.trim());
        values.put("status", "ANSWERED");
        database.getWritableDatabase().update("support_requests", values, "request_id = ?",
                new String[]{"" + requestId});
        notifications.add(userId, "Support team replied", reply.trim(), "SYSTEM");
    }

    private static SupportRequest map(Cursor c) {
        return new SupportRequest(Db.lng(c, "request_id"), Db.str(c, "subject"), Db.str(c, "message"),
                Db.str(c, "status"), Db.str(c, "reply"), Db.str(c, "created_at"));
    }
}
