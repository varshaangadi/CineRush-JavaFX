DROP TABLE IF EXISTS bookings;

CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    movie_title VARCHAR(255),
    show_time VARCHAR(50),
    seat_no VARCHAR(20),
    username VARCHAR(100),
    total_amount DOUBLE,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
SHOW TABLES;

SELECT * FROM bookings;