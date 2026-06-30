package src.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import src.model.Movie;
import java.sql.PreparedStatement;

public class MovieDAO {

        public static ArrayList<Movie> getAllMovies() {

                ArrayList<Movie> movies = new ArrayList<>();

                try {

                        Connection conn = DBConnection.getConnection();

                        String query = "SELECT * FROM movies";

                        Statement stmt = conn.createStatement();

                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {

                                Movie movie = new Movie(

                                                rs.getInt("id"),
                                                rs.getString("title"),
                                                rs.getString("genre"),
                                                rs.getString("language"),
                                                rs.getString("duration"),
                                                rs.getString("rating"),
                                                rs.getDouble("imdb"),
                                                rs.getString("description"),
                                                rs.getString("poster_url"),
                                                rs.getString("trailer_url"),
                                                rs.getString("release_date"),
                                                rs.getBoolean("new_release"));

                                movies.add(movie);
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                }

                return movies;
        }

        public static void addMovie(Movie movie) {
                try {

                        Connection conn = DBConnection.getConnection();

                        String checkQuery =

                                        "SELECT * FROM movies WHERE title=?";

                        PreparedStatement checkPs = conn.prepareStatement(checkQuery);

                        checkPs.setString(
                                        1,
                                        movie.getTitle());

                        ResultSet rs = checkPs.executeQuery();

                        if (rs.next()) {
                                return;

                        }

                        String query =

                                        "INSERT INTO movies " +

                                                        "(title, genre, language, duration, " +

                                                        "rating, imdb, description, " +

                                                        "poster_url, trailer_url, " +

                                                        "release_date, new_release) " +

                                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                        PreparedStatement ps = conn.prepareStatement(query);

                        ps.setString(1, movie.getTitle());
                        ps.setString(2, movie.getGenre());
                        ps.setString(3, movie.getLanguage());
                        ps.setString(4, movie.getDuration());
                        ps.setString(5, movie.getRating());

                        ps.setDouble(6, movie.getImdb());

                        ps.setString(7,
                                        movie.getDescription());

                        ps.setString(8,
                                        movie.getPosterUrl());

                        ps.setString(9,
                                        movie.getTrailerUrl());

                        ps.setString(10,
                                        movie.getReleaseDate());

                        ps.setBoolean(11,
                                        movie.isNewRelease());

                        ps.executeUpdate();

                        System.out.println(
                                        "Movie Added Successfully!");

                } catch (Exception e) {

                        e.printStackTrace();
                }

        }

        public static void updatePoster(
                        int movieId,
                        String posterUrl) {
                try {

                        Connection conn = DBConnection.getConnection();

                        String query =

                                        "UPDATE movies " +

                                                        "SET poster_url=? " +

                                                        "WHERE id=?";

                        PreparedStatement ps =

                                        conn.prepareStatement(query);

                        ps.setString(1, posterUrl);

                        ps.setInt(2, movieId);

                        ps.executeUpdate();

                } catch (Exception e) {

                        e.printStackTrace();
                }

        }

}