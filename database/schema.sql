-- ==========================================================
-- TrailSafe – Smart Trekking & Tourist Safety System
-- Database Schema for MySQL
-- Chapter 4.1 & 4.3 Database Design / 15 Relational Tables
-- ==========================================================

CREATE DATABASE IF NOT EXISTS trailsafe_db;
USE trailsafe_db;

-- 1. Admin Table
DROP TABLE IF EXISTS SafetyAlert;
DROP TABLE IF EXISTS PaymentSync;
DROP TABLE IF EXISTS Payment;
DROP TABLE IF EXISTS SOSRequest;
DROP TABLE IF EXISTS LocationShare;
DROP TABLE IF EXISTS Trip;
DROP TABLE IF EXISTS Feedback;
DROP TABLE IF EXISTS EmergencyContact;
DROP TABLE IF EXISTS RouteFacility;
DROP TABLE IF EXISTS Checkpoint;
DROP TABLE IF EXISTS WeatherInfo;
DROP TABLE IF EXISTS TrekkingRoute;
DROP TABLE IF EXISTS EmergencyService;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Admin;

CREATE TABLE Admin (
    Admin_ID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(50) NOT NULL,
    Email VARCHAR(50) NOT NULL UNIQUE,
    Password VARCHAR(30) NOT NULL
);

-- 2. User Table
CREATE TABLE User (
    User_ID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(50) NOT NULL,
    Email VARCHAR(50) NOT NULL UNIQUE,
    Phone VARCHAR(15) NOT NULL,
    Password VARCHAR(30) NOT NULL
);

-- 3. Emergency Contact Table
CREATE TABLE EmergencyContact (
    Contact_ID INT AUTO_INCREMENT PRIMARY KEY,
    User_ID INT NOT NULL,
    Name VARCHAR(50) NOT NULL,
    Phone VARCHAR(15) NOT NULL,
    Relationship VARCHAR(20) NOT NULL,
    FOREIGN KEY (User_ID) REFERENCES User(User_ID) ON DELETE CASCADE
);

-- 4. Trekking Route Table
CREATE TABLE TrekkingRoute (
    Route_ID INT AUTO_INCREMENT PRIMARY KEY,
    Route_Name VARCHAR(50) NOT NULL,
    Difficulty VARCHAR(10) NOT NULL, -- Easy / Medium / Hard
    Distance DECIMAL(6,2) NOT NULL  -- in km
);

-- 5. Trip Table
CREATE TABLE Trip (
    Trip_ID INT AUTO_INCREMENT PRIMARY KEY,
    User_ID INT NOT NULL,
    Route_ID INT NOT NULL,
    Start_Date DATE NOT NULL,
    End_Date DATE NOT NULL,
    Status VARCHAR(15) NOT NULL DEFAULT 'Planned', -- Planned / Ongoing / Completed
    FOREIGN KEY (User_ID) REFERENCES User(User_ID) ON DELETE CASCADE,
    FOREIGN KEY (Route_ID) REFERENCES TrekkingRoute(Route_ID) ON DELETE CASCADE
);

-- 6. Checkpoint Table
CREATE TABLE Checkpoint (
    Checkpoint_ID INT AUTO_INCREMENT PRIMARY KEY,
    Route_ID INT NOT NULL,
    Name VARCHAR(50) NOT NULL,
    Latitude DECIMAL(9,6) NOT NULL,
    Longitude DECIMAL(9,6) NOT NULL,
    FOREIGN KEY (Route_ID) REFERENCES TrekkingRoute(Route_ID) ON DELETE CASCADE
);

-- 7. Route Facility Table
CREATE TABLE RouteFacility (
    Facility_ID INT AUTO_INCREMENT PRIMARY KEY,
    Route_ID INT NOT NULL,
    Facility_Name VARCHAR(50) NOT NULL,
    Facility_Type VARCHAR(30) NOT NULL, -- Water point, shelter, medical, etc.
    Location VARCHAR(50) NOT NULL,
    FOREIGN KEY (Route_ID) REFERENCES TrekkingRoute(Route_ID) ON DELETE CASCADE
);

-- 8. Weather Info Table
CREATE TABLE WeatherInfo (
    Weather_ID INT AUTO_INCREMENT PRIMARY KEY,
    Route_ID INT NOT NULL,
    `Condition` VARCHAR(30) NOT NULL,
    Temperature DECIMAL(5,2) NOT NULL,
    Forecast_Time DATE NOT NULL,
    FOREIGN KEY (Route_ID) REFERENCES TrekkingRoute(Route_ID) ON DELETE CASCADE
);

-- 9. Safety Alert Table
CREATE TABLE SafetyAlert (
    Alert_ID INT AUTO_INCREMENT PRIMARY KEY,
    Admin_ID INT NOT NULL,
    Route_ID INT NOT NULL,
    Message VARCHAR(200) NOT NULL,
    Severity VARCHAR(10) NOT NULL, -- Low / Medium / High
    Issued_At DATETIME NOT NULL,
    FOREIGN KEY (Admin_ID) REFERENCES Admin(Admin_ID) ON DELETE CASCADE,
    FOREIGN KEY (Route_ID) REFERENCES TrekkingRoute(Route_ID) ON DELETE CASCADE
);

-- 10. Location Share Table
CREATE TABLE LocationShare (
    Share_ID INT AUTO_INCREMENT PRIMARY KEY,
    Trip_ID INT NOT NULL,
    Latitude DECIMAL(9,6) NOT NULL,
    Longitude DECIMAL(9,6) NOT NULL,
    Shared_At DATETIME NOT NULL,
    Status VARCHAR(10) NOT NULL DEFAULT 'Active', -- Active / Stopped
    FOREIGN KEY (Trip_ID) REFERENCES Trip(Trip_ID) ON DELETE CASCADE
);

-- 11. Payment Table
CREATE TABLE Payment (
    Payment_ID INT AUTO_INCREMENT PRIMARY KEY,
    User_ID INT NOT NULL,
    Trip_ID INT NOT NULL,
    Amount DECIMAL(12,2) NOT NULL,
    Payment_Date DATE NOT NULL,
    Status VARCHAR(10) NOT NULL DEFAULT 'Pending', -- Success / Failed / Pending
    FOREIGN KEY (User_ID) REFERENCES User(User_ID) ON DELETE CASCADE,
    FOREIGN KEY (Trip_ID) REFERENCES Trip(Trip_ID) ON DELETE CASCADE
);

-- 12. Payment Sync Table
CREATE TABLE PaymentSync (
    Sync_ID INT AUTO_INCREMENT PRIMARY KEY,
    Payment_ID INT NOT NULL,
    Sync_Status VARCHAR(15) NOT NULL DEFAULT 'Completed', -- Pending / Completed
    Last_Synced_At DATE NOT NULL,
    FOREIGN KEY (Payment_ID) REFERENCES Payment(Payment_ID) ON DELETE CASCADE
);

-- 13. Emergency Service Table
CREATE TABLE EmergencyService (
    Service_ID INT AUTO_INCREMENT PRIMARY KEY,
    Service_Name VARCHAR(50) NOT NULL,
    Phone VARCHAR(15) NOT NULL,
    Service_Type VARCHAR(20) NOT NULL -- Rescue, medical, police, etc.
);

-- 14. SOS Request Table
CREATE TABLE SOSRequest (
    SOS_ID INT AUTO_INCREMENT PRIMARY KEY,
    User_ID INT NOT NULL,
    Trip_ID INT NOT NULL,
    Service_ID INT NOT NULL,
    Timestamp DATETIME NOT NULL,
    Latitude DECIMAL(9,6) NOT NULL,
    Longitude DECIMAL(9,6) NOT NULL,
    Status VARCHAR(15) NOT NULL DEFAULT 'Sent', -- Sent / Acknowledged / Resolved
    FOREIGN KEY (User_ID) REFERENCES User(User_ID) ON DELETE CASCADE,
    FOREIGN KEY (Trip_ID) REFERENCES Trip(Trip_ID) ON DELETE CASCADE,
    FOREIGN KEY (Service_ID) REFERENCES EmergencyService(Service_ID) ON DELETE CASCADE
);

-- 15. Feedback Table
CREATE TABLE Feedback (
    Feedback_ID INT AUTO_INCREMENT PRIMARY KEY,
    User_ID INT NOT NULL,
    Rating INT NOT NULL CHECK (Rating >= 1 AND Rating <= 5),
    Comments VARCHAR(200) NOT NULL,
    Created_At DATE NOT NULL,
    FOREIGN KEY (User_ID) REFERENCES User(User_ID) ON DELETE CASCADE
);
