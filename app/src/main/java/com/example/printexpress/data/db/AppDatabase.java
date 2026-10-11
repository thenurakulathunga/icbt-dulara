package com.example.printexpress.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.printexpress.util.PasswordHasher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * The app's SQLite database ("printxpress.db" in the phone's private storage).
 *
 * The first time the app starts, onCreate() runs:
 *   1. assets/database/schema.sql  - creates the 13 tables
 *   2. the demo customer            - added in Java because the password must be hashed
 *   3. assets/database/seed.sql    - categories, products, offers, guidelines ...
 */
public class AppDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "printxpress.db";
    private static final int DB_VERSION = 1;

    private static AppDatabase instance;
    private final Context context;

    /** One shared instance for the whole app (opening a database is slow). */
    public static synchronized AppDatabase get(Context context) {
        if (instance == null) {
            instance = new AppDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private AppDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        // SQLite ignores FOREIGN KEY rules unless we switch them on
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        runSqlFile(db, "database/schema.sql");
        insertDemoCustomer(db);
        runSqlFile(db, "database/seed.sql");
    }

    /** Version 1 has no upgrades yet: simply rebuild (development only - deletes data). */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String[] tables = {"support_requests", "faqs", "guidelines", "sample_designs", "notifications",
                "order_items", "orders", "promotions", "designs", "product_options", "products",
                "categories", "addresses", "users"};
        for (String table : tables) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
        onCreate(db);
    }

    private void insertDemoCustomer(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("full_name", "Demo Customer");
        values.put("email", "demo@printxpress.lk");
        values.put("phone", "0771234567");
        values.put("password_hash", PasswordHasher.hash("Demo@123"));
        db.insertOrThrow("users", null, values);   // becomes user_id 1
    }

    /**
     * Reads a .sql file from assets and runs each statement (they end with ";").
     * Lines starting with "--" are comments and are skipped.
     */
    private void runSqlFile(SQLiteDatabase db, String assetPath) {
        StringBuilder statement = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(assetPath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;
                statement.append(line).append('\n');
                if (trimmed.endsWith(";")) {
                    db.execSQL(statement.toString());
                    statement.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + assetPath, e);
        }
    }
}
