# 6. Test Plan (Task E)

## 6.1 Strategy

| Level | What | How |
|---|---|---|
| **Unit** | Validation rules, price calculation, password hashing, formatting | JUnit on the PC — `app/src/test` (21 tests) |
| **Database / integration** | Repositories with the real SQLite code: login, ordering rules, cancel/reschedule, privacy | `app/src/androidTest/RepositoryTest` (15 tests, runs on an emulator or phone) |
| **Schema** | Tables, sample data, constraints (CHECK, UNIQUE, FOREIGN KEY), key queries | `schema.sql` + `seed.sql` executed in the official `sqlite3` tool |
| **System (UI)** | Every screen on the emulator or a phone | Manual, follow the cases below, screenshot as evidence |
| **Validation** | Boundary values, wrong formats, empty fields | Screen (instant red errors) **and** repository (second check) **and** database constraints (third) |

Techniques: **equivalence partitioning** (valid / invalid groups) and **boundary value analysis**
(e.g. quantity 49 / 50 / 5000 / 5001 for business cards).

**How to run**
```
gradlew testDebugUnitTest              unit tests (no device needed)
gradlew connectedDebugAndroidTest      RepositoryTest (start an emulator or plug in a phone first)
```

## 6.2 Results so far (10 Oct 2026)

| Suite | Tests | Result |
|---|---|---|
| `ValidatorTest` | 10 | ✅ passed |
| `PriceCalculatorTest` | 6 | ✅ passed |
| `PasswordHasherTest` | 5 | ✅ passed |
| Schema check in `sqlite3` 3.54 | 14 tables, 14 products, 62 options, order queries, 3 constraint checks | ✅ as expected (see 6.4) |
| `RepositoryTest` (15, on device) | 15 | ⚠️ **not yet run** — the development PC ran out of memory while the emulator was running. Run it on a machine with more RAM or a real phone and record the result here. |

## 6.3 Test cases

Status: **Pass** = executed and passed · **To run** = execute and fill in.
*Auto* = covered by an automated test named in the Evidence column.

### Authentication

| ID | Test | Test data | Expected result | Evidence | Status |
|---|---|---|---|---|---|
| T01 | Login with empty fields | (blank) | "Enter your email or mobile number", "Password is required" under the fields | Emulator screenshot (earlier build, same screen code) | Pass |
| T02 | Login with invalid identifier | `d` | "Enter a valid Sri Lankan mobile number" | Emulator screenshot (earlier build) | Pass |
| T03 | Login wrong password | demo@printxpress.lk / `wrong` | "Incorrect email/phone or password" | Auto: `RepositoryTest.wrongPasswordIsRejected` | To run |
| T04 | Login with email | demo@printxpress.lk / Demo@123 | Home opens | Auto: `demoUserCanLogInWithEmailOrPhone` + manual | To run |
| T05 | Login with phone (both formats) | 0771234567 and +94771234567 / Demo@123 | Logged in as Demo Customer | Auto: `demoUserCanLogInWithEmailOrPhone` | To run |
| T06 | Register invalid data | name `A`, email `bad`, phone `123`, password `short` | Error under each field | Unit: `ValidatorTest`; Auto: `invalidRegistrationIsRejectedByTheRepositoryToo` | Pass (unit) |
| T07 | Register duplicate email | kamal@test.lk twice | "already registered" | Auto: `registerStoresNormalisedPhoneAndRejectsDuplicates`; DB: UNIQUE constraint rejected duplicate in sqlite3 | Pass (DB) |
| T08 | Register valid | Kamal Perera, Kamal@Test.lk, +94712345678, Pass1234 | Saved as kamal@test.lk / 0712345678, welcome notification | Auto: same test | To run |
| T09 | Passwords don't match | Pass1234 / Pass1235 | "Passwords do not match" | Unit: `ValidatorTest.confirmPasswordMustMatch` | Pass |
| T10 | Privacy consent unticked | valid data, box unticked | "Please accept the privacy policy" | Manual | To run |
| T11 | Password stored safely | any password | Stored value is "salt:hash", never the password; same password → different hash | Unit: `PasswordHasherTest` (5) | Pass |
| T12 | Logout | Log out → confirm | Login screen; back button does not return | Manual | To run |

### Browsing

| ID | Test | Test data | Expected | Evidence | Status |
|---|---|---|---|---|---|
| T13 | Category filter | Business Cards chip | 2 products | Manual | To run |
| T14 | Search | `mug` | Classic White Mug, Magic Colour Mug | Auto: `sampleContentIsLoaded`; sqlite3 LIKE query returned 2 | Pass (DB) |
| T15 | Search no result | `xyz` | Empty state "No products found" | Manual | To run |
| T16 | Product details | Standard Business Cards | 2 sizes, 2 materials, min 50, ready in 2 days | Manual | To run |
| T17 | Sample content | first start | 7 categories, 6+ guidelines, 6+ FAQs, running offers | Auto: `sampleContentIsLoaded`; sqlite3: 3 offers running today | Pass (DB) |

### Ordering & validation

| ID | Test | Test data | Expected | Evidence | Status |
|---|---|---|---|---|---|
| T18 | Quantity below minimum (boundary) | 49 cards (min 50) | "Minimum order is 50" | Unit: `ValidatorTest.quantityBoundaries`; Auto: `quantityBelowMinimumIsRejected` | Pass (unit) |
| T19 | Quantity at minimum (boundary) | 50 | Accepted | Unit: `quantityBoundaries` | Pass |
| T20 | Quantity above maximum (boundary) | 5001 | "Maximum order is 5000" | Unit: `quantityBoundaries` | Pass |
| T21 | Date too early | today (turnaround 2 days) | "The earliest available date is …" | Auto: `dateBeforeTurnaroundIsRejected` | To run |
| T22 | No artwork | blank text, no design | "Upload a design or enter the text to print" | Auto: `artworkIsRequired` | To run |
| T23 | Delivery without address | DELIVERY, no address | "Please choose a delivery address" | Auto: `deliveryNeedsAnAddress` | To run |
| T24 | Promo below minimum | BULK15 with 100 items | "Code BULK15 needs at least 500 items" | Auto: `promoBelowMinimumQuantityIsRejected` | To run |
| T25 | Valid order with promo + delivery | 600 cards, gloss (+4), BULK15, delivery, +5 days | Subtotal 9,600.00 · Discount 1,440.00 · Delivery 350.00 · **Total 8,510.00**, PENDING | Unit: `PriceCalculatorTest.fullOrderWithPromoAndDelivery`; sqlite3 summary query returned 8510.0; Auto: `validOrderIsPricedCorrectly` | Pass (unit + DB) |
| T26 | Invalid fulfilment value in DB | `'BOAT'` | Rejected by CHECK constraint | sqlite3: "CHECK constraint failed" | Pass |
| T27 | Order for a user that does not exist | user_id 99 | Rejected by FOREIGN KEY | sqlite3: "FOREIGN KEY constraint failed" | Pass |
| T28 | Live price updates | change size / quantity / delivery | Total changes instantly | Manual | To run |
| T29 | Confirm dialog | tap Place order | Dialog shows quantity, date, total | Manual | To run |

### Artwork upload

| ID | Test | Test data | Expected | Evidence | Status |
|---|---|---|---|---|---|
| T30 | Upload PDF | logo.pdf, title "My Logo" | Appears in My designs | Manual | To run |
| T31 | Wrong file type | a .docx file | Not selectable in the picker / "Only PDF, PNG and JPG…" | Manual | To run |
| T32 | File over 10 MB | 12 MB image | "File is too large" | Manual | To run |
| T33 | Title too short | `A` | "Title must be at least 3 characters" | Manual | To run |
| T34 | Pick design for order | tap a design in pick mode | Returns to the form with the title shown | Manual | To run |

### Order management

| ID | Test | Test data | Expected | Evidence | Status |
|---|---|---|---|---|---|
| T35 | Reschedule + cancel a pending order | +9 days, then cancel | New date saved, then CANCELLED | Auto: `pendingOrderCanBeRescheduledAndCancelled` | To run |
| T36 | Cancel after printing started | Staff panel → Printing, then Cancel | "can no longer be changed because it is printing"; buttons hidden | Auto: `orderCannotBeCancelledAfterPrintingStarts` + manual | To run |
| T37 | Privacy | another user asks for order #1 | "Order not found" | Auto: `customerCannotSeeSomeoneElsesOrder` | To run |
| T38 | Tracker display | order in Printing | First 3 steps orange | Manual | To run |
| T39 | Orders filter | Active / Completed / Cancelled chips | List filters correctly | Manual | To run |
| T40 | Staff status change notification | Staff panel → Ready | Phone alert "Order #x: Ready" | Manual | To run |

### Profile, notifications, support

| ID | Test | Test data | Expected | Evidence | Status |
|---|---|---|---|---|---|
| T41 | Address bad postal code | `2000` | "Postal code must be 5 digits" | Unit: `postalCodeIsFiveDigits`; Auto: `addressPostalCodeIsValidated` | Pass (unit) |
| T42 | Add default address | Shop, 12 Main St, Kandy, 20000, default on | Only this address is default | Manual | To run |
| T43 | Change password wrong current | `wrong123` | "Current password is incorrect" | Manual | To run |
| T44 | Unread badge | after placing an order | Bell badge count goes up | Manual | To run |
| T45 | Mark all read | toolbar ✓ | Bold titles and dots disappear | Manual | To run |
| T46 | Support request + staff reply | subject "Bleed help"; reply via Staff panel | Status Answered, reply shown, notification | Manual | To run |
| T47 | Send offer to everyone | Staff panel → Send offer → Christmas Special | Notification for every user | sqlite3: broadcast INSERT created one row per user | Pass (DB) |
| T48 | Copy promo code | tap copy on an offer | Toast "Code … copied" | Manual | To run |

## 6.4 Schema check output (sqlite3 3.54.0)

```
tables|14            products|14            options|62
promos running today|3
order summary: 1|PENDING|DELIVERY|2026-10-15|8510.0|Standard Business Cards|...|600|Demo Customer
order detail:  1|BULK15|No. 25, Temple Road, Nugegoda, Colombo 10250
Error: CHECK constraint failed: fulfilment_type IN ('PICKUP', 'DELIVERY')
Error: FOREIGN KEY constraint failed
Error: UNIQUE constraint failed: users.email
```

## 6.5 Bugs found during testing and fixed

| Bug | Fix |
|---|---|
| Status bar icons turned dark (invisible) on phones in light mode | `EdgeToEdge.enable` forced to dark style in `BaseActivity` |
| Styles named `PrintXpress.X` failed to build (implicit parent) | Added `parent=""` |
| Pull-to-refresh fired while the list was scrolled down | `setOnChildScrollUpCallback` on every list |
| SQLite timestamps ("2026-10-10 14:30:00") could not be parsed by the date formatter | `Formatter.dateTime` accepts the space format |
| Fragments touched `requireContext()` from the background thread | Repositories stored in `BaseFragment.onAttach` |
