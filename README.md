# Baby Log

An Android app (Jetpack Compose, Material 3) for logging a baby's day-to-day care — feedings, diapers, vitamins, and growth — entirely on-device.

## Features

- **Multiple baby profiles** — track more than one child, switch between them from the header, each with its own fully separate history.
- **Daily timeline** — every entry logged for the selected day, showing the time, food amount, and icons for poop, pee, puke, and vitamins (only shown when applicable).
- **Quick add** — a floating action button expands into "Add entry" and "Add weight" shortcuts.
  - **Add entry**: time (defaults to now), food amount in ml, and checkboxes for poop, pee, puke, and vitamins. The vitamins checkbox is only offered once per day and hides itself once it's been logged.
  - **Add weight**: weight in kg, with an optional height in cm.
- **Edit and delete** — long-press any timeline entry to edit its details or remove it.
- **Day picker** — tap the day label to jump between past days that have logged data.
- **Growth and food charts** — a dual-axis chart plotting weight (kg) and height (cm) over time, plus a separate chart of total daily food intake (ml).
- **Quick stats** — a header card summarizing the selected day at a glance: total food, whether vitamins were given, and poop/pee/puke counts.
- **Fully local** — all data is stored on-device (Room database), no account or internet connection required.
- **Multi-language** — available in English and Romanian, following the device's language automatically; all UI text is externalized to string resources, so more languages are easy to add.
- **Navigation drawer** (hamburger menu) with:
  - **Main app** — the daily timeline described above.
  - **Baby profile** — name, birth date (editable), age shown as years+months, total months, and total days, plus latest weight, latest height, and the last 7 days' average/min/max food intake (today excluded, since it may still be incomplete).
  - **Import / export data** — import a Baby Log JSON file (creates a new baby profile from it) or export any existing baby's data to a JSON file, using the system file picker.
  - **About** — app info and credits.

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- Room for local persistence
- DataStore for lightweight preferences (selected baby profile)
- [Vico](https://github.com/patrykandpatrick/vico) for charts
- No backend — everything runs and stays on the device

## Building the app

### Prerequisites
- JDK 17 (the project pins `org.gradle.java.home` in `gradle.properties`; edit that path if your JDK 17 lives elsewhere)
- Android SDK with platform 37.1 and build-tools installed (`compileSdk`/`targetSdk` = 37, `minSdk` = 26)
- No system-wide Gradle install is required — the Gradle wrapper (`./gradlew`) downloads Gradle 9.6.1 on first run

### Key dependency versions
| Dependency | Version |
|---|---|
| Android Gradle Plugin | 9.3.0 (built-in Kotlin, no `kotlin-android` plugin) |
| Kotlin | 2.3.21 |
| KSP | 2.3.10 |
| Jetpack Compose BOM | 2026.06.01 |
| Room | 2.8.4 |
| AndroidX DataStore (Preferences) | 1.2.1 |
| Kotlinx Coroutines | 1.11.0 |
| Vico (charts) | 3.2.3 |

Exact coordinates live in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

### Build commands
Run from the project root:

```bash
./gradlew assembleDebug   # compiles and packages a debug APK
./gradlew lint            # Android lint
./gradlew test            # JVM unit tests
```

or full command 

```bash
./gradlew lint test assembleDebug
```

The debug APK is written to:

```
app/build/outputs/apk/debug/app-debug.apk
```

Install it on a connected device or emulator with `adb install -r app/build/outputs/apk/debug/app-debug.apk`.