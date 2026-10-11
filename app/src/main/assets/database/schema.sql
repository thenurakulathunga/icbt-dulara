-- =====================================================================
--  PrintXpress - SQLite database schema (stored on the phone)
--  AppDatabase.java runs this file the first time the app starts.
--  All tables are in Third Normal Form (3NF). See docs/03-database-design.md
--
--  SQLite types: INTEGER (whole numbers, also used for true/false 0/1),
--  REAL (decimal numbers), TEXT (strings and dates as 'YYYY-MM-DD').
--  CHECK constraints replace MySQL ENUMs.
-- =====================================================================

-- 1. Customers -----------------------------------------------------------
CREATE TABLE users (
    user_id        INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name      TEXT    NOT NULL,
    email          TEXT    NOT NULL UNIQUE,
    phone          TEXT    NOT NULL UNIQUE,
    password_hash  TEXT    NOT NULL,              -- PBKDF2 "salt:hash", never the plain password
    created_at     TEXT    NOT NULL DEFAULT (datetime('now', 'localtime'))
);

CREATE TABLE addresses (
    address_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id        INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    label          TEXT    NOT NULL,
    address_line   TEXT    NOT NULL,
    city           TEXT    NOT NULL,
    postal_code    TEXT    NOT NULL,
    is_default     INTEGER NOT NULL DEFAULT 0 CHECK (is_default IN (0, 1))
);

-- 2. Product catalogue ---------------------------------------------------
CREATE TABLE categories (
    category_id    INTEGER PRIMARY KEY AUTOINCREMENT,
    name           TEXT    NOT NULL UNIQUE,
    description    TEXT    NOT NULL,
    icon_name      TEXT    NOT NULL               -- name of the icon drawable in the app
);

CREATE TABLE products (
    product_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    category_id     INTEGER NOT NULL REFERENCES categories(category_id),
    name            TEXT    NOT NULL,
    description     TEXT    NOT NULL,
    base_price      REAL    NOT NULL CHECK (base_price >= 0),   -- LKR for ONE unit
    min_quantity    INTEGER NOT NULL DEFAULT 1,
    max_quantity    INTEGER NOT NULL DEFAULT 1000,
    turnaround_days INTEGER NOT NULL DEFAULT 2,
    is_active       INTEGER NOT NULL DEFAULT 1
);

-- Sizes and materials (paper type, fabric ...) a product can be ordered in
CREATE TABLE product_options (
    option_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id     INTEGER NOT NULL REFERENCES products(product_id) ON DELETE CASCADE,
    option_type    TEXT    NOT NULL CHECK (option_type IN ('SIZE', 'MATERIAL')),
    option_name    TEXT    NOT NULL,
    extra_price    REAL    NOT NULL DEFAULT 0     -- added to base_price per unit
);

-- 3. Customer designs (uploaded artwork) ----------------------------------
CREATE TABLE designs (
    design_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id        INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    title          TEXT    NOT NULL,
    original_name  TEXT    NOT NULL,              -- file name the customer picked
    file_name      TEXT    NOT NULL UNIQUE,       -- copy saved in the app's private storage
    uploaded_at    TEXT    NOT NULL DEFAULT (datetime('now', 'localtime'))
);

-- 4. Promotions & orders ---------------------------------------------------
CREATE TABLE promotions (
    promotion_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    title            TEXT    NOT NULL,
    description      TEXT    NOT NULL,
    promo_code       TEXT    NOT NULL UNIQUE,
    discount_percent INTEGER NOT NULL CHECK (discount_percent BETWEEN 1 AND 100),
    min_quantity     INTEGER NOT NULL DEFAULT 1,
    start_date       TEXT    NOT NULL,
    end_date         TEXT    NOT NULL
);

CREATE TABLE orders (
    order_id        INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER NOT NULL REFERENCES users(user_id),
    address_id      INTEGER REFERENCES addresses(address_id) ON DELETE SET NULL,
    promotion_id    INTEGER REFERENCES promotions(promotion_id),
    fulfilment_type TEXT    NOT NULL CHECK (fulfilment_type IN ('PICKUP', 'DELIVERY')),
    scheduled_date  TEXT    NOT NULL,
    status          TEXT    NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING','PROCESSING','PRINTING','READY','COMPLETED','CANCELLED')),
    subtotal        REAL    NOT NULL,
    discount_amount REAL    NOT NULL DEFAULT 0,
    delivery_fee    REAL    NOT NULL DEFAULT 0,
    notes           TEXT,
    created_at      TEXT    NOT NULL DEFAULT (datetime('now', 'localtime')),
    updated_at      TEXT    NOT NULL DEFAULT (datetime('now', 'localtime'))
);

CREATE TABLE order_items (
    order_item_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id           INTEGER NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
    product_id         INTEGER NOT NULL REFERENCES products(product_id),
    size_option_id     INTEGER NOT NULL REFERENCES product_options(option_id),
    material_option_id INTEGER REFERENCES product_options(option_id),
    design_id          INTEGER REFERENCES designs(design_id) ON DELETE SET NULL,   -- uploaded artwork, OR ...
    custom_text        TEXT,                                                       -- ... text to print
    quantity           INTEGER NOT NULL CHECK (quantity > 0),
    unit_price         REAL    NOT NULL                                            -- price at time of ordering
);

-- 5. Notifications -----------------------------------------------------------
CREATE TABLE notifications (
    notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    title           TEXT    NOT NULL,
    message         TEXT    NOT NULL,
    type            TEXT    NOT NULL CHECK (type IN ('ORDER', 'PROMOTION', 'SYSTEM')),
    is_read         INTEGER NOT NULL DEFAULT 0,
    created_at      TEXT    NOT NULL DEFAULT (datetime('now', 'localtime'))
);

-- 6. Design support & resources --------------------------------------------
CREATE TABLE sample_designs (
    sample_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    category_id    INTEGER NOT NULL REFERENCES categories(category_id),
    title          TEXT    NOT NULL,
    description    TEXT    NOT NULL,
    style_color    TEXT    NOT NULL               -- hex colour for the preview card
);

CREATE TABLE guidelines (
    guideline_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    title          TEXT    NOT NULL,
    content        TEXT    NOT NULL,
    sort_order     INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE faqs (
    faq_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    question       TEXT    NOT NULL,
    answer         TEXT    NOT NULL,
    sort_order     INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE support_requests (
    request_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id        INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    subject        TEXT    NOT NULL,
    message        TEXT    NOT NULL,
    status         TEXT    NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'ANSWERED', 'CLOSED')),
    reply          TEXT,
    created_at     TEXT    NOT NULL DEFAULT (datetime('now', 'localtime'))
);

-- Indexes on foreign keys make the joins fast
CREATE INDEX idx_addresses_user ON addresses(user_id);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_options_product ON product_options(product_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_items_order ON order_items(order_id);
CREATE INDEX idx_notifications_user ON notifications(user_id);
