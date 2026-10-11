package com.example.printexpress.data.repository;

import android.database.Cursor;

import com.example.printexpress.data.AppException;
import com.example.printexpress.data.db.AppDatabase;
import com.example.printexpress.data.db.Db;
import com.example.printexpress.data.model.Faq;
import com.example.printexpress.data.model.Guideline;
import com.example.printexpress.data.model.Promotion;
import com.example.printexpress.data.model.SampleDesign;

import java.util.List;

/** Read-only content: promotions, sample gallery, guidelines and FAQs. */
public class ContentRepository {

    /** Dates are stored as 'YYYY-MM-DD' text, so they can be compared directly. */
    private static final String RUNNING_TODAY = "date('now', 'localtime') BETWEEN start_date AND end_date";

    private final AppDatabase database;

    public ContentRepository(AppDatabase database) {
        this.database = database;
    }

    /** Only offers that are running today. */
    public List<Promotion> getActivePromotions() {
        return Db.list(database.getReadableDatabase(),
                "SELECT * FROM promotions WHERE " + RUNNING_TODAY + " ORDER BY discount_percent DESC",
                ContentRepository::mapPromotion);
    }

    /** All offers (staff can send any of them). */
    public List<Promotion> getAllPromotions() {
        return Db.list(database.getReadableDatabase(), "SELECT * FROM promotions ORDER BY promotion_id",
                ContentRepository::mapPromotion);
    }

    /** Checks a promo code for an order of 'quantity' items. Throws with a friendly message if not usable. */
    public Promotion validatePromoCode(String code, int quantity) {
        Promotion promo = Db.one(database.getReadableDatabase(),
                "SELECT * FROM promotions WHERE promo_code = ? AND " + RUNNING_TODAY,
                ContentRepository::mapPromotion, code.trim().toUpperCase());
        if (promo == null) {
            throw new AppException("Promo code is invalid or expired");
        }
        if (quantity < promo.getMinQuantity()) {
            throw new AppException("Code " + promo.getPromoCode() + " needs at least " + promo.getMinQuantity() + " items");
        }
        return promo;
    }

    /** categoryId = null shows every category. */
    public List<SampleDesign> getGallery(Long categoryId) {
        String sql = "SELECT s.*, c.name AS category_name, c.icon_name "
                + "FROM sample_designs s JOIN categories c ON c.category_id = s.category_id "
                + (categoryId == null ? "" : "WHERE s.category_id = ? ")
                + "ORDER BY s.category_id, s.sample_id";
        Object[] args = categoryId == null ? new Object[0] : new Object[]{categoryId};
        return Db.list(database.getReadableDatabase(), sql,
                c -> new SampleDesign(Db.lng(c, "sample_id"), Db.lng(c, "category_id"), Db.str(c, "category_name"),
                        Db.str(c, "icon_name"), Db.str(c, "title"), Db.str(c, "description"), Db.str(c, "style_color")),
                args);
    }

    public List<Guideline> getGuidelines() {
        return Db.list(database.getReadableDatabase(), "SELECT * FROM guidelines ORDER BY sort_order",
                c -> new Guideline(Db.lng(c, "guideline_id"), Db.str(c, "title"), Db.str(c, "content")));
    }

    public List<Faq> getFaqs() {
        return Db.list(database.getReadableDatabase(), "SELECT * FROM faqs ORDER BY sort_order",
                c -> new Faq(Db.lng(c, "faq_id"), Db.str(c, "question"), Db.str(c, "answer")));
    }

    private static Promotion mapPromotion(Cursor c) {
        return new Promotion(Db.lng(c, "promotion_id"), Db.str(c, "title"), Db.str(c, "description"),
                Db.str(c, "promo_code"), Db.integer(c, "discount_percent"), Db.integer(c, "min_quantity"),
                Db.str(c, "start_date"), Db.str(c, "end_date"));
    }
}
