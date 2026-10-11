package com.example.printexpress.data.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.printexpress.data.AppException;
import com.example.printexpress.data.db.AppDatabase;
import com.example.printexpress.data.db.Db;
import com.example.printexpress.data.model.OrderDetail;
import com.example.printexpress.data.model.OrderItem;
import com.example.printexpress.data.model.OrderSummary;
import com.example.printexpress.data.model.Product;
import com.example.printexpress.data.model.ProductOption;
import com.example.printexpress.data.model.Promotion;
import com.example.printexpress.data.model.Requests;
import com.example.printexpress.util.Formatter;
import com.example.printexpress.util.PriceCalculator;
import com.example.printexpress.util.StatusHelper;

import java.time.LocalDate;
import java.util.List;

/**
 * Orders and order items, with ALL the business rules:
 * quantity limits, valid options, artwork, address, date window, promo code,
 * price calculation, and "no changes after printing has started".
 */
public class OrderRepository {

    public static final int MAX_DAYS_AHEAD = 60;

    /** total is calculated, not stored (3NF) */
    private static final String TOTAL = "(o.subtotal - o.discount_amount + o.delivery_fee)";

    private final AppDatabase database;
    private final CatalogRepository catalog;
    private final AddressRepository addresses;
    private final DesignRepository designs;
    private final ContentRepository content;
    private final NotificationRepository notifications;

    public OrderRepository(AppDatabase database, CatalogRepository catalog, AddressRepository addresses,
                           DesignRepository designs, ContentRepository content, NotificationRepository notifications) {
        this.database = database;
        this.catalog = catalog;
        this.addresses = addresses;
        this.designs = designs;
        this.content = content;
        this.notifications = notifications;
    }

    // ---------- Reading ----------

    /** "My orders" list - one row per order showing its first item. */
    public List<OrderSummary> getOrders(long userId) {
        return Db.list(database.getReadableDatabase(), SUMMARY_SQL + "WHERE o.user_id = ? "
                + "ORDER BY o.order_id DESC", OrderRepository::mapSummary, userId);
    }

    /** Every customer's orders (staff demo panel). */
    public List<OrderSummary> getAllOrders() {
        return Db.list(database.getReadableDatabase(), SUMMARY_SQL + "ORDER BY o.order_id DESC",
                OrderRepository::mapSummary);
    }

    private static final String SUMMARY_SQL =
            "SELECT o.order_id, o.status, o.fulfilment_type, o.scheduled_date, o.created_at, "
                    + TOTAL + " AS total_amount, p.name AS product_name, c.icon_name, oi.quantity, "
                    + "u.full_name AS customer_name "
                    + "FROM orders o "
                    + "JOIN users u ON u.user_id = o.user_id "
                    + "JOIN order_items oi ON oi.order_item_id = "
                    + "     (SELECT MIN(order_item_id) FROM order_items WHERE order_id = o.order_id) "
                    + "JOIN products p ON p.product_id = oi.product_id "
                    + "JOIN categories c ON c.category_id = p.category_id ";

    public OrderDetail getOrder(long userId, long orderId) {
        OrderDetail detail = Db.one(database.getReadableDatabase(),
                "SELECT o.*, pr.promo_code, "
                        + "a.address_line || ', ' || a.city || ' ' || a.postal_code AS delivery_address "
                        + "FROM orders o "
                        + "LEFT JOIN addresses a ON a.address_id = o.address_id "
                        + "LEFT JOIN promotions pr ON pr.promotion_id = o.promotion_id "
                        + "WHERE o.order_id = ? AND o.user_id = ?",
                c -> new OrderDetail(Db.lng(c, "order_id"), Db.str(c, "status"), Db.str(c, "fulfilment_type"),
                        Db.str(c, "scheduled_date"), Db.str(c, "delivery_address"), Db.str(c, "promo_code"),
                        Db.dbl(c, "subtotal"), Db.dbl(c, "discount_amount"), Db.dbl(c, "delivery_fee"),
                        Db.str(c, "notes"), Db.str(c, "created_at"), getItems(orderId)),
                orderId, userId);
        if (detail == null) throw new AppException("Order not found");
        return detail;
    }

    private List<OrderItem> getItems(long orderId) {
        return Db.list(database.getReadableDatabase(),
                "SELECT p.name AS product_name, c.icon_name, s.option_name AS size_name, "
                        + "m.option_name AS material_name, d.title AS design_title, oi.custom_text, "
                        + "oi.quantity, oi.unit_price "
                        + "FROM order_items oi "
                        + "JOIN products p ON p.product_id = oi.product_id "
                        + "JOIN categories c ON c.category_id = p.category_id "
                        + "JOIN product_options s ON s.option_id = oi.size_option_id "
                        + "LEFT JOIN product_options m ON m.option_id = oi.material_option_id "
                        + "LEFT JOIN designs d ON d.design_id = oi.design_id "
                        + "WHERE oi.order_id = ? ORDER BY oi.order_item_id",
                c -> new OrderItem(Db.str(c, "product_name"), Db.str(c, "icon_name"), Db.str(c, "size_name"),
                        Db.str(c, "material_name"), Db.str(c, "design_title"), Db.str(c, "custom_text"),
                        Db.integer(c, "quantity"), Db.dbl(c, "unit_price")),
                orderId);
    }

    // ---------- Placing an order ----------

    public OrderDetail placeOrder(long userId, Requests.Order req) {
        Product product = catalog.getProduct(req.productId);

        // 1. Quantity
        if (req.quantity < product.getMinQuantity() || req.quantity > product.getMaxQuantity()) {
            throw new AppException("Quantity must be between " + product.getMinQuantity()
                    + " and " + product.getMaxQuantity());
        }

        // 2. Size and material must belong to this product
        ProductOption size = req.sizeOptionId == null ? null
                : catalog.getOption(product.getProductId(), req.sizeOptionId, "SIZE");
        if (size == null) throw new AppException("Please choose a valid size");

        ProductOption material = null;
        if (!catalog.getOptions(product.getProductId(), "MATERIAL").isEmpty()) {
            material = req.materialOptionId == null ? null
                    : catalog.getOption(product.getProductId(), req.materialOptionId, "MATERIAL");
            if (material == null) throw new AppException("Please choose a material");
        }

        // 3. Artwork: an uploaded design OR custom text
        String customText = req.customText == null || req.customText.trim().isEmpty() ? null : req.customText.trim();
        if (req.designId == null && customText == null) {
            throw new AppException("Upload a design or enter the text to print");
        }
        if (customText != null && customText.length() > 200) {
            throw new AppException("Custom text can be up to 200 characters");
        }
        if (req.designId != null && !designs.exists(userId, req.designId)) {
            throw new AppException("Selected design was not found");
        }

        // 4. Pickup or delivery
        boolean delivery = "DELIVERY".equals(req.fulfilmentType);
        if (!delivery && !"PICKUP".equals(req.fulfilmentType)) {
            throw new AppException("Choose pickup or delivery");
        }
        if (delivery && (req.addressId == null || !addresses.exists(userId, req.addressId))) {
            throw new AppException("Please choose a delivery address");
        }

        // 5. Date must allow time to print
        LocalDate date = parseDate(req.scheduledDate);
        checkDate(date, product.getTurnaroundDays());

        // 6. Promo code (optional)
        Promotion promo = null;
        if (req.promoCode != null && !req.promoCode.trim().isEmpty()) {
            promo = content.validatePromoCode(req.promoCode, req.quantity);
        }

        // 7. Price - always recalculated here, never taken from the screen
        double unitPrice = PriceCalculator.unitPrice(product.getBasePrice(), size.getExtraPrice(),
                material == null ? 0 : material.getExtraPrice());
        double subtotal = PriceCalculator.subtotal(unitPrice, req.quantity);
        double discount = promo == null ? 0 : PriceCalculator.discount(subtotal, promo.getDiscountPercent());
        double deliveryFee = PriceCalculator.deliveryFee(delivery);

        if (req.notes != null && req.notes.length() > 300) {
            throw new AppException("Notes can be up to 300 characters");
        }

        // 8. Save order + item together: both are saved, or neither (transaction)
        SQLiteDatabase db = database.getWritableDatabase();
        long orderId;
        db.beginTransaction();
        try {
            ContentValues order = new ContentValues();
            order.put("user_id", userId);
            order.put("address_id", delivery ? req.addressId : null);
            order.put("promotion_id", promo == null ? null : promo.getPromotionId());
            order.put("fulfilment_type", req.fulfilmentType);
            order.put("scheduled_date", date.toString());
            order.put("subtotal", subtotal);
            order.put("discount_amount", discount);
            order.put("delivery_fee", deliveryFee);
            order.put("notes", req.notes == null || req.notes.trim().isEmpty() ? null : req.notes.trim());
            orderId = db.insertOrThrow("orders", null, order);

            ContentValues item = new ContentValues();
            item.put("order_id", orderId);
            item.put("product_id", product.getProductId());
            item.put("size_option_id", size.getOptionId());
            item.put("material_option_id", material == null ? null : material.getOptionId());
            item.put("design_id", req.designId);
            item.put("custom_text", customText);
            item.put("quantity", req.quantity);
            item.put("unit_price", unitPrice);
            db.insertOrThrow("order_items", null, item);

            notifications.add(userId, "Order #" + orderId + " confirmed",
                    "We received your order for " + req.quantity + " x " + product.getName() + ".", "ORDER");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return getOrder(userId, orderId);
    }

    // ---------- Changing an order ----------

    public OrderDetail reschedule(long userId, long orderId, String newDate) {
        OrderDetail order = getOrder(userId, orderId);
        checkChangeable(order);
        LocalDate date = parseDate(newDate);
        int turnaround = (int) Db.number(database.getReadableDatabase(),
                "SELECT MAX(p.turnaround_days) FROM order_items oi JOIN products p ON p.product_id = oi.product_id "
                        + "WHERE oi.order_id = ?", orderId);
        checkDate(date, turnaround);

        update(orderId, "scheduled_date", date.toString());
        notifications.add(userId, "Order #" + orderId + " rescheduled",
                "Your new date is " + Formatter.date(date) + ".", "ORDER");
        return getOrder(userId, orderId);
    }

    public OrderDetail cancel(long userId, long orderId) {
        checkChangeable(getOrder(userId, orderId));
        update(orderId, "status", "CANCELLED");
        notifications.add(userId, "Order #" + orderId + " cancelled", "Your order has been cancelled.", "ORDER");
        return getOrder(userId, orderId);
    }

    /** Staff moves an order to a new status. The customer gets a notification. */
    public void updateStatus(long orderId, String status) {
        String[] row = Db.one(database.getReadableDatabase(),
                "SELECT user_id, fulfilment_type FROM orders WHERE order_id = ?",
                c -> new String[]{Db.str(c, "user_id"), Db.str(c, "fulfilment_type")}, orderId);
        if (row == null) throw new AppException("Order not found");

        update(orderId, "status", status);
        notifications.add(Long.parseLong(row[0]), "Order #" + orderId + ": " + StatusHelper.label(status),
                statusMessage(status, row[1]), "ORDER");
    }

    // ---------- helpers ----------

    private void update(long orderId, String column, String value) {
        ContentValues values = new ContentValues();
        values.put(column, value);
        values.put("updated_at", java.time.LocalDateTime.now().withNano(0).toString().replace('T', ' '));
        database.getWritableDatabase().update("orders", values, "order_id = ?", new String[]{"" + orderId});
    }

    private static void checkChangeable(OrderDetail order) {
        if (!order.isCanChange()) {
            throw new AppException("This order can no longer be changed because it is "
                    + StatusHelper.label(order.getStatus()).toLowerCase());
        }
    }

    private static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text);
        } catch (Exception e) {
            throw new AppException("Choose a date");
        }
    }

    private static void checkDate(LocalDate date, int turnaroundDays) {
        LocalDate earliest = LocalDate.now().plusDays(turnaroundDays);
        if (date.isBefore(earliest)) {
            throw new AppException("The earliest available date is " + Formatter.date(earliest));
        }
        if (date.isAfter(LocalDate.now().plusDays(MAX_DAYS_AHEAD))) {
            throw new AppException("Please choose a date within the next " + MAX_DAYS_AHEAD + " days");
        }
    }

    private static String statusMessage(String status, String fulfilmentType) {
        switch (status) {
            case "PROCESSING":
                return "We are checking your artwork and preparing your order.";
            case "PRINTING":
                return "Your order is now being printed.";
            case "READY":
                return "DELIVERY".equals(fulfilmentType)
                        ? "Your order is ready and will be delivered soon."
                        : "Your order is ready for pickup at our Colombo branch.";
            case "COMPLETED":
                return "Your order is complete. Thank you for choosing PrintXpress!";
            case "CANCELLED":
                return "Your order has been cancelled.";
            default:
                return "Your order status was updated.";
        }
    }

    private static OrderSummary mapSummary(Cursor c) {
        return new OrderSummary(Db.lng(c, "order_id"), Db.str(c, "product_name"), Db.str(c, "icon_name"),
                Db.integer(c, "quantity"), Db.str(c, "status"), Db.str(c, "fulfilment_type"),
                Db.str(c, "scheduled_date"), Db.dbl(c, "total_amount"), Db.str(c, "created_at"),
                Db.str(c, "customer_name"));
    }
}
