-- ==========================================================
-- TrailSafe – Smart Trekking & Tourist Safety System
-- Sample Seed Data (Derived from Chapter 4.4.4 Object Diagram)
-- ==========================================================

USE trailsafe_db;

-- 1. Admin
INSERT INTO Admin (Admin_ID, Name, Email, Password) VALUES
(1, 'Admin Officer', 'admin@trailsafe.com', 'admin123');

-- 2. User (user1 from Section 4.4.4)
INSERT INTO User (User_ID, Name, Email, Phone, Password) VALUES
(101, 'Roshan', 'roshan@gmail.com', '9876543210', 'password123'),
(102, 'Priya Sharma', 'priya@example.com', '9822011223', 'pass123'),
(103, 'Aarav Patel', 'aarav@example.com', '9890123456', 'pass123');

-- 3. Emergency Contact (contact1 from Section 4.4.4)
INSERT INTO EmergencyContact (Contact_ID, User_ID, Name, Phone, Relationship) VALUES
(1301, 101, 'Amit', '9876543210', 'Friend'),
(1302, 102, 'Ramesh Sharma', '9822998877', 'Father'),
(1303, 103, 'Neha Patel', '9890998811', 'Sister');

-- 4. Trekking Routes (route1 from Section 4.4.4 + additional prominent routes)
INSERT INTO TrekkingRoute (Route_ID, Route_Name, Difficulty, Distance) VALUES
(301, 'Harishchandragad', 'Medium', 12.50),
(302, 'Rajmachi Fort Trek', 'Easy', 14.00),
(303, 'Kalsubai Peak Trek', 'Hard', 6.60),
(304, 'Sinhagad Fort Trail', 'Easy', 5.20),
(305, 'Sandhan Valley Canyon', 'Hard', 9.80);

-- 5. Checkpoints (cp1 from Section 4.4.4)
INSERT INTO Checkpoint (Checkpoint_ID, Route_ID, Name, Latitude, Longitude) VALUES
-- Harishchandragad Checkpoints
(401, 301, 'Base Camp (Khireshwar)', 19.925000, 73.795000),
(402, 301, 'Tolar Khind Pass', 19.387200, 73.782000),
(403, 301, 'Harishchandreshwar Temple', 19.390000, 73.778000),
(404, 301, 'Konkan Kada Cliff Edge', 19.393000, 73.774000),
-- Rajmachi Checkpoints
(405, 302, 'Udhewadi Base Village', 18.828500, 73.398000),
(406, 302, 'Shrivardhan Peak View', 18.831000, 73.402000),
(407, 302, 'Manaranjan Fort Gate', 18.826000, 73.394000),
-- Kalsubai Checkpoints
(408, 303, 'Bari Village Trailhead', 19.601500, 73.704200),
(409, 303, 'Iron Ladder 1 Checkpoint', 19.601000, 73.709000),
(410, 303, 'Kalsubai Summit Temple (Highest Peak)', 19.600300, 73.714500);

-- 6. Route Facilities (fac1 from Section 4.4.4)
INSERT INTO RouteFacility (Facility_ID, Route_ID, Facility_Name, Facility_Type, Location) VALUES
(801, 301, 'Water Point', 'Water point', 'Km 3'),
(802, 301, 'Forest Rest Shelter', 'Shelter', 'Tolar Khind'),
(803, 301, 'First-Aid Post', 'Medical', 'Base Camp'),
(804, 302, 'Fresh Spring Water Point', 'Water point', 'Km 5 near Plateau'),
(805, 302, 'Village Homestay Shelter', 'Shelter', 'Udhewadi'),
(806, 303, 'Energy Drink & Snack Shed', 'Refreshment', 'Midway Ladder 2'),
(807, 303, 'Emergency Medical Tent', 'Medical', 'Bari Village Base');

-- 7. Weather Info (weather1 from Section 4.4.4)
INSERT INTO WeatherInfo (Weather_ID, Route_ID, `Condition`, Temperature, Forecast_Time) VALUES
(901, 301, 'Cloudy', 22.00, '2026-10-16'),
(902, 302, 'Sunny & Breezy', 26.50, '2026-10-16'),
(903, 303, 'Mist & Strong Winds', 18.50, '2026-10-16'),
(904, 304, 'Clear Skies', 28.00, '2026-10-16'),
(905, 305, 'Humid / Low Sun', 24.00, '2026-10-16');

-- 8. Emergency Services (service1 from Section 4.4.4)
INSERT INTO EmergencyService (Service_ID, Service_Name, Phone, Service_Type) VALUES
(1201, 'Mountain Rescue Team', '112', 'Rescue'),
(1202, 'Forest Police Rapid Response', '100', 'Police'),
(1203, 'Sahyadri Wilderness First Responders', '108', 'Medical'),
(1204, 'State Disaster Response Force (SDRF)', '1077', 'Rescue');

-- 9. Safety Alerts (alert1 from Section 4.4.4)
INSERT INTO SafetyAlert (Alert_ID, Admin_ID, Route_ID, Message, Severity, Issued_At) VALUES
(1001, 1, 301, 'Heavy Rain Alert: Slippery rocks near Tolar Khind. Exercise caution and carry trekking poles.', 'High', '2026-10-15 07:00:00'),
(1002, 1, 303, 'High wind speeds reported at Kalsubai ladder sections. Trek in groups.', 'Medium', '2026-10-16 08:30:00');

-- 10. Trips (trip1 from Section 4.4.4)
INSERT INTO Trip (Trip_ID, User_ID, Route_ID, Start_Date, End_Date, Status) VALUES
(201, 101, 301, '2026-10-15', '2026-10-18', 'Planned'),
(202, 102, 302, '2026-10-20', '2026-10-21', 'Planned');

-- 11. Location Share (loc1 from Section 4.4.4)
INSERT INTO LocationShare (Share_ID, Trip_ID, Latitude, Longitude, Shared_At, Status) VALUES
(701, 201, 19.921000, 73.803000, '2026-10-15 09:30:00', 'Active');

-- 12. Payments (payment1 from Section 4.4.4)
INSERT INTO Payment (Payment_ID, User_ID, Trip_ID, Amount, Payment_Date, Status) VALUES
(501, 101, 201, 500.00, '2026-10-15', 'Success'),
(502, 102, 202, 350.00, '2026-10-16', 'Success');

-- 13. Payment Sync (sync1 from Section 4.4.4)
INSERT INTO PaymentSync (Sync_ID, Payment_ID, Sync_Status, Last_Synced_At) VALUES
(601, 501, 'Completed', '2026-10-15');

-- 14. SOS Requests (sos1 from Section 4.4.4)
INSERT INTO SOSRequest (SOS_ID, User_ID, Trip_ID, Service_ID, Timestamp, Latitude, Longitude, Status) VALUES
(1101, 101, 201, 1201, '2026-10-15 11:45:00', 19.930000, 73.810000, 'Sent');

-- 15. Feedback (feedback1 from Section 4.4.4)
INSERT INTO Feedback (Feedback_ID, User_ID, Rating, Comments, Created_At) VALUES
(1401, 101, 5, 'Great experience! Trail was well marked and emergency features gave immense confidence.', '2026-10-15');
