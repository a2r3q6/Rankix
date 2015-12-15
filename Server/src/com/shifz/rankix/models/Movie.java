package com.shifz.rankix.models;

/**
 * Created by shifar on 14/12/15.
 */
public class Movie {

    private static final int EXPIRED = 2;
    private final String id, name, gender, plot;
    private String imdbId, rating;
    private final String posterUrl;
    private final boolean isValidMovie;
    private int ratingUpdatedBefore;

    public Movie(String id, String name, String imdbId, String rating, String gender, String plot, String posterUrl, final int ratingUpdatedBefore) {
        this.id = id;
        this.imdbId = imdbId;
        this.name = name;
        this.rating = rating;
        this.gender = gender;
        this.plot = plot;
        this.posterUrl = posterUrl;
        this.isValidMovie = this.rating != null;
        this.ratingUpdatedBefore = ratingUpdatedBefore;
    }


    public Movie(String id, String name) {
        this(id, name, null, null, null, null, null, EXPIRED);
    }

    public Movie(String name, String imdbId, String rating, String gender, String plot, String posterUrl) {
        this(null, name, imdbId, rating, gender, plot, posterUrl, EXPIRED);
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


    public String getName() {
        return this.name;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getGender() {
        return this.gender;
    }


    public String getPlot() {
        return this.plot;
    }

    public String getPosterUrl() {
        return this.posterUrl;
    }

    public boolean isRatingExpired() {
        return this.ratingUpdatedBefore > 1; //day
    }

    public boolean isValidMovie() {
        return isValidMovie;
    }
}
