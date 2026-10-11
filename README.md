# PrintXpress — Mobile Printing Orders (CSE5011 WRIT1)

Native **Android (Java)** app for ordering printing in Sri Lanka, with a **local SQLite database**.
Dark theme with orange branding. No server or internet needed.

```
Screens (Activities / Fragments) → Async (background thread) → Repositories (rules + SQL) → SQLite (printxpress.db)
```

## Quick start
1. Open this folder in Android Studio → Run ▶ on an emulator or phone.
2. Log in: **demo@printxpress.lk** / **Demo@123** (or phone 0771234567).
3. Profile → **Staff demo panel** to move orders through Processing → Printing → Ready, send offers
   and reply to support.

Full instructions: [docs/08-technical-guide.md](docs/08-technical-guide.md)

## Features
Register/login (email or phone) · profile & password · delivery addresses · browse/search by
category · size, material & quantity with live price · upload artwork (PDF/PNG/JPG) or custom text ·
pickup or delivery with date rules · promo codes · order tracking (5 steps) · reschedule/cancel
before printing · notifications (in-app + phone alerts) · seasonal offers · design gallery · print
guidelines · FAQs · support with replies · staff demo panel.

## Documentation (`docs/`)
| File | Content |
|---|---|
| [01-requirements-analysis](docs/01-requirements-analysis.md) | Brief analysis, marking map, requirements, business rules |
| [02-system-design](docs/02-system-design.md) | Architecture, use case, class, activity, sequence diagrams |
| [03-database-design](docs/03-database-design.md) | ERD, normalisation UNF→3NF, SQLite schema decisions |
| [04-ui-design](docs/04-ui-design.md) | Design principles, palette, screens |
| [05-development-plan](docs/05-development-plan.md) | Phases, timeline, risks |
| [06-test-plan](docs/06-test-plan.md) | Test cases with data and results |
| [07-user-guide](docs/07-user-guide.md) | For customers |
| [08-technical-guide](docs/08-technical-guide.md) | Setup, structure, security, troubleshooting |
| [09-task-a-comparison-notes](docs/09-task-a-comparison-notes.md) | Research points for Task A |
| [diagrams/](docs/diagrams) | PNG + PlantUML source of every diagram |

Database scripts: `app/src/main/assets/database/schema.sql` and `seed.sql`.

## Tests
`gradlew testDebugUnitTest` (21 unit tests) · `gradlew connectedDebugAndroidTest` (15 database tests on an emulator)
