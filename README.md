# HSB REFRESH — College Fest Management System

**Stack:** Kotlin (Native Android Frontend) · Python + FastAPI (Backend) · SQLite (Database) · Postman (API Suite)  
**Companion to:** PM2-01 (How Software Works) & PM2-02 (Front-End, Back-End & Mobile)  
**App Name:** **HSB REFRESH**

---

## 1. Project Overview

**HSB REFRESH** is a full-stack college fest management system comprising:
1. **Native Android Application (Kotlin)**: Faithfully reproduces the web application's modern dark UI using native Android XML layouts, custom drawables, Material components, Retrofit2, Coroutines, and ViewBinding. No Jetpack Compose.
2. **FastAPI Backend (Python)**: REST API with strict server-side validation, live computed availability calculations, and CRUD endpoints.
3. **SQLite Database (`fest.db`)**: Pre-seeded with 14 official fest events across Technical, Cultural, Sports, Gaming, and Workshop categories.
4. **Postman Test Suite**: Complete collection covering all 2xx, 4xx, and 5xx edge cases.
5. **Web Application (HTML + Tailwind CSS + JS)**: Preserved and served directly from FastAPI at `http://localhost:8082/`.

---

## 2. Project Architecture & Folder Structure

```
hsb-refresh-fest-system/
├── android/                                 # Native Android Studio Kotlin Project
│   ├── app/
│   │   ├── build.gradle.kts                # App build configuration & dependencies
│   │   └── src/main/
│   │       ├── AndroidManifest.xml         # Internet permissions, themes & activities
│   │       ├── java/com/hsbrefresh/fest/
│   │       │   ├── HsbRefreshApplication.kt
│   │       │   ├── data/
│   │       │   │   ├── model/              # Event, Registration, Stats, Request models
│   │       │   │   └── network/            # Retrofit ApiClient, ApiConfig, ApiService
│   │       │   ├── ui/
│   │       │   │   ├── MainActivity.kt     # Navigation coordinator & server settings
│   │       │   │   ├── student/            # Portal, EventAdapter, Registration Sheet
│   │       │   │   ├── admin/              # Dashboard, Table Adapters, Add Event Dialog
│   │       │   │   └── dialogs/            # Runtime Server IP & Port Settings Dialog
│   │       │   └── utils/                  # CategoryHelper, DateHelper
│   │       └── res/
│   │           ├── drawable/               # Custom rounded cards, gradients, badges, icons
│   │           ├── layout/                 # Native XML layouts matching Web UI 1:1
│   │           ├── values/                 # strings.xml, colors.xml, themes.xml, dimens.xml
│   │           └── xml/                    # network_security_config.xml (Cleartext enabled)
│   ├── build.gradle.kts                    # Root build configuration
│   ├── settings.gradle.kts                 # Project name: HsbRefreshFestSystem
│   ├── gradle.properties
│   └── local.properties
├── backend/                                # FastAPI Python Backend
│   ├── database.py                         # SQLite connection & schema initialization
│   ├── models.py                           # Pydantic schemas (Event, Registration, Stats)
│   ├── seed_data.py                        # Seeds 14 events from data/events_seed.xlsx
│   └── main.py                             # REST API endpoints & static file serving
├── data/
│   └── events_seed.xlsx                    # 14 sample fest events
├── frontend/                               # Preserved Web Frontend
│   ├── index.html                          # Student Event Portal
│   ├── admin.html                          # Admin Coordinator Dashboard
│   └── js/
│       ├── app.js                          # Portal logic & fetch calls
│       └── admin.js                        # Admin dashboard logic
├── fest.db                                 # SQLite database file
├── requirements.txt                        # fastapi, uvicorn, pydantic, openpyxl
├── hsb-refresh-api.postman_collection.json # Postman testing collection
├── Postman-Testing-Commands-HSB-Refresh.md # Postman test execution guide
└── README.md                               # Project documentation
```

---

## 3. How to Run the FastAPI Backend

### Prerequisites
* Python 3.10 or higher installed.

### Steps
1. Open terminal inside `hsb-refresh-fest-system/`:
   ```bash
   cd "C:\Users\dksac\Desktop\HSB\TERM-2\PM2\Mobile app\hsb-refresh-fest-system"
   ```
2. Install Python dependencies:
   ```bash
   pip install -r requirements.txt
   ```
3. Initialize and seed the database:
   ```bash
   python -m backend.seed_data
   ```
4. Start the FastAPI server on port 8082:
   ```bash
   uvicorn backend.main:app --reload --host 0.0.0.0 --port 8082
   ```
5. Confirm in your browser:
   * **Student Portal:** `http://localhost:8082/`
   * **Admin Dashboard:** `http://localhost:8082/admin.html`
   * **Interactive Swagger Docs:** `http://localhost:8082/docs`

---

## 4. How to Open and Run the Android Application

### Prerequisites
* **Android Studio** (Koala / Ladybug / Meerkat or later) installed.
* JDK 17 (Java 17 is pre-configured in project build files).

### Opening the Project in Android Studio
1. Launch **Android Studio**.
2. Click **File** > **Open...**
3. Navigate to and select the `android` folder:
   ```
   C:\Users\dksac\Desktop\HSB\TERM-2\PM2\Mobile app\hsb-refresh-fest-system\android
   ```
4. Click **OK**. Android Studio will import the Gradle project, sync dependencies, and index source files.

### Running on Android Studio Emulator
1. Start an Android Virtual Device (AVD) from Android Studio Device Manager (e.g. Pixel 8 running Android 14 / API 34).
2. Click the green **Run 'app'** button (or `Shift + F10`).
3. The app connects to `http://10.0.2.2:8082/` by default (`10.0.2.2` is Android Emulator's loopback address mapped to the host computer).
4. If your server is running on a different port or host, tap the **⚙️ Settings** icon in the top right header to change or test the URL instantly.

### Running on a Physical Android Device
1. Connect your Android phone to the same Wi-Fi network as your development laptop.
2. Find your computer's local Wi-Fi IP address:
   * Windows: Open PowerShell, run `ipconfig`, look for IPv4 Address (e.g., `192.168.1.105` or `172.16.53.144`).
3. Make sure the FastAPI backend was started with `--host 0.0.0.0` (as shown in section 3).
4. Launch the app on your physical device.
5. Tap the **⚙️ Settings** gear icon in the top header.
6. Enter `http://<YOUR_COMPUTER_IP>:8082/` (e.g. `http://192.168.1.105:8082/`).
7. Tap **Test Connection** to confirm connectivity, then tap **Save & Apply**.

---

## 5. Mobile UI & Features Implemented

### Screen 1: Student Event Portal
* **Header:** Sticky navbar with `⚡ HSB REFRESH` brand, `Events Portal` and `Admin Dashboard` switchers, and `Server Settings` gear icon.
* **Hero Section:** Annual Tech & Cultural Extravaganza chip, "Experience the Pulse of HSB REFRESH" gradient heading, date badge (`Nov 14–15, 2026`), venue badge (`Campus Main Grounds`), and official event count badge (`14 Official Events`).
* **Search Bar:** Real-time search with 250ms debounce sending query parameter `?search=` to the FastAPI backend.
* **Category Filter Pills:** Horizontally scrolling pills (`All`, `Technical`, `Cultural`, `Sports`, `Gaming`, `Workshop`) making direct backend requests with `?category=`.
* **Event Cards:**
  * High-contrast emoji visual box.
  * Category badge matching web styling (Cyan for Technical, Fuchsia for Cultural, Amber for Sports, Emerald for Gaming, Violet for Workshop).
  * Entry fee indicator (`Free` in Emerald, `₹Fee` in Amber).
  * Title and 2-line clamped description.
  * Date/Time (`📅`) and Venue (`📍`).
  * Live seats left badge: `FULL` (Rose solid), `<10 seats left` (Amber border), or `X/Y seats` (Slate).
  * `View Details` button with violet styling.
* **Pull-to-Refresh:** SwipeRefreshLayout allows instant pull-to-refresh on mobile.
* **States:** Polished animated loading indicator, empty search state with "Reset Filters" button, and friendly network error state with "Retry Loading".

### Screen 2: Event Details & Registration Modal
* Presented as an interactive `BottomSheetDialogFragment`.
* Displays full event metadata, fee, date/time, venue, and live seat count.
* **Registration Form:**
  * Student Full Name (Required)
  * Roll Number (Required)
  * Phone Number (Optional)
  * College Email (Required)
* **Validation & Error Handling:**
  * Client-side check for required fields and valid email containing `@`.
  * Server-side validation parsing: Displays actual error details returned by FastAPI (`Event is full`, `Already registered`, `Invalid email`).
  * Disables submit button with "Submitting registration..." state during API round-trip.
  * On success (201): Displays green success banner with Registration ID and automatically updates available seat count on the portal and modal.

### Screen 3: Admin Coordinator Dashboard
* **Header & Controls:** Live Operations Dashboard heading, real-time "Updated HH:mm:ss" indicator, Refresh button, and `+ Add Event` button.
* **Top 4 Stat Cards:**
  1. `Total Events` (14)
  2. `Total Registrations` (live count)
  3. `Overall Utilization` (`%` with total occupied / total capacity)
  4. `Upcoming Events`
* **Category Breakdown:** Horizontally wrapping pills showing event counts per category with violet count badges.
* **Navigation Tabs:**
  * **Tab 1 — Events Capacity & Demand:**
    * Automatically sorted by **utilization percentage in descending order**.
    * Events with $\ge 90\%$ capacity feature a **rose warning highlight card** and `Near Capacity` / `FULL` badge.
    * Shows title, category badge, schedule, venue, registered / capacity count, utilization %, and status badge.
  * **Tab 2 — Student Registration Records:**
    * Filter dropdown: filters registrations by event dynamically.
    * Record counter: "Showing X record(s)".
    * Registration cards: ID, Student Name, Roll Number, Email, Phone, Event title badge, formatted timestamp, and a `Cancel` registration button.
* **Add Event Dialog (Stretch Goal):**
  * Dialog to create new events with Title, Emoji, Category, Venue, Date, Time, Capacity, Fee, and Description.
  * Enforces full server-side validation.

---

## 6. Testing with Postman

1. Open Postman.
2. Import `hsb-refresh-api.postman_collection.json`.
3. Follow the 13 requests documented in `Postman-Testing-Commands-HSB-Refresh.md`:
   * Test 1: Category filter & title search (`GET /api/events?category=Technical&search=Code`)
   * Test 2: List all events (`GET /api/events`)
   * Test 3: Get single event detail (`GET /api/events/1`)
   * Test 4: 404 Not Found handling (`GET /api/events/999`)
   * Test 5: Successful registration (`POST /api/events/1/register` -> 201 Created)
   * Test 6: Missing required field validation (`POST /api/events/1/register` with empty name -> 400 Bad Request)
   * Test 7: Invalid email validation (`POST /api/events/1/register` without `@` -> 400 Bad Request)
   * Test 8: Duplicate registration (`POST /api/events/1/register` with same email -> 409 Conflict)
   * Test 9: List all registrations (`GET /api/registrations`)
   * Test 10: Filter registrations by event (`GET /api/registrations?event_id=1`)
   * Test 11: Live dashboard stats (`GET /api/stats`)
   * Test 12: Admin add event (`POST /api/events` -> 201 Created)
   * Test 13: Admin cancel registration (`DELETE /api/registrations/1` -> 200 OK)
