# 5. Development Plan

Simple, phase-by-phase plan (about 6 weeks). Tick each box as you finish it. Phases 1–5 are
**already implemented** in this project; use the plan to understand the order things were built in,
to explain your process in the report, and to finish phases 6–7.

## Tools

| Purpose | Tool |
|---|---|
| Android app | Android Studio, Java 11 language level, XML layouts, Material Components 1.14 |
| Database | SQLite (built into Android) — viewed with Android Studio *Database Inspector* |
| Testing | JUnit (unit tests) + AndroidX Test (RepositoryTest on the emulator) |
| Diagrams | PlantUML (`docs/diagrams/*.puml`) |
| Version control | Git + GitHub (recommended — commit after every step) |

## Phase plan

| Phase | Week | Work | Output | Status |
|---|---|---|---|---|
| 1. Analysis | 1 | Read brief, list requirements, actors, rules | `01-requirements-analysis.md` | ✅ |
| 2. Design | 1–2 | Use case, class, activity, sequence, ERD, normalisation, UI palette & screen list | `02`, `03`, `04` docs + `diagrams/` | ✅ |
| 3. Database | 2 | Write `schema.sql` + `seed.sql`, `AppDatabase` (SQLiteOpenHelper) | `app/src/main/assets/database/` | ✅ |
| 4. Data layer | 2–3 | Models → `Db` helpers → repositories with business rules → `Async` background runner → RepositoryTest | `app/…/data/` | ✅ |
| 5. Android app | 3–5 | (a) theme, colours, styles, icons (b) base classes & reusable components (c) repositories wired into BaseActivity (d) auth screens (e) home/browse (f) order form (g) orders & tracking (h) profile, addresses, designs (i) notifications, offers, gallery, guidelines, FAQs, support | `app/` | ✅ |
| 6. Testing | 5 | Run unit tests, execute every test case in `06-test-plan.md` on the emulator, fill "Actual result" and screenshot evidence, fix bugs | completed test plan | ☐ you |
| 7. Documentation & report | 6 | Task A essay, design explanations, UI screenshots, user & technical guide, Harvard references, export PDF | final report | ☐ you |

## Order of building inside phase 5 (why this order)

1. **Theme first** – every later screen automatically looks right.
2. **Reusable pieces next** (`BaseActivity`, `GenericAdapter`, `Async`, `Validator`,
   `StateView` …) – each new screen then needs only a little code.
3. **Login/Register** – every other screen needs the logged-in user id.
4. **Browse → Product → Order** – the core business flow (most marks).
5. **Order tracking, cancel/reschedule** – depends on orders existing.
6. **Extras** – notifications, offers, gallery, guidelines, FAQs, support.

## Suggested Git commits

```
1  docs: requirements and design diagrams
2  db: sqlite schema and sample data
3  data: sqlite helper, repositories, rules
4  test: repository tests
5  app: theme and reusable components
6  app: login and register with validation
7  app: home, browse, product order form
8  app: orders, tracking, reschedule, cancel
9  app: profile, addresses, designs upload
10 app: notifications, offers, gallery, guidelines, faqs, support
11 test: unit tests and test plan results
```

## Risks

| Risk | Plan |
|---|---|
| Emulator too slow on the PC | Use a real phone with USB debugging, close other programs, or run the emulator with more RAM. |
| Sample data out of date after editing SQL | Clear the app storage or raise `DB_VERSION`. |
| Running out of time | Phases 6–7 first; the code is already complete. |
