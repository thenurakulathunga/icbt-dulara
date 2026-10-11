# 1. Requirements Analysis — PrintXpress

**Module:** CSE5011 Mobile Application Development · **Assessment:** WRIT1 (100%)

## 1.1 What the brief asks for

PrintXpress is a Sri Lankan digital printing service. Individuals and small businesses order business
cards, flyers, posters, banners, stickers, mugs and t‑shirts. The **customer** mobile app must let them:

1. Browse products by type, material and size.
2. Order by choosing specifications, uploading a design **or** typing text, and choosing pickup/delivery.
3. Track orders and get notifications about progress and promotions.
4. Use a sample gallery, print guidelines, FAQs and support.

**Constraint:** native development only (no Flutter / React Native / Ionic).

## 1.2 Marking map (what to hand in)

| Task | Marks | Deliverable | Where in this project |
|---|---|---|---|
| A – Compare mobile OS, tools, technologies (1000 words) | 10 | Essay | Key points: `09-task-a-comparison-notes.md` (write the essay yourself) |
| B – System & database design | 10 | Use case, class, activity, ER + normalised schema | `02-system-design.md`, `03-database-design.md`, `diagrams/` |
| C – Attractive UI | 20 | Screens + design rationale | `04-ui-design.md`, app screenshots |
| D – Working app + DB + validation | 40 | Android app + SQLite | `app/` (database scripts in `app/src/main/assets/database/`) |
| E – Test plan, test data, results | 10 | Test cases table | `06-test-plan.md`, unit tests |
| F – User + technical documentation | 10 | Guides | `07-user-guide.md`, `08-technical-guide.md` |

Task D carries 40% of the marks, so the app has to be **fully working with robust validation**.

## 1.3 Actors

| Actor | Description |
|---|---|
| **Customer** | Main user of the mobile app. Registers, orders, tracks, gets alerts. |
| **Staff member** | PrintXpress employee. Updates order status, sends promotions, replies to support (demonstrated through the in-app **Staff demo panel** — no separate staff app is required by the brief). |
| **Notification service** | System actor that creates alerts when order status changes or a promotion is sent. |

## 1.4 Functional requirements

| ID | Requirement | Brief section |
|---|---|---|
| FR1 | Register with name, email, Sri Lankan mobile number and password | Authentication |
| FR2 | Log in with **email or phone** + password | Authentication |
| FR3 | View/edit profile, change password, log out | Profiles |
| FR4 | Add / edit / delete delivery addresses, choose a default | Profiles |
| FR5 | Upload, list and delete saved designs (PDF/PNG/JPG ≤ 10 MB) | Profiles / ordering |
| FR6 | Browse categories and products with price, specs and preview | Browsing |
| FR7 | Search products and filter by category | Browsing |
| FR8 | Choose size, material/paper type and quantity with live price | Ordering |
| FR9 | Provide artwork by uploaded design **or** custom text | Ordering |
| FR10 | Choose pickup or delivery, address and date | Ordering |
| FR11 | Apply a promo code (validated for dates and minimum quantity) | Offers |
| FR12 | View order history and order details | Order management |
| FR13 | Track status: Pending → Processing → Printing → Ready → Completed | Order management |
| FR14 | Reschedule or cancel **only before printing begins** | Order management |
| FR15 | Receive alerts for confirmation, progress, completion and promotions | Notifications |
| FR16 | View seasonal offers | Offers |
| FR17 | Browse a sample design gallery | Design support |
| FR18 | Read print guidelines (file types, CMYK, bleed, resolution) | Design support |
| FR19 | Read FAQs and contact support (call, email, chat, in-app question) | Design support |
| FR20 | Staff can update order status, broadcast promotions, reply to support | Supporting |

## 1.5 Non-functional requirements

| ID | Requirement | How it is met |
|---|---|---|
| NFR1 Security | Passwords never stored in plain text | PBKDF2 hashing with random salt |
| NFR2 Security | Only logged-in users access their own data | Session keeps only the user id; every SQL query filters by `user_id` |
| NFR3 Privacy (EDGE: Ethical) | Collect only needed data, user consent | Consent checkbox on register, no extra personal data |
| NFR4 Validation | Invalid data blocked on the screen **and** in the data layer | `Validator` (screens) + repositories re-check every rule + SQLite CHECK / UNIQUE / FOREIGN KEY constraints |
| NFR5 Usability | Consistent, dark, easy UI | Material 3, shared styles, reusable components |
| NFR6 Reliability | Friendly errors, works offline | Local SQLite, `Async` + `AppException` + `StateView` (loading / empty / error + retry) |
| NFR7 Maintainability (EDGE: Digital) | Modular code | Layers + base classes + reusable views |
| NFR8 Compatibility | Android 8.0+ (API 26) | `minSdk 26` |

## 1.6 Business rules

1. Quantity must be between the product's `min_quantity` and `max_quantity`.
2. Earliest date = today + product turnaround days; latest = today + 60 days.
3. Delivery costs a flat **LKR 350**; pickup is free.
4. A promo code must be active today and the quantity must reach its minimum.
5. Cancelling or rescheduling is allowed only while status is **PENDING** or **PROCESSING**.
6. Price is always recalculated in `OrderRepository` when saving (the form price is a live preview).
7. Every status change creates a notification for the customer.

## 1.7 Assumptions & scope

- Payment is cash on pickup/delivery or bank transfer (online card payment is out of scope).
- Data is stored on the phone (SQLite). Staff actions are shown with a Staff demo panel inside the app; a real multi-device system would add an online API later.
- One product per order in the app UI; the database supports many items per order (future cart).
- Notifications are stored in the database and shown as phone alerts when the app is used; Firebase Cloud Messaging is a future improvement.
