package com.example.printexpress.data.repository;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.printexpress.data.AppException;
import com.example.printexpress.data.db.AppDatabase;
import com.example.printexpress.data.db.Db;
import com.example.printexpress.data.model.Address;
import com.example.printexpress.data.model.Requests;
import com.example.printexpress.util.Validator;

import java.util.List;

/** Delivery addresses. Every query filters by user_id so users only see their own data. */
public class AddressRepository {

    private final AppDatabase database;

    public AddressRepository(AppDatabase database) {
        this.database = database;
    }

    public List<Address> getAddresses(long userId) {
        return Db.list(database.getReadableDatabase(),
                "SELECT * FROM addresses WHERE user_id = ? ORDER BY is_default DESC, address_id",
                c -> new Address(Db.lng(c, "address_id"), Db.str(c, "label"), Db.str(c, "address_line"),
                        Db.str(c, "city"), Db.str(c, "postal_code"), Db.bool(c, "is_default")),
                userId);
    }

    public boolean exists(long userId, long addressId) {
        return Db.number(database.getReadableDatabase(),
                "SELECT COUNT(*) FROM addresses WHERE user_id = ? AND address_id = ?", userId, addressId) > 0;
    }

    /** Adds (addressId = 0) or updates an address, then returns the new list. */
    public List<Address> save(long userId, long addressId, Requests.AddressBody a) {
        AppException.check(Validator.length(a.label, "Label", 2, 30));
        AppException.check(Validator.length(a.addressLine, "Address", 5, 200));
        AppException.check(Validator.length(a.city, "City", 2, 60));
        AppException.check(Validator.postalCode(a.postalCode));

        SQLiteDatabase db = database.getWritableDatabase();
        // The first address automatically becomes the default one
        boolean makeDefault = a.isDefault || getAddresses(userId).isEmpty();

        db.beginTransaction();
        try {
            if (makeDefault) {
                ContentValues clear = new ContentValues();
                clear.put("is_default", 0);
                db.update("addresses", clear, "user_id = ?", new String[]{"" + userId});
            }
            ContentValues values = new ContentValues();
            values.put("label", a.label.trim());
            values.put("address_line", a.addressLine.trim());
            values.put("city", a.city.trim());
            values.put("postal_code", a.postalCode.trim());
            values.put("is_default", makeDefault ? 1 : 0);

            if (addressId == 0) {
                values.put("user_id", userId);
                db.insertOrThrow("addresses", null, values);
            } else if (db.update("addresses", values, "user_id = ? AND address_id = ?",
                    new String[]{"" + userId, "" + addressId}) == 0) {
                throw new AppException("Address not found");
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return getAddresses(userId);
    }

    public List<Address> delete(long userId, long addressId) {
        database.getWritableDatabase().delete("addresses", "user_id = ? AND address_id = ?",
                new String[]{"" + userId, "" + addressId});
        return getAddresses(userId);
    }
}
