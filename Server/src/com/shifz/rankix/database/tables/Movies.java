package com.shifz.rankix.database.tables;

import com.shifz.rankix.database.Connection;
import com.shifz.rankix.models.Movie;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by shifar on 15/12/15.
 */
public final class Movies {

    private static final Movies instance = new Movies();
    public static final String COLUMN_IMDB_ID = "imdb_id";
    private static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_RATING = "rating";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_PLOT = "plot";
    private static final String COLUMN_POSTER_URL = "poster_url";
    private static final String COLUMN_AS_RATING_UPDATED_BEFORE = "rating_updated_before";
    private final java.sql.Connection con;

    private Movies() {
        this.con = Connection.getConnection();
    }

    public static Movies getInstance() {
        return instance;
    }


    /**
     * Return Movie from the database if the given column matches with the value.
     *
     * @param column column name
     * @param value  value
     * @return Movie
     */
    public Movie getMovie(String column, String value) {

        //TODO: Fix rating_updated_before
        final String query = String.format("SELECT id,name,imdb_id,rating,gender,plot,poster_url, DATEDIFF(edited_at,now()) AS rating_updated_before FROM movies WHERE %s = ? LIMIT 1", column);
        try {
            final PreparedStatement ps = this.con.prepareStatement(query);
            ps.setString(1, value);
            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {

                final String id = rs.getString(COLUMN_ID);
                final String name = rs.getString(COLUMN_NAME);
                final String imdbId = rs.getString(COLUMN_IMDB_ID);
                final String rating = rs.getString(COLUMN_RATING);
                final String gender = rs.getString(COLUMN_GENDER);
                final String plot = rs.getString(COLUMN_PLOT);
                final String posterUrl = rs.getString(COLUMN_POSTER_URL);
                final int ratingUpdatedBefore = rs.getInt(COLUMN_AS_RATING_UPDATED_BEFORE);

                final Movie movie = new Movie(id, name, imdbId, rating, gender, plot, posterUrl, ratingUpdatedBefore);

                rs.close();
                ps.close();
                return movie;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Updates the given column as the given value where the id = given id
     *
     * @param id
     * @param column
     * @param value
     * @return true on success, false on failure.
     */
    public boolean update(String id, String column, String value) {
        final String query = String.format("UPDATE movies SET %s = ? WHERE id = ?", column);
        try {
            final PreparedStatement ps = this.con.prepareStatement(query);
            ps.setString(1, value);
            ps.setString(2, id);
            final boolean isUpdated = ps.executeUpdate() == 1;
            ps.close();
            return isUpdated;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean add(Movie movie) {
        final String query = "INSERT INTO movies (name,imdb_id,gender,rating,plot,poster_url,created_at) VALUES (?,?,?,?,?,?,now());";
        try {
            final PreparedStatement ps = this.con.prepareStatement(query);

            //Setting values
            ps.setString(1, movie.getName());
            ps.setString(2, movie.getImdbId());
            ps.setString(3, movie.getGender());
            ps.setString(4, movie.getRating());
            ps.setString(5, movie.getPlot());
            ps.setString(6, movie.getPosterUrl());

            final boolean isAdded = ps.executeUpdate() == 1;

            ps.close();
            return isAdded;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addBadMovie(String name) {
        final String query = "INSERT INTO movies (name,created_at) VALUES (?,now());";
        try {
            final PreparedStatement ps = this.con.prepareStatement(query);
            ps.setString(1, name);
            final boolean isAdded = ps.executeUpdate() == 1;
            ps.close();
            return isAdded;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
