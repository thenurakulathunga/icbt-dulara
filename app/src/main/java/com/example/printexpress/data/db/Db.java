package com.example.printexpress.data.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Small helpers that make the repositories short:
 *
 *   List<Category> list = Db.list(db, "SELECT * FROM categories", c -> new Category(
 *           Db.lng(c, "category_id"), Db.str(c, "name"), ...));
 *
 * A RowMapper turns ONE row of the result (the Cursor) into ONE Java object.
 */
public final class Db {

    public interface RowMapper<T> {
        T map(Cursor cursor);
    }

    private Db() {
    }

    /** Runs a SELECT and returns every row. */
    public static <T> List<T> list(SQLiteDatabase db, String sql, RowMapper<T> mapper, Object... args) {
        List<T> result = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(sql, toArgs(args))) {
            while (cursor.moveToNext()) {
                result.add(mapper.map(cursor));
            }
        }
        return result;
    }

    /** Runs a SELECT and returns the first row, or null when nothing matches. */
    public static <T> T one(SQLiteDatabase db, String sql, RowMapper<T> mapper, Object... args) {
        List<T> rows = list(db, sql, mapper, args);
        return rows.isEmpty() ? null : rows.get(0);
    }

    /** For "SELECT COUNT(*) ..." style queries. */
    public static long number(SQLiteDatabase db, String sql, Object... args) {
        try (Cursor cursor = db.rawQuery(sql, toArgs(args))) {
            return cursor.moveToFirst() ? cursor.getLong(0) : 0;
        }
    }

    // ---------- read a column by name ----------

    public static String str(Cursor c, String column) {
        int i = c.getColumnIndexOrThrow(column);
        return c.isNull(i) ? null : c.getString(i);
    }

    public static long lng(Cursor c, String column) {
        return c.getLong(c.getColumnIndexOrThrow(column));
    }

    public static int integer(Cursor c, String column) {
        return c.getInt(c.getColumnIndexOrThrow(column));
    }

    public static double dbl(Cursor c, String column) {
        return c.getDouble(c.getColumnIndexOrThrow(column));
    }

    /** SQLite has no boolean type - we store 0 / 1. */
    public static boolean bool(Cursor c, String column) {
        return c.getInt(c.getColumnIndexOrThrow(column)) == 1;
    }

    /** rawQuery() wants the "?" values as Strings. */
    private static String[] toArgs(Object... args) {
        String[] result = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = String.valueOf(args[i]);
        }
        return result;
    }
}
