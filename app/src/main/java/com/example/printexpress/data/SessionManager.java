package com.example.printexpress.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.printexpress.data.model.User;

/**
 * Remembers who is logged in on this phone (SharedPreferences).
 * Only the user id and basic profile are kept - never the password.
 */
public class SessionManager {

    private static final String PREFS = "printxpress_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_LAST_NOTIFIED = "last_notified_id";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveLogin(User user) {
        prefs.edit().putLong(KEY_USER_ID, user.getUserId()).apply();
        saveUser(user);
    }

    public void saveUser(User user) {
        prefs.edit()
                .putString(KEY_NAME, user.getFullName())
                .putString(KEY_EMAIL, user.getEmail())
                .putString(KEY_PHONE, user.getPhone())
                .apply();
    }

    public boolean isLoggedIn() {
        return getUserId() > 0;
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, 0);
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getPhone() {
        return prefs.getString(KEY_PHONE, "");
    }

    /** Id of the newest notification already shown as a phone alert. */
    public long getLastNotifiedId() {
        return prefs.getLong(KEY_LAST_NOTIFIED, 0);
    }

    public void setLastNotifiedId(long id) {
        prefs.edit().putLong(KEY_LAST_NOTIFIED, id).apply();
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}
