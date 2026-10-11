package com.example.printexpress.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.example.printexpress.data.AppException;
import com.example.printexpress.data.db.AppDatabase;
import com.example.printexpress.data.db.Db;
import com.example.printexpress.data.model.Design;
import com.example.printexpress.util.FileHelper;
import com.example.printexpress.util.Validator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * Customer artwork. The picked file is COPIED into the app's private folder
 * (files/designs/) with a random name, and only that name is saved in the database.
 */
public class DesignRepository {

    private final AppDatabase database;
    private final Context context;

    public DesignRepository(AppDatabase database, Context context) {
        this.database = database;
        this.context = context.getApplicationContext();
    }

    public List<Design> getDesigns(long userId) {
        return Db.list(database.getReadableDatabase(),
                "SELECT * FROM designs WHERE user_id = ? ORDER BY design_id DESC",
                c -> new Design(Db.lng(c, "design_id"), Db.str(c, "title"), Db.str(c, "original_name"),
                        Db.str(c, "file_name"), Db.str(c, "uploaded_at")),
                userId);
    }

    public boolean exists(long userId, long designId) {
        return Db.number(database.getReadableDatabase(),
                "SELECT COUNT(*) FROM designs WHERE user_id = ? AND design_id = ?", userId, designId) > 0;
    }

    public List<Design> upload(long userId, Uri uri, String title) throws IOException {
        AppException.check(Validator.length(title, "Title", 3, 80));

        String extension = FileHelper.extensionFor(context.getContentResolver().getType(uri));
        if (extension == null) throw new AppException("Only PDF, PNG and JPG files are allowed");
        if (FileHelper.getFileSize(context, uri) > FileHelper.MAX_SIZE_BYTES) {
            throw new AppException("File is too large. Maximum size is 10 MB");
        }

        String storedName = UUID.randomUUID() + "." + extension;
        copy(uri, new File(designFolder(), storedName));

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("title", title.trim());
        values.put("original_name", FileHelper.getFileName(context, uri));
        values.put("file_name", storedName);
        database.getWritableDatabase().insertOrThrow("designs", null, values);
        return getDesigns(userId);
    }

    public List<Design> delete(long userId, long designId) {
        String fileName = Db.one(database.getReadableDatabase(),
                "SELECT file_name FROM designs WHERE user_id = ? AND design_id = ?",
                c -> Db.str(c, "file_name"), userId, designId);
        if (fileName != null) {
            database.getWritableDatabase().delete("designs", "user_id = ? AND design_id = ?",
                    new String[]{"" + userId, "" + designId});
            new File(designFolder(), fileName).delete();
        }
        return getDesigns(userId);
    }

    private File designFolder() {
        File folder = new File(context.getFilesDir(), "designs");
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    private void copy(Uri from, File to) throws IOException {
        try (InputStream in = context.getContentResolver().openInputStream(from);
             OutputStream out = new FileOutputStream(to)) {
            if (in == null) throw new IOException("Cannot open file");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
    }
}
