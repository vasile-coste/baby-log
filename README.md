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

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- Room for local persistence
- DataStore for lightweight preferences (selected baby profile)
- [Vico](https://github.com/patrykandpatrick/vico) for charts
- No backend — everything runs and stays on the device
