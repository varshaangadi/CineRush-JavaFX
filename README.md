# 🎬 CineRush - JavaFX Movie Ticket Booking System
## 📌 Overview

CineRush is a full-stack desktop movie ticket booking application built using JavaFX and MySQL.

The application allows users to browse movies, watch trailers, filter by language, book seats, and manage bookings with persistent database storage.

It also includes an admin panel for importing live movie data using the TMDB API.

## 🚀 Features

### 👤 User Features
- User Signup & Login System
- Browse Movies
- Search Movies
- Filter by Language
- Watch Movie Trailers
- Seat Selection System
- Dynamic Seat Locking
- Booking Confirmation
- Booking Persistence after Restart
- My Bookings History

### 👑 Admin Features
- Admin Login
- Import Latest Movies from TMDB API
- Dynamic Poster Loading
- Real-Time Movie Updates

### 🎨 UI Features
- Netflix/BookMyShow Inspired UI
- Responsive Movie Cards
- Hover Animations
- Smooth Navigation
- Poster-Based Movie Display

## 🛠 Tech Stack

- Java
- JavaFX
- MySQL
- JDBC
- TMDB API
- JSON Library

## 🗄 Database

The project uses MySQL for persistent data storage.

### Tables Used
- users
- movies
- bookings

## 🌐 API Integration

CineRush uses the TMDB (The Movie Database) API to:

- Import latest movies
- Fetch movie posters
- Update movie details dynamically

## ⚙️ Setup Instructions

1. Clone Repository: git clone <your-github-link>
2. Open in VS Code / IntelliJ
3. Add Required Libraries
    - MySQL Connector JAR
    - JSON Library JAR
4. Setup MySQL Database
5. Add TMDB API Key
    - Add your TMDB API key inside:
    -TMDBService.java
6. Run Project

# ✅ FUTURE IMPROVEMENTS

- Online Payment Gateway
- Email Ticket System
- QR Code Tickets
- Recommendation System
- Cloud Deployment

## 👨‍💻 Author

Developed by Varsha Angadi