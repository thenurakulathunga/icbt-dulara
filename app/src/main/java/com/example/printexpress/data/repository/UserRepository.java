package com.example.printexpress.data.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.printexpress.data.AppException;
import com.example.printexpress.data.db.AppDatabase;
import com.example.printexpress.data.db.Db;
import com.example.printexpress.data.model.Requests;
import com.example.printexpress.data.model.User;
import com.example.printexpress.util.PasswordHasher;
import com.example.printexpress.util.Validator;

/** Register, login and profile changes (users table). */
public class UserRepository {

    private static final String USER_COLUMNS = "user_id, full_name, email, phone, created_at";

    private final AppDatabase database;
    private final NotificationRepository notifications;

    public UserRepository(AppDatabase database, NotificationRepository notifications) {
        this.database = database;
        this.notifications = notifications;
    }

    public User register(Requests.Register r) {
        // Check again here - never trust only the screen
        AppException.check(Validator.name(r.fullName));
        AppException.check(Validator.email(r.email));
        AppException.check(Validator.phone(r.phone));
        AppException.check(Validator.password(r.password));

        String email = normalizeEmail(r.email);
        String phone = normalizePhone(r.phone);
        if (isTaken(email, phone, 0)) {
            throw new AppException("This email or phone number is already registered");
        }

        ContentValues values = new ContentValues();
        values.put("full_name", r.fullName.trim());
        values.put("email", email);
        values.put("phone", phone);
        values.put("password_hash", PasswordHasher.hash(r.password));
        long userId = database.getWritableDatabase().insertOrThrow("users", null, values);

        notifications.add(userId, "Welcome to PrintXpress!",
                "Use code WELCOME5 to get 5% off your first order.", "PROMOTION");
        return getUser(userId);
    }

    /** Login works with email OR phone number. */
    public User login(String identifier, String password) {
        String id = identifier.trim();
        id = id.contains("@") ? normalizeEmail(id) : normalizePhone(id);

        String[] row = Db.one(database.getReadableDatabase(),
                "SELECT user_id, password_hash FROM users WHERE email = ? OR phone = ?",
                c -> new String[]{Db.str(c, "user_id"), Db.str(c, "password_hash")}, id, id);

        if (row == null || !PasswordHasher.matches(password, row[1])) {
            throw new AppException("Incorrect email/phone or password");
        }
        return getUser(Long.parseLong(row[0]));
    }

    /** Returns null if the user no longer exists. */
    public User getUser(long userId) {
        return Db.one(database.getReadableDatabase(),
                "SELECT " + USER_COLUMNS + " FROM users WHERE user_id = ?", UserRepository::map, userId);
    }

    public User updateProfile(long userId, Requests.Profile p) {
        AppException.check(Validator.name(p.fullName));
        AppException.check(Validator.email(p.email));
        AppException.check(Validator.phone(p.phone));

        String email = normalizeEmail(p.email);
        String phone = normalizePhone(p.phone);
        if (isTaken(email, phone, userId)) {
            throw new AppException("This email or phone number is already used by another account");
        }
        ContentValues values = new ContentValues();
        values.put("full_name", p.fullName.trim());
        values.put("email", email);
        values.put("phone", phone);
        database.getWritableDatabase().update("users", values, "user_id = ?", new String[]{"" + userId});
        return getUser(userId);
    }

    public void changePassword(long userId, String currentPassword, String newPassword) {
        AppException.check(Validator.password(newPassword));
        String stored = Db.one(database.getReadableDatabase(),
                "SELECT password_hash FROM users WHERE user_id = ?", c -> Db.str(c, "password_hash"), userId);
        if (!PasswordHasher.matches(currentPassword, stored)) {
            throw new AppException("Current password is incorrect");
        }
        ContentValues values = new ContentValues();
        values.put("password_hash", PasswordHasher.hash(newPassword));
        database.getWritableDatabase().update("users", values, "user_id = ?", new String[]{"" + userId});
    }

    // ---------- helpers ----------

    /** Is the email or phone already used by someone other than exceptUserId? */
    private boolean isTaken(String email, String phone, long exceptUserId) {
        return Db.number(database.getReadableDatabase(),
                "SELECT COUNT(*) FROM users WHERE (email = ? OR phone = ?) AND user_id <> ?",
                email, phone, exceptUserId) > 0;
    }

    private static User map(Cursor c) {
        return new User(Db.lng(c, "user_id"), Db.str(c, "full_name"), Db.str(c, "email"),
                Db.str(c, "phone"), Db.str(c, "created_at"));
    }

    static String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    /** Store every phone as 07XXXXXXXX so +94 77... and 077... are the same person. */
    static String normalizePhone(String phone) {
        String p = phone.trim().replace(" ", "");
        return p.startsWith("+94") ? "0" + p.substring(3) : p;
    }
}
