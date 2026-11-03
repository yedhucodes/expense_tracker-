# [APP_NAME] - Android Expense Tracker

An offline-first, privacy-respecting financial tracker engineered with strict minor-unit monetary precision (`Long`), Room database persistence, and a liquid glass design aesthetic inspired by BeatWave and BitChord.

---

## Key Features

- **Strict Minor-Unit Monetary Precision**: All calculations are performed in minor units (`Long` cents/paise) to guarantee zero floating-point rounding errors across transactions, category totals, and budget limits.
- **BeatWave Liquid Glass Design**:
  - Atmospheric ambient color mesh aura with dynamic pulse animation.
  - Separated floating navigation bar:
    - Floating pill capsule housing destinations (`Home`, `Activity`, `Insights`, `Budgets`).
    - Reactive sliding indicator with spring physics and chromatic aberration / prismatic rainbow lens refraction rims.
    - Tactile touch scaling (`0.88f` scale) on navigation tabs and floating action buttons.
    - Standalone circular glass action button for instant transaction creation.
  - Frosted translucent cards (`GlassCard`) with hairline specular gradient borders and top highlight gloss lines.
  - Glowing circular liquid glass orbs for empty states.
- **Transaction Management**:
  - Hero amount glass input card with currency symbol and large decimal input.
  - Multi-line category selector with glowing reactive selection tiles.
  - Merchant / payee and note liquid glass input cards.
  - Payment method selector (`Card`, `Cash`, `Bank`, `Other`).
  - Date picker dialog with full date formatting.
- **Budgets & Spending Limits**:
  - Overall monthly spending limit with real-time health progress bars and threshold warnings.
  - Category-specific monthly budgets.
- **Insights & Analytics**:
  - Period segmented control with liquid glass styling.
  - Spending by category breakdown with visual indicators.
- **Category Manager**:
  - Built-in and customizable expense/income categories.
  - Liquid glass creation modal with live preview tile, curated color swatches, and icon selection.
  - Safe reassign-and-delete flow preventing orphaned transactions.
- **Crash Diagnostics & Fault Isolation**:
  - Isolated out-of-process crash reporting activity (`:crash` process) with native View UI (immune to Compose crashes).
  - One-tap error log copying and destructive cache clearance.
  - Database schema integrity with fallback destructive migration for clean recovery.
- **Privacy & Offline First**:
  - Zero external tracking, analytics, or remote API dependencies.
  - All financial data is stored locally in SQLite via Room.
  - CSV export for personal spreadsheet backups.

---

## Tech Stack & Architecture

- **Language**: Kotlin 2.0+ (Targeting JDK 21)
- **UI Framework**: Jetpack Compose & Material 3
- **Liquid Glass Effects**: [Haze](https://github.com/chrisbanes/haze) & custom Compose graphics layers, radial gradients, and chromatic brushes
- **Local Storage**: Room (SQLite) with KSP code generation
- **State Management**: Kotlin Coroutines & `StateFlow` within Android `ViewModel`s
- **Navigation**: AndroidX Navigation 3

---

## Getting Started

### Prerequisites
- JDK 21
- Android Studio Ladybug or later (or Android SDK with build tools for API 35)

### Build Debug APK
```bash
./gradlew assembleDebug
```
The compiled APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Run Unit Tests
```bash
./gradlew test
```
