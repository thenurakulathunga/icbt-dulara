# 8. Technical Guide (Task F)

## 8.1 Requirements

| Software | Version used |
|---|---|
| Android Studio | Latest (bundled JDK) |
| Android SDK | compileSdk 37, minSdk 26 (Android 8.0) |
| Database | SQLite (built into Android — nothing to install) |

No server, XAMPP or internet connection is needed.

## 8.2 Run it

1. Open the project folder in Android Studio and wait for Gradle sync.
2. Start an emulator (or plug in a phone with USB debugging on) and press **Run ▶**.
3. Log in with **demo@printxpress.lk** / **Demo@123** (or phone **0771234567**).

The first start creates `printxpress.db` from `assets/database/schema.sql` + `seed.sql`.
To start again with fresh sample data: *Settings → Apps → PrintXpress → Storage → Clear storage*
(or uninstall / reinstall).

## 8.3 Looking inside the database (for screenshots / marking)

Android Studio → **View → Tool Windows → App Inspection → Database Inspector** → select
`printxpress.db`. You can browse every table and run SQL live, e.g.

```sql
SELECT o.order_id, u.full_name, o.status, (o.subtotal - o.discount_amount + o.delivery_fee) AS total
FROM orders o JOIN users u ON u.user_id = o.user_id;
```

## 8.4 Demonstrating staff actions

Profile → **Staff demo panel** (plays the PrintXpress staff role):

| Action | How |
|---|---|
| Move an order forward | Tap an order → choose Processing / Printing / Ready / Completed |
| Send an offer to every customer | Toolbar 🏷 → choose an offer |
| Reply to a support question | Toolbar 💬 → choose a question → type reply |

The customer receives a phone notification and sees the new status / reply in the app.

## 8.5 Project structure

```
Thashmika/
├── app/
│   └── src/main/
│       ├── assets/database/
│       │   ├── schema.sql                14 tables (3NF)
│       │   └── seed.sql                  sample data
│       ├── java/com/example/printexpress/
│       │   ├── PrintXpressApp.java       creates notification channel
│       │   ├── data/
│       │   │   ├── db/AppDatabase        SQLiteOpenHelper: creates & seeds the DB
│       │   │   ├── db/Db                 query helpers (list / one / number)
│       │   │   ├── repository/           User, Address, Catalog, Design, Order,
│       │   │   │                         Notification, Content, Support repositories
│       │   │   ├── model/                User, Product, OrderDetail, Requests …
│       │   │   ├── Async                 background thread → UI thread
│       │   │   ├── AppException          friendly rule-broken message
│       │   │   └── SessionManager        logged-in user id (SharedPreferences)
│       │   ├── ui/
│       │   │   ├── base/                 BaseActivity, BaseFragment, BaseListActivity
│       │   │   ├── common/               GenericAdapter, StateView, QuantityView,
│       │   │   │                         PriceSummaryView, OrderStatusView, MenuRowView, Form
│       │   │   └── auth/ main/ product/ order/ profile/ support/   screens
│       │   └── util/                     Validator, PriceCalculator, PasswordHasher,
│       │                                 Formatter, StatusHelper, IconMapper, Notification*
│       └── res/                          layouts, colors, themes, styles, icons
│   ├── src/test/                         JUnit unit tests (run on the PC)
│   └── src/androidTest/                  RepositoryTest (runs on a phone/emulator)
└── docs/                                 analysis, design, diagrams, plans, tests, guides
```

## 8.6 How a "Place order" tap travels

`ProductDetailActivity` validates the form → `requestWithLoading(() -> repo.orders.placeOrder(userId(), order), …)`
(BaseActivity) → `Async` runs it on a background thread → `OrderRepository` checks every business
rule, recalculates the price, and inserts the order + item in one SQLite transaction → returns an
`OrderDetail` → back on the UI thread the success lambda opens the tracking screen. If a rule is
broken, the repository throws `AppException("…")` and the message appears in a snackbar.

## 8.7 Security & privacy

- Passwords hashed with **PBKDF2-HMAC-SHA256** (10,000 iterations, random 16-byte salt).
- Every customer query includes `WHERE user_id = ?` — one customer can never see another's orders,
  addresses or designs (tested in `RepositoryTest.customerCannotSeeSomeoneElsesOrder`).
- Rules are checked twice: in the screen and again in the repository.
- The database and uploaded files are in the app's private storage (other apps cannot read them).
- All SQL uses `?` parameters → no SQL injection.
- Production ideas: encrypt the database (SQLCipher), add an online API for multi-device use,
  remove the Staff demo panel from customer builds.

## 8.8 Adding a new screen (example of reuse)

```java
public class FaqActivity extends BaseListActivity<Faq> {
    protected String getScreenTitle()  { return "FAQs"; }
    protected int getItemLayout()      { return R.layout.item_info; }
    protected void bindItem(View v, Faq f, int pos) { ItemInfoBinding.bind(v).textHeading.setText(f.getHeading()); }
    protected void loadItems()         { request(() -> repo.content.getFaqs(), false, this::showItems, this::showError); }
    protected int getEmptyIcon()       { return R.drawable.ic_help; }
    protected String getEmptyTitle()   { return "No FAQs"; }
}
```

Toolbar, pull-to-refresh, background loading, empty and error states all come from the base class.

## 8.9 Changing the database

1. Edit `schema.sql` / `seed.sql`.
2. Increase `DB_VERSION` in `AppDatabase.java` (the app then rebuilds the database —
   development only, it deletes existing data), or clear the app's storage.

## 8.10 Tests

| Command | What |
|---|---|
| `gradlew testDebugUnitTest` | 21 unit tests: Validator, PriceCalculator, PasswordHasher |
| `gradlew connectedDebugAndroidTest` | 15 RepositoryTest cases on a running emulator/phone (real SQLite) |

## 8.11 Troubleshooting

| Problem | Fix |
|---|---|
| Sample data looks old / broken after editing SQL | Clear app storage or raise `DB_VERSION`. |
| "Something went wrong" | See *Logcat* filter `PrintXpress` for the real error. |
| Emulator very slow | Close other programs, use a real phone, or a lighter emulator image. |
| No notification pop-ups | Allow notifications for PrintXpress in Android settings (Android 13+). |
