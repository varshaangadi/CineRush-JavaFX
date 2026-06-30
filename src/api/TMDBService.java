package src.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import src.database.MovieDAO;
import src.model.Movie;

public class TMDBService {
        private static final String API_KEY =

                        "86189f44a5de5bd7e0ae47dbefd9a86c";

        private static final String NOW_PLAYING_URL =

                        "https://api.themoviedb.org/3/movie/now_playing?api_key="

                                        + API_KEY;

        public static void importNowPlayingMovies() {

                try {

                        URL url = new URL(NOW_PLAYING_URL);

                        HttpURLConnection conn =

                                        (HttpURLConnection) url.openConnection();

                        conn.setRequestMethod("GET");

                        BufferedReader reader =

                                        new BufferedReader(

                                                        new InputStreamReader(
                                                                        conn.getInputStream()));

                        StringBuilder response = new StringBuilder();

                        String line;

                        while ((line = reader.readLine()) != null) {

                                response.append(line);
                        }

                        reader.close();

                        JSONObject json =

                                        new JSONObject(
                                                        response.toString());

                        JSONArray results =

                                        json.getJSONArray("results");

                        for (int i = 0; i < results.length(); i++) {

                                JSONObject m = results.getJSONObject(i);

                                String title = m.getString("title");

                                String description = m.getString("overview");

                                double imdb = m.getDouble("vote_average");

                                String releaseDate = m.getString("release_date");

                                String posterPath = m.getString("poster_path");

                                String posterUrl =

                                                "https://image.tmdb.org/t/p/w500"

                                                                + posterPath;

                                Movie movie = new Movie(

                                                0,

                                                title,

                                                "Unknown",

                                                "English",

                                                "2h",

                                                "UA",

                                                imdb,

                                                description,

                                                posterUrl,

                                                "",

                                                releaseDate,

                                                true);

                                MovieDAO.addMovie(movie);
                        }

                        System.out.println(
                                        "TMDB Movies Imported!");

                } catch (Exception e) {

                        e.printStackTrace();
                }
        }

        public static String fetchPosterByTitle(
                        String movieTitle) {

                try {

                        String searchUrl =

                                        "https://api.themoviedb.org/3/search/movie?api_key="

                                                        + API_KEY

                                                        + "&query="

                                                        + movieTitle.replace(" ", "%20");

                        URL url = new URL(searchUrl);

                        HttpURLConnection conn =

                                        (HttpURLConnection) url.openConnection();

                        conn.setRequestMethod("GET");

                        BufferedReader reader =

                                        new BufferedReader(

                                                        new InputStreamReader(
                                                                        conn.getInputStream()));

                        StringBuilder response = new StringBuilder();

                        String line;

                        while ((line = reader.readLine()) != null) {

                                response.append(line);
                        }

                        reader.close();

                        JSONObject json =

                                        new JSONObject(
                                                        response.toString());

                        JSONArray results =

                                        json.getJSONArray("results");

                        if (results.length() > 0) {

                                JSONObject movie = results.getJSONObject(0);

                                String posterPath =

                                                movie.getString(
                                                                "poster_path");

                                return

                                "https://image.tmdb.org/t/p/w500"

                                                + posterPath;
                        }

                } catch (Exception e) {

                        e.printStackTrace();
                }

                return "";

        }

        public static void updateMissingPosters() {
                try {

                        ArrayList<Movie> movies = MovieDAO.getAllMovies();

                        for (Movie movie : movies) {

                                if (movie.getPosterUrl() == null ||

                                                movie.getPosterUrl().isEmpty() ||

                                                movie.getPosterUrl().startsWith("file:")) {

                                        String poster =

                                                        fetchPosterByTitle(
                                                                        movie.getTitle());

                                        if (!poster.isEmpty()) {

                                                MovieDAO.updatePoster(

                                                                movie.getId(),

                                                                poster);

                                                System.out.println(

                                                                "Poster updated for: "

                                                                                + movie.getTitle());
                                        }
                                }
                        }

                } catch (Exception e) {

                        e.printStackTrace();
                }

        }

}
