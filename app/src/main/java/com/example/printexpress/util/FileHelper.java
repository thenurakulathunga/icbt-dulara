package com.example.printexpress.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

/** Helpers for the file the customer picks from the phone (artwork upload). */
public final class FileHelper {

    public static final long MAX_SIZE_BYTES = 10 * 1024 * 1024;   // 10 MB
    public static final String[] ALLOWED_TYPES = {"application/pdf", "image/png", "image/jpeg"};

    private FileHelper() {
    }

    /** "application/pdf" -> "pdf". Returns null for file types we do not accept. */
    public static String extensionFor(String mimeType) {
        if ("application/pdf".equals(mimeType)) return "pdf";
        if ("image/png".equals(mimeType)) return "png";
        if ("image/jpeg".equals(mimeType)) return "jpg";
        return null;
    }

    /** The real file name, e.g. "my-logo.pdf". */
    public static String getFileName(Context context, Uri uri) {
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) return cursor.getString(index);
            }
        }
        return "design";
    }

    public static long getFileSize(Context context, Uri uri) {
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (index >= 0) return cursor.getLong(index);
            }
        }
        return 0;
    }
}
