# 9. Task A — Research notes (write the essay in your own words)

Task A is a **1000-word critical comparison** that you must write yourself (the brief treats
unacknowledged or outsourced writing as unfair practice). These notes give you the structure and
the points to research and cite (Harvard style) — check every fact against a source you reference.

## Suggested structure (≈1000 words)

| Section | Words | Points to cover |
|---|---|---|
| Introduction | 100 | What PrintXpress needs (camera/file access, notifications, offline-friendly UI, Sri Lankan market) |
| Mobile operating systems | 250 | Android vs iOS (vs HarmonyOS briefly) |
| Development approaches | 250 | Native vs cross-platform vs hybrid/PWA — why native is required here |
| Tools & languages | 200 | Android Studio vs Xcode; Java vs Kotlin; XML Views vs Jetpack Compose |
| Data storage | 150 | SQLite (on device) vs MySQL + REST API (server) vs Firebase (cloud) |
| Conclusion / justification | 50 | Your choice: Android + Java + SQLite, and why |

## Points to research

**Android vs iOS**
- Market share in Sri Lanka (Android is the clear majority — find a figure from StatCounter).
- Device price range → PrintXpress targets individuals and small businesses.
- Openness: side-loading/APK testing, Google Play fee vs Apple fee, Xcode needs a Mac.
- Fragmentation on Android (many screen sizes/versions) → handled with minSdk 26 and responsive layouts.
- Security models: sandboxing, runtime permissions (POST_NOTIFICATIONS, file picker).

**Native vs cross-platform**
- Native: full API access, best performance, platform look & feel; cost: separate code per OS.
- Flutter / React Native: one codebase, but extra runtime layer, plugin dependence — not allowed by the brief.
- PWA: no install, but weaker notifications/file access on iOS.

**Java vs Kotlin; XML vs Compose**
- Kotlin is Google's preferred language; Java is still fully supported and is what the module teaches.
- XML Views + Material Components: mature, lots of learning material; Compose is newer, declarative.
- Critical point: a team must weigh *familiarity and speed of delivery* against *modern features*.

**Data storage**
- SQLite (chosen): built into Android, no server, works offline, relational (foreign keys, transactions), simple to demonstrate; limitation: data is on one device only.
- MySQL behind a REST API (e.g. Spring Boot / PHP): central data for staff and many devices; needs hosting and internet. An app must never connect to MySQL directly (credentials inside the APK).
- Room: Google's layer on top of SQLite — less SQL to write, but hides the SQL a student should understand.
- Firebase: real-time cloud sync and push messages, but NoSQL and vendor lock-in; order data is relational.
- Critical point: SQLite fits a prototype / single-user app; a production PrintXpress would add an online API so staff can see orders.

## Possible sources to look for
- Android Developers documentation (developer.android.com) — architecture, permissions, notifications.
- Apple Developer documentation — App Store requirements, Xcode.
- StatCounter Global Stats — mobile OS share, Sri Lanka.
- SQLite documentation (sqlite.org); Android "Save data using SQLite" guide.
- A textbook from your module reading list (e.g. on Android programming / software engineering).
