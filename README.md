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
- **Tummy time** — a dedicated screen (accessible from the navigation drawer) for tracking tummy time:
  - A header card shows the total tummy time logged for the selected day.
  - A big round **Start** button begins a live-ticking stopwatch (mm:ss); a **Stop** button appears below it to end the session, which is then saved with its start time and duration.
  - A timeline below lists every tummy time session for the selected day (long-press to edit or delete).
  - A floating action button lets you log a session manually (start time + duration) for sessions not tracked live from the app.
- **Statistics page** — a dedicated screen (accessible from the navigation drawer) with a dual-axis chart plotting weight (kg) and height (cm) over time, a chart of total daily food intake (ml), and a chart of total daily tummy time (minutes and seconds).
- **Quick stats** — a header card summarizing the selected day at a glance: total food, whether vitamins were given, and poop/pee/puke counts.
- **Fully local** — all data is stored on-device (Room database), no account or internet connection required.
- **Multi-language** — available in English and Romanian, following the device's language automatically; all UI text is externalized to string resources, so more languages are easy to add.
- **Navigation drawer** ("Menu", opened via the hamburger icon) with:
  - **Main app** — the daily timeline described above.
  - **Tummy time** — the tummy time tracker described above.
  - **Statistics** — the growth, food, and tummy time charts described above.
  - **Baby profile** — name, birth date (editable), age shown as years+months, total months, and total days, plus latest weight, latest height, and the last 7 days' average/min/max food intake (today excluded, since it may still be incomplete).
  - **Import / export data** — import a Baby Log JSON file (creates a new baby profile from it) or export any existing baby's data to a JSON file, using the system file picker. Tummy time sessions are included alongside entries, weights, and diaper summaries.
  - **About** — app info and credits.

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- Room for local persistence
- DataStore for lightweight preferences (selected baby profile)
- [Vico](https://github.com/patrykandpatrick/vico) for charts
- No backend — everything runs and stays on the device

## Building the app

### Prerequisites
- JDK 17
- Android SDK with platform 37.1 and build-tools installed (`compileSdk`/`targetSdk` = 37, `minSdk` = 26)
- No system-wide Gradle install is required — the Gradle wrapper (`./gradlew`) downloads Gradle 9.6.1 on first run

`gradle.properties` in this repo is shared across every machine via git, so it deliberately does **not** pin a JDK path — a machine-specific path there breaks the build on any other OS. Point Gradle at your JDK 17 locally instead, using whichever of these fits your workflow:

#### macOS
List installed JDKs:
```bash
/usr/libexec/java_home -V
```
If JDK 17 isn't listed, install one (e.g. `brew install openjdk@17`, or a distro from Adoptium/Oracle). Then either export it in your shell profile (`~/.zshrc`):
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```
or, without touching your shell profile, pin it for Gradle only by adding this line to `~/.gradle/gradle.properties` (per-user, never committed to the repo):
```
org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
```

#### Linux
Install a JDK 17, e.g. on Debian/Ubuntu/Zorin:
```bash
sudo apt install openjdk-17-jdk
```
Then set `JAVA_HOME` (add to `~/.bashrc` or `~/.zshrc`):
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64   # confirm the exact path: update-alternatives --list java
export PATH="$JAVA_HOME/bin:$PATH"
```
If Android Studio is installed, it also bundles its own JDK you can point to instead (e.g. `/opt/android-studio/jbr`), which lets you skip the `apt install` above.

#### Windows
Check for an installed JDK 17:
```powershell
where java
java -version
```
If JDK 17 isn't installed, get one (e.g. from [Adoptium](https://adoptium.net/) or Oracle), or point to the JDK bundled with Android Studio (typically `C:\Program Files\Android\Android Studio\jbr`). Then either set `JAVA_HOME` as a user environment variable (System Properties → Environment Variables, or `setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-17\.jdk\Home"` in a new terminal afterwards), or pin it for Gradle only by adding this line to `%USERPROFILE%\.gradle\gradle.properties` (per-user, never committed to the repo):
```
org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-17.jdk\\Home
```
Use `./gradlew.bat` instead of `./gradlew` for the commands below.

Any OS: verify with `java -version` and `./gradlew -v` before building. Building through Android Studio's own UI instead of a terminal doesn't need any of this — its Gradle JDK is configured separately under **Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**.

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

Configure local properties

```bash
cp local.properties.example local.properties
```

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

To build a **signed release** instead, use:

```bash
./gradlew assembleRelease   # signed release APK
./gradlew bundleRelease     # signed Android App Bundle (.aab), for Play Store upload
```

This requires a `keystore.properties` file with your signing key set up first — see [Signing a release build](#signing-a-release-build) below.

### Signing a release build

Release builds (`assembleRelease` / `bundleRelease`) are only signed if a `keystore.properties` file is present at the project root — without it, the release build type is left unsigned (it still builds, just can't be installed as-is or uploaded to Play). Debug builds are unaffected either way.

**1. Generate an upload keystore** (skip if you already have one):

```bash
keytool -genkey -v -keystore ~/upload-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

Store the resulting `.jks` file **outside the project directory** (e.g. your home folder, as above) and keep it — and its passwords — backed up somewhere durable. If you lose it, you can no longer publish updates to an existing Play Store listing signed with it.

**2. Configure the credentials locally:**

```bash
cp keystore.properties.example keystore.properties
```

Edit `keystore.properties` with your real values:

```properties
storeFile=/Users/you/upload-keystore.jks
storePassword=your-keystore-password
keyAlias=my-key-alias
keyPassword=your-key-password
```

`keystore.properties` (and any `.jks`/`.keystore` file) is gitignored — never commit real signing credentials. This file is per-machine, similar to `local.properties`.

**3. Build a signed release:**

```bash
./gradlew assembleRelease   # signed release APK
./gradlew bundleRelease     # signed Android App Bundle (.aab), for Play Store upload
```

Outputs land at:

```
app/build/outputs/apk/release/app-release.apk
app/build/outputs/bundle/release/app-release.aab
```

You can confirm an APK is properly signed with `apksigner` (bundled with the Android SDK build-tools):

```bash
$ANDROID_HOME/build-tools/<version>/apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
```
