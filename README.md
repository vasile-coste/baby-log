# Baby Log

An Android app (Jetpack Compose, Material 3) for logging a baby's day-to-day care — feedings, diapers, vitamins, and growth — entirely on-device.

## Features

- **Multiple baby profiles** — track more than one child, switch between them from the header, each with its own fully separate history.
- **Daily timeline** — every entry logged for the selected day, showing the time, food amount, and icons for poop, pee, puke, and vitamins (only shown when applicable).
- **Quick add** — a floating action button expands into "Add entry" and "Add weight" shortcuts.
  - **Add entry**: time (defaults to now), food amount in ml, and checkboxes for poop, pee, puke, and vitamins. The vitamins checkbox is only offered once per day and hides itself once it's been logged.
  - **Add growth**: weight in kg, height in cm.
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


## Images

|  |  |  |
| --- | --- | --- |
| ![](/images/home01.jpg) | ![](/images/home02.jpg) | ![](/images/home03.jpg) |
| ![](/images/home04.jpg) | ![](/images/home05.jpg) | ![](/images/tummy01.jpg) |
| ![](/images/tummy02.jpg) | ![](/images/stats01.jpg) | ![](/images/stats02.jpg) |
| ![](/images/profile01.jpg) | ![](/images/profile02.jpg) | ![](/images/import01.jpg) |
| ![](/images/import02.jpg) |  |  |
