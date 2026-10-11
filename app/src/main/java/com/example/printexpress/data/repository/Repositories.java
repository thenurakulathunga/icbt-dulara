package com.example.printexpress.data.repository;

import android.content.Context;

import com.example.printexpress.data.db.AppDatabase;

/**
 * All repositories in one place, created once and shared by every screen.
 *
 *   repo.orders.getOrders(userId)
 *   repo.catalog.getProducts(categoryId, search)
 */
public final class Repositories {

    private static Repositories instance;

    public final UserRepository users;
    public final AddressRepository addresses;
    public final CatalogRepository catalog;
    public final DesignRepository designs;
    public final OrderRepository orders;
    public final NotificationRepository notifications;
    public final ContentRepository content;
    public final SupportRepository support;

    public static synchronized Repositories get(Context context) {
        if (instance == null) instance = new Repositories(context.getApplicationContext());
        return instance;
    }

    private Repositories(Context context) {
        AppDatabase db = AppDatabase.get(context);
        notifications = new NotificationRepository(db);
        users = new UserRepository(db, notifications);
        addresses = new AddressRepository(db);
        catalog = new CatalogRepository(db);
        designs = new DesignRepository(db, context);
        content = new ContentRepository(db);
        support = new SupportRepository(db, notifications);
        orders = new OrderRepository(db, catalog, addresses, designs, content, notifications);
    }
}
