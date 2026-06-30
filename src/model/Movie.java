package src.model;

public class Movie {

    private int id;
    private String title;
    private String genre;
    private String language;
    private String duration;
    private String rating;
    private double imdb;
    private String description;
    private String posterUrl;
    private String trailerUrl;
    private String releaseDate;
    private boolean newRelease;

    // Constructor
    public Movie(
            int id,
            String title,
            String genre,
            String language,
            String duration,
            String rating,
            double imdb,
            String description,
            String posterUrl,
            String trailerUrl,
            String releaseDate,
            boolean newRelease) {

        this.id = id;
        this.title = title;
        this.genre = genre;
        this.language = language;
        this.duration = duration;
        this.rating = rating;
        this.imdb = imdb;
        this.description = description;
        this.posterUrl = posterUrl;
        this.trailerUrl = trailerUrl;
        this.releaseDate = releaseDate;
        this.newRelease = newRelease;
    }

    // Getters

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getLanguage() {
        return language;
    }

    public String getDuration() {
        return duration;
    }

    public String getRating() {
        return rating;
    }

    public double getImdb() {
        return imdb;
    }

    public String getDescription() {
        return description;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }
    public String getReleaseDate() {
return releaseDate;

}

public boolean isNewRelease() {
return newRelease;

}

}