package com.example.printexpress.data.repository;

import android.database.Cursor;

import com.example.printexpress.data.AppException;
import com.example.printexpress.data.db.AppDatabase;
import com.example.printexpress.data.db.Db;
import com.example.printexpress.data.model.Category;
import com.example.printexpress.data.model.Product;
import com.example.printexpress.data.model.ProductDetail;
import com.example.printexpress.data.model.ProductOption;

import java.util.ArrayList;
import java.util.List;

/** Categories, products and product options (sizes / materials). */
public class CatalogRepository {

    private static final String PRODUCT_SELECT =
            "SELECT p.*, c.name AS category_name, c.icon_name "
                    + "FROM products p JOIN categories c ON c.category_id = p.category_id "
                    + "WHERE p.is_active = 1 ";

    private final AppDatabase database;

    public CatalogRepository(AppDatabase database) {
        this.database = database;
    }

    public List<Category> getCategories() {
        return Db.list(database.getReadableDatabase(), "SELECT * FROM categories ORDER BY category_id",
                c -> new Category(Db.lng(c, "category_id"), Db.str(c, "name"),
                        Db.str(c, "description"), Db.str(c, "icon_name")));
    }

    /** categoryId and search are optional (null = no filter). */
    public List<Product> getProducts(Long categoryId, String search) {
        StringBuilder sql = new StringBuilder(PRODUCT_SELECT);
        List<Object> args = new ArrayList<>();
        if (categoryId != null) {
            sql.append("AND p.category_id = ? ");
            args.add(categoryId);
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (p.name LIKE ? OR c.name LIKE ?) ");
            String like = "%" + search.trim() + "%";
            args.add(like);
            args.add(like);
        }
        sql.append("ORDER BY p.category_id, p.base_price");
        return Db.list(database.getReadableDatabase(), sql.toString(), CatalogRepository::mapProduct, args.toArray());
    }

    public Product getProduct(long productId) {
        Product product = Db.one(database.getReadableDatabase(), PRODUCT_SELECT + "AND p.product_id = ?",
                CatalogRepository::mapProduct, productId);
        if (product == null) throw new AppException("Product not found");
        return product;
    }

    public ProductDetail getProductDetail(long productId) {
        return new ProductDetail(getProduct(productId), getOptions(productId, "SIZE"), getOptions(productId, "MATERIAL"));
    }

    public List<ProductOption> getOptions(long productId, String type) {
        return Db.list(database.getReadableDatabase(),
                "SELECT * FROM product_options WHERE product_id = ? AND option_type = ? ORDER BY extra_price, option_id",
                CatalogRepository::mapOption, productId, type);
    }

    /** Makes sure the option really belongs to the product. Returns null if not. */
    public ProductOption getOption(long productId, long optionId, String type) {
        return Db.one(database.getReadableDatabase(),
                "SELECT * FROM product_options WHERE product_id = ? AND option_id = ? AND option_type = ?",
                CatalogRepository::mapOption, productId, optionId, type);
    }

    private static Product mapProduct(Cursor c) {
        return new Product(Db.lng(c, "product_id"), Db.lng(c, "category_id"), Db.str(c, "category_name"),
                Db.str(c, "icon_name"), Db.str(c, "name"), Db.str(c, "description"), Db.dbl(c, "base_price"),
                Db.integer(c, "min_quantity"), Db.integer(c, "max_quantity"), Db.integer(c, "turnaround_days"));
    }

    private static ProductOption mapOption(Cursor c) {
        return new ProductOption(Db.lng(c, "option_id"), Db.str(c, "option_type"),
                Db.str(c, "option_name"), Db.dbl(c, "extra_price"));
    }
}
