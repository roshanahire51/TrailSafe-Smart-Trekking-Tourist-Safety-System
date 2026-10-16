# TrailSafe – Smart Trekking & Tourist Safety System

> A computerized application to plan trekking trips safely, browse routes, check weather information and safety alerts, book trips, make online/offline payments with automatic synchronisation, share live location with trusted emergency contacts, and transmit SOS rescue requests.

---

## 🏔️ Tech Stack (Strictly Aligned with Project Documentation)

- **Frontend**:
  - **HTML5, CSS3, JavaScript (ES6+)**
  - **Leaflet.js & OpenStreetMap** for interactive mountain route rendering, checkpoints, facilities, and live trekker GPS tracking
  - **LocalStorage & Offline Queue** for automatic offline payment caching and network recovery synchronization
  - **Responsive Layout & Print Stylesheet** for official booking permits & invoice reports
- **Backend**:
  - **Java (Standard JDK 21)**
  - Built using standard library `com.sun.net.httpserver` with zero external dependencies needed for instant compilation and execution
  - RESTful architecture handling authentication, routes, bookings, offline synchronization, location tracking, SOS dispatch, feedback, and admin oversight
- **Database**:
  - **MySQL Relational Database** (`database/schema.sql` and `database/seed_data.sql`)
  - Implements all **15 relational tables** defined in **Chapter 4.1 & 4.3**:
    1. `Admin`
    2. `User`
    3. `EmergencyContact`
    4. `TrekkingRoute`
    5. `Trip`
    6. `Checkpoint`
    7. `RouteFacility`
    8. `WeatherInfo`
    9. `SafetyAlert`
    10. `LocationShare`
    11. `Payment`
    12. `PaymentSync`
    13. `EmergencyService`
    14. `SOSRequest`
    15. `Feedback`

---

## 🚀 How to Run the Website

### Option 1: Double-click or run `run.bat` (Windows Command Prompt)
```cmd
run.bat
```

### Option 2: Run via PowerShell
```powershell
.\run.ps1
```

### Option 3: Manual Terminal Command
```powershell
# 1. Compile backend classes into bin/
javac -encoding UTF-8 -d bin (Get-ChildItem -Path src/main/java -Filter *.java -Recurse).FullName

# 2. Launch server
java -cp bin com.trailsafe.server.TrailSafeServer 8080
```
Then open your browser to **http://localhost:8080**.

---

## 🔑 Pre-seeded Test Accounts & Sample Data

All sample records are derived directly from the **Object Diagram (Chapter 4.4.4)**:

### 1. Trekker Account
- **Email**: `roshan@gmail.com`
- **Password**: `password123`
- **User ID**: `101`
- **Emergency Contact**: Amit (`1301`, Friend, `9876543210`)

### 2. Administrator Account
- **Email**: `admin@trailsafe.com`
- **Password**: `admin123`
- **Role**: TrailSafe Safety Officer (can broadcast safety alerts, monitor all users, view payments, and manage SOS emergency dispatches)

---

## 💡 Key Features Implemented

1. **Route Catalog & Checkpoint Waypoints**:
   - Explore verified routes: Harishchandragad (301), Rajmachi Fort (302), Kalsubai Peak (303), Sinhagad Valley (304), Sandhan Valley (305).
   - Each route includes elevation checkpoints, route facilities (water stations, shelters, medical points), weather conditions, and active safety advisories.
2. **Online and Offline Payments with Automatic Synchronisation**:
   - Use the **Network Mode** button in the top navigation bar to simulate being deep in remote mountain corridors without network coverage.
   - When offline, payments are saved locally with `Pending` status.
   - Once connection is restored, TrailSafe automatically executes synchronisation with the server, creating `PaymentSync` completed entries.
3. **Live Location Sharing**:
   - Start sharing GPS coordinates with your designated emergency contact.
   - Real-time map pins track the trekker moving along the trail between checkpoints.
4. **One-Click Emergency Rescue SOS**:
   - Urgent SOS button captures high-precision coordinates and alerts emergency rescue forces (Mountain Rescue 112, Police 100, Medical 108).
5. **Admin Monitoring Center**:
   - Live incoming SOS incident triage (Acknowledge / Resolve).
   - Issue and broadcast safety alerts with severity levels (High / Medium / Low).
   - Complete ledger of users, trusted contacts, and payments.
6. **Printable Permits & Receipts (Chapter 5.2)**:
   - View and print official booking permits with safety stamps and transaction references.

---

## 🗄️ MySQL Database Setup (Optional for College Submissions)

If deploying to a dedicated MySQL instance:
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed_data.sql
```
