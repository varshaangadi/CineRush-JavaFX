package src.database;

import java.sql.*;
import java.util.ArrayList;

public class BookingDAO {
// SAVE BOOKING
public static void addBooking(
        String movieTitle,
        String showTime,
        String seatNo,
        String username,
        double totalAmount) {

    try {

        Connection conn =
                DBConnection.getConnection();

        String query =
                "INSERT INTO bookings " +
                "(movie_title, show_time, seat_no, username, total_amount) " +
                "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps =
                conn.prepareStatement(query);

        ps.setString(1, movieTitle);
        ps.setString(2, showTime);
        ps.setString(3, seatNo);
        ps.setString(4, username);
        ps.setDouble(5, totalAmount);

        ps.executeUpdate();

        System.out.println("Booking Saved!");

    } catch (Exception e) {

        e.printStackTrace();
    }
}

// GET BOOKED SEATS
public static ArrayList<String> getBookedSeats(
        String movieTitle,
        String showTime) {

    ArrayList<String> seats =
            new ArrayList<>();

    try {

        Connection conn =
                DBConnection.getConnection();

        String query =
                "SELECT seat_no FROM bookings " +
                "WHERE movie_title=? AND show_time=?";

        PreparedStatement ps =
                conn.prepareStatement(query);

        ps.setString(1, movieTitle);
        ps.setString(2, showTime);

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            seats.add(
                    rs.getString("seat_no"));
        }

    } catch (Exception e) {

        e.printStackTrace();
    }

    return seats;
}

// GET USER BOOKINGS
public static ArrayList<String> getBookingsByUser(
        String username) {

    ArrayList<String> bookings =
            new ArrayList<>();

    try {

        Connection conn =
                DBConnection.getConnection();

        String query =
                "SELECT * FROM bookings " +
                "WHERE username=? " +
                "ORDER BY booking_date DESC";

        PreparedStatement ps =
                conn.prepareStatement(query);

        ps.setString(1, username);

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            String booking =

                    "🎬 " +
                    rs.getString("movie_title")

                    + "\n⏰ " +
                    rs.getString("show_time")

                    + "\n💺 Seat: " +
                    rs.getString("seat_no")

                    + "\n💰 ₹" +
                    rs.getDouble("total_amount")

                    + "\n📅 " +
                    rs.getTimestamp("booking_date");

            bookings.add(booking);
        }

    } catch (Exception e) {

        e.printStackTrace();
    }

    return bookings;
}

}
