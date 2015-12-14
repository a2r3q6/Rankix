package com.shifz.rankix.models;

/**
 * Created by shifar on 14/12/15.
 */
public class Movie {

    private final String id, name, gender, plot;
    private String imdbId, rating;

    public Movie(String id, String name, String imdbId, String rating, String gender, String plot) {
        this.id = id;
        this.imdbId = imdbId;
        this.name = name;
        this.rating = rating;
        this.gender = gender;
        this.plot = plot;
    }

    public Movie(String id, String name) {
        this(id, name, null, null, null, null);
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
}
