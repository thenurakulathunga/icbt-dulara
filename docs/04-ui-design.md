# 4. UI Design (Task C)

## 4.1 Design principles

| Principle | How it appears in the app |
|---|---|
| **Dark theme, orange brand** | Near-black background `#0F0F12`, cards `#1F1F26`, orange primary `#FF7A00`, light-orange secondary `#FFA94D`. Orange is used only for actions and highlights so it guides the eye. |
| **Consistency** | Every button, text field, card, chip and toolbar comes from one theme (`themes.xml`) and style file (`styles.xml`). No screen sets its own colours. |
| **Material Design 3** | Familiar Android patterns: bottom navigation, outlined text fields, chips, date picker, snackbars. |
| **Feedback** | Loading spinners, empty states with icons, error states with *Try again*, red error text under each wrong field, success snackbars, live price. |
| **Fewer mistakes** | Date picker blocks impossible dates, quantity stepper respects min/max, dropdowns instead of typing, confirm dialogs before ordering or cancelling. |
| **Accessibility** | High contrast (white on near-black, dark text on orange), 52–54dp touch targets, icons with content descriptions, text sizes in `sp`. |

## 4.2 Colour palette (`res/values/colors.xml`)

| Token | Hex | Use |
|---|---|---|
| px_primary | `#FF7A00` | Buttons, active tab, prices, links |
| px_primary_dark | `#E05A00` | Gradient end |
| px_secondary | `#FFA94D` | Category labels, secondary accents |
| px_primary_container | `#3D2209` | Soft orange behind icons, selected chips |
| px_background | `#0F0F12` | Screen background |
| px_card | `#1F1F26` | Cards and fields |
| px_text_primary / secondary / hint | `#F5F5F7` / `#A8A8B3` / `#6E6E7A` | Text hierarchy |
| status colours | amber, blue, purple, teal, green, red | Pending → Cancelled pills |

## 4.3 Screens (18)

| # | Screen | Key elements |
|---|---|---|
| 1 | Splash | Logo, "PrintXpress" wordmark, session check |
| 2 | Login | Email/phone + password, show/hide password, inline errors, demo hint |
| 3 | Register | 5 validated fields, password rules helper, privacy consent |
| 4 | Home | Greeting, notification bell with badge, search bar, offers carousel, categories, latest order, design-help tiles |
| 5 | Browse | Live search, category chips, product cards with price/min/turnaround |
| 6 | Product & order form | Specs card, size/material chips, quantity stepper, upload/text toggle, pickup/delivery toggle, address dropdown, date picker, promo code, price summary, sticky total bar |
| 7 | Orders | Filter chips (All/Active/Completed/Cancelled), status pills, pull to refresh |
| 8 | Order tracking | 5-step progress tracker, items, pickup/delivery info, price summary, Reschedule / Cancel |
| 9 | Profile | Avatar initials, grouped menu rows, log out |
| 10 | Edit profile | Details + change password |
| 11 | Addresses | List, default badge, delete, FAB "Add address" |
| 12 | Address form | Label, address, city, 5-digit postal code, default switch |
| 13 | My designs | File list, upload FAB (PDF/PNG/JPG), pick mode for orders |
| 14 | Notifications | Type icons, unread dot, mark all read |
| 15 | Offers | Gradient cards, copy code |
| 16 | Gallery | 2-column coloured tiles, category filter |
| 17 | Guidelines / FAQs | Expandable cards (same screen for both) |
| 18 | Support | Call / Email / WhatsApp, ask a question, replies |

## 4.4 Screenshots for the report

Run the app (see `08-technical-guide.md`) and take screenshots with the camera button in the
emulator toolbar. Suggested set: Login (with errors), Register, Home, Browse, Product form (upload +
delivery), Order tracking, Orders list, Profile, Gallery, Guidelines. Put them side-by-side in the
report with one sentence each on *why* the design helps the user.

## 4.5 Wireframe of the most important screen (order form)

```
┌──────────────────────────────┐
│ ←  Product details           │
├──────────────────────────────┤
│ [icon] BUSINESS CARDS        │
│        Standard Business Cards│
│        From LKR 12.00 / unit │
│ Classic 300gsm cards…        │
│ 📅 Ready in 2 days           │
│                              │
│ SIZE                         │
│ (Standard ✓) (Square +LKR 3) │
│ MATERIAL / PAPER TYPE        │
│ (Art Card ✓) (Gloss +LKR 4)  │
│ QUANTITY                     │
│ [ − ]        100       [ + ] │
│ Min 50 • Max 5000            │
│ ARTWORK                      │
│ [⬆ Upload design][✎ Text]    │
│ 📁 Choose a saved design  >  │
│ PICKUP OR DELIVERY           │
│ [🏪 Pickup][🚚 Delivery]     │
│ 📅 Pickup date               │
│ 🏷 Promo code      [Apply]   │
│ Notes                        │
│ PRICE SUMMARY                │
│ Subtotal      LKR 1,200.00   │
│ Delivery              Free   │
│ Total         LKR 1,200.00   │
├──────────────────────────────┤
│ Total LKR 1,200.00 [Place order]│
└──────────────────────────────┘
```
