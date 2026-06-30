CREATE DATABASE cinerush_db;
USE cinerush_db;
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(15),
    role VARCHAR(20) DEFAULT 'USER'
);
CREATE TABLE movies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    genre VARCHAR(100),
    language VARCHAR(100),
    duration VARCHAR(20),
    rating VARCHAR(20),
    imdb DOUBLE,
    description TEXT,
    poster_url TEXT,
    trailer_url TEXT,
    release_date DATE,
    new_release BOOLEAN
);

CREATE TABLE showtimes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    movie_id INT,
    show_time VARCHAR(20),
    screen_no INT,
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    movie_id INT,
    showtime_id INT,
    seat_no VARCHAR(10),
    booking_date DATETIME,
    total_amount DOUBLE,
    payment_status VARCHAR(20),
    booking_id VARCHAR(50),

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id)
);

CREATE TABLE reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    movie_id INT,
    rating INT,
    review_text TEXT,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

USE cinerush_db;
INSERT INTO movies (

title,
genre,
language,
duration,
rating,
imdb,
description,
poster_url,
trailer_url,
release_date,
new_release

)

VALUES (

'Interstellar Returns',

'Sci-Fi / Epic',

'English, Hindi',

'2h 49m',

'U',

8.9,

'Beyond the black hole, beyond time — humanitys last hope travels where no one has returned from.',

'file:images/interstellar.jpg',

'https://youtube.com',

'2026-01-01',

true
);
SELECT * FROM movies;
DELETE FROM movies;
SELECT * FROM movies;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE movies;
SET FOREIGN_KEY_CHECKS = 1;

LOAD DATA LOCAL INFILE 'C:/Users/Mahesh Angadi/Downloads/movies.csv'
INTO TABLE movies
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(
title,
genre,
language,
duration,
rating,
imdb,
description,
poster_url,
trailer_url,
release_date,
new_release
);

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE movies;
SET FOREIGN_KEY_CHECKS = 1;
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS showtimes;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS movies;
SET FOREIGN_KEY_CHECKS = 1;

SELECT COUNT(*) FROM movies;