package com.shifz.rankix.models;

/**
 * Created by shifar on 14/12/15.
 */
public class Movie {

    private static final int EXPIRED = -1;
    private final String id, fileName;
    private String imdbId, rating, movieName, gender, plot, posterUrl;
    private int ratingUpdatedBefore;
    private static final int MAX_RATING_VALIDITY_IN_DAYS = 5;

    public Movie(String id, String movieName, String fileName, String imdbId, String rating, String gender, String plot, String posterUrl, final int ratingUpdatedBefore) {
        this.id = id;
        this.fileName = fileName;
        this.imdbId = imdbId;
        this.movieName = movieName;
        this.rating = rating;
        this.gender = gender;
        this.plot = plot;
        this.posterUrl = posterUrl;
        this.ratingUpdatedBefore = ratingUpdatedBefore;
    }


    public Movie(String id, String filename) {
        this(id, null, filename, null, null, null, null, null, EXPIRED);
    }

    public Movie(String movieName, String imdbId, String rating, String gender, String plot, String posterUrl) {
        this(null, movieName, null, imdbId, rating, gender, plot, posterUrl, 0);
    }

    public Movie(String name) {
        this(null, name);
    }

    public String getRating() {
        return this.rating;
    }

    public String getImdbId() {
        return this.imdbId;
    }

    public String getId() {
        return id;
    }

    public boolean hasRating() {
        return this.rating != null;
    }


    public String getMovieName() {
        return this.movieName;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return this.gender;
    }


    public String getPlot() {
        return this.plot;
    }

    public String getPosterUrl() {
        return this.posterUrl;
    }

    public boolean isValid() {
        return this.imdbId != null &&
                this.rating != null &&
                this.plot != null &&
                this.movieName != null &&
                this.fileName != null &&
                this.gender != null &&
                //And finally the base
                hasValidRating();
    }


    public boolean hasMoreDetails() {
        return this.plot != null;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public boolean hasValidRating() {
        return this.ratingUpdatedBefore >= 0 && this.ratingUpdatedBefore <= MAX_RATING_VALIDITY_IN_DAYS;
    }

    public int getRatingUpdatedBefore() {
        return this.ratingUpdatedBefore;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", imdbId='" + imdbId + '\'' +
                ", rating='" + rating + '\'' +
                ", movieName='" + movieName + '\'' +
                ", gender='" + gender + '\'' +
                ", plot='" + plot + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", ratingUpdatedBefore=" + ratingUpdatedBefore +
                '}';
    }
}
