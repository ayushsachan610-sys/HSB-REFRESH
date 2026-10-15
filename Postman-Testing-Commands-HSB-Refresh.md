# Postman API Testing Guide — HSB REFRESH College Fest Management System

This document outlines every API request included in the `hsb-refresh-api.postman_collection.json` test suite for the **HSB REFRESH** project.

---

## 1. Setup Instructions

1. **Start the FastAPI backend server**:
   ```bash
   uvicorn backend.main:app --reload --port 8082
   ```
2. **Open Postman** (free desktop client, works signed out).
3. **Import Collection**: Click **Import** > Select `hsb-refresh-api.postman_collection.json`.
4. **Environment / Variable**: The collection defines a `base_url` variable defaulted to `http://localhost:8082`.

---

## 2. Test Request Suite

### 1. GET /api/events (Filtered by Category & Search)
* **Method:** `GET`
* **URL:** `{{base_url}}/api/events?category=Technical&search=Code`
* **Expected Status:** `200 OK`
* **Expected Response:** An array containing event matching "Code" under "Technical", with live computed `available_seats`.

### 2. GET /api/events (All 14 Seeded Events)
* **Method:** `GET`
* **URL:** `{{base_url}}/api/events`
* **Expected Status:** `200 OK`
* **Expected Response:** JSON array with all 14 official fest events.

### 3. GET /api/events/:event_id (Valid Event ID 1)
* **Method:** `GET`
* **URL:** `{{base_url}}/api/events/1`
* **Expected Status:** `200 OK`
* **Expected Response:**
```json
{
  "id": 1,
  "title": "Code Sprint 2.0",
  "category": "Technical",
  "description": "24-hour hackathon where teams solve real-world problems. Mentors and food provided.",
  "event_date": "2026-11-14",
  "event_time": "10:00 AM",
  "venue": "Main Auditorium",
  "capacity": 100,
  "fee": 0,
  "icon": "💻",
  "available_seats": 100
}
```

### 4. GET /api/events/:event_id (Non-Existent ID 999)
* **Method:** `GET`
* **URL:** `{{base_url}}/api/events/999`
* **Expected Status:** `404 Not Found`
* **Expected Response:**
```json
{
  "detail": "Event with id 999 not found"
}
```

### 5. POST /api/events/:event_id/register (Successful Registration)
* **Method:** `POST`
* **URL:** `{{base_url}}/api/events/1/register`
* **Headers:** `Content-Type: application/json`
* **Body:**
```json
{
  "student_name": "Ayush Sachan",
  "roll_number": "26HSB042",
  "email": "ayush.sachan@college.edu",
  "phone": "9876543210"
}
```
* **Expected Status:** `201 Created`
* **Expected Response:**
```json
{
  "id": 1,
  "event_id": 1,
  "student_name": "Ayush Sachan",
  "registered_at": "2026-10-15T12:00:00.000000"
}
```

### 6. POST /api/events/:event_id/register (Validation: Missing Student Name)
* **Method:** `POST`
* **URL:** `{{base_url}}/api/events/1/register`
* **Headers:** `Content-Type: application/json`
* **Body:**
```json
{
  "student_name": "",
  "roll_number": "26HSB042",
  "email": "ayush.sachan@college.edu"
}
```
* **Expected Status:** `400 Bad Request`
* **Expected Response:**
```json
{
  "detail": "Student name, roll number, and email must not be empty"
}
```

### 7. POST /api/events/:event_id/register (Validation: Invalid Email Format)
* **Method:** `POST`
* **URL:** `{{base_url}}/api/events/1/register`
* **Headers:** `Content-Type: application/json`
* **Body:**
```json
{
  "student_name": "Priya Sharma",
  "roll_number": "26HSB088",
  "email": "invalid-email-without-at"
}
```
* **Expected Status:** `400 Bad Request`
* **Expected Response:**
```json
{
  "detail": "Invalid email: email must contain '@'"
}
```

### 8. POST /api/events/:event_id/register (Duplicate Registration Conflict)
* **Method:** `POST`
* **URL:** `{{base_url}}/api/events/1/register`
* **Headers:** `Content-Type: application/json`
* **Body:** Same email as in Request 5
* **Expected Status:** `409 Conflict`
* **Expected Response:**
```json
{
  "detail": "Already registered"
}
```

### 9. GET /api/registrations (All Records)
* **Method:** `GET`
* **URL:** `{{base_url}}/api/registrations`
* **Expected Status:** `200 OK`
* **Expected Response:** Array of student registration records with embedded `event_title`.

### 10. GET /api/registrations?event_id=1 (Filtered by Event)
* **Method:** `GET`
* **URL:** `{{base_url}}/api/registrations?event_id=1`
* **Expected Status:** `200 OK`
* **Expected Response:** Registrations filtered specifically for Event ID 1.

### 11. GET /api/stats (Live Aggregated Analytics)
* **Method:** `GET`
* **URL:** `{{base_url}}/api/stats`
* **Expected Status:** `200 OK`
* **Expected Response:**
```json
{
  "total_events": 14,
  "total_registrations": 1,
  "total_capacity": 1507,
  "overall_utilization_pct": 0.1,
  "upcoming_events_count": 14,
  "events_by_category": {
    "Cultural": 5,
    "Technical": 4,
    "Sports": 2,
    "Workshop": 2,
    "Gaming": 1
  }
}
```

### 12. POST /api/events (Stretch Goal: Admin Create Event)
* **Method:** `POST`
* **URL:** `{{base_url}}/api/events`
* **Headers:** `Content-Type: application/json`
* **Body:**
```json
{
  "title": "Quantum Computing Keynote",
  "category": "Technical",
  "description": "An inspiring keynote by industry leaders on the frontier of quantum computation.",
  "event_date": "2026-11-15",
  "event_time": "04:30 PM",
  "venue": "Auditorium Hall B",
  "capacity": 80,
  "fee": 0,
  "icon": "⚛️"
}
```
* **Expected Status:** `201 Created`

### 13. DELETE /api/registrations/:registration_id (Stretch Goal: Cancel Registration)
* **Method:** `DELETE`
* **URL:** `{{base_url}}/api/registrations/1`
* **Expected Status:** `200 OK`
* **Expected Response:**
```json
{
  "message": "Registration cancelled successfully",
  "id": 1,
  "event_id": 1,
  "student_name": "Ayush Sachan"
}
```
