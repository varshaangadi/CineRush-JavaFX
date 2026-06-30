package src;

import src.database.MovieDAO;
import src.model.Movie;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        ArrayList<Movie> movies = MovieDAO.getAllMovies();

        for (Movie movie : movies) {

            System.out.println(movie.getTitle());

        }
    }
}