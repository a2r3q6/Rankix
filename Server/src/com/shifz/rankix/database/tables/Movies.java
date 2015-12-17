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
    public static final String COLUMN_MOVIE_NAME = "movie_name";
    public static final String COLUMN_FILE_NAME = "file_name";
    public static final String COLUMN_RATING = "rating";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_PLOT = "plot";
    public static final String COLUMN_POSTER_URL = "poster_url";
    private static final String COLUMN_AS_RATING_UPDATED_BEFORE = "rating_updated_before";


    private Movies() {
    }

    public static Movies getInstance() {
        //TODO: Bug fix - Automatic Connection.getConnection() loss.
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

        final String query = String.format("SELECT id,file_name,movie_name,imdb_id,rating,gender,plot,poster_url, IFNULL(DATEDIFF(now(),updated_at),-1) AS rating_updated_before FROM movies WHERE %s = ? LIMIT 1", column);
        try {
            final PreparedStatement ps = Connection.getConnection().prepareStatement(query);
            ps.setString(1, value);
            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {

                final String id = rs.getString(COLUMN_ID);
                final String fileName = rs.getString(COLUMN_FILE_NAME);
                final String movieName = rs.getString(COLUMN_MOVIE_NAME);
                final String imdbId = rs.getString(COLUMN_IMDB_ID);
                final String rating = rs.getString(COLUMN_RATING);
                final String gender = rs.getString(COLUMN_GENDER);
                final String plot = rs.getString(COLUMN_PLOT);
                final String posterUrl = rs.getString(COLUMN_POSTER_URL);
                final int ratingUpdatedBefore = rs.getInt(COLUMN_AS_RATING_UPDATED_BEFORE);

                final Movie movie = new Movie(id, movieName, fileName, imdbId, rating, gender, plot, posterUrl, ratingUpdatedBefore);

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
    private boolean update(String id, String column, String value) {
        final String query = String.format("UPDATE movies SET %s = ? WHERE id = ?", column);
        try {
            final PreparedStatement ps = Connection.getConnection().prepareStatement(query);
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

    public boolean updateRating(final String movieId, final String newRating) {
        return update(movieId, COLUMN_RATING, newRating);
    }

    public boolean add(Movie movie) {
        final String query = "INSERT INTO movies (movie_name,file_name,imdb_id,gender,rating,plot,poster_url) VALUES (?,?,?,?,?,?,?);";
        try {

            final PreparedStatement ps = Connection.getConnection().prepareStatement(query);

            //Setting values
            ps.setString(1, movie.getMovieName());
            ps.setString(2, movie.getFileName());
            ps.setString(3, movie.getImdbId());
            ps.setString(4, movie.getGender());
            ps.setString(5, movie.getRating());
            ps.setString(6, movie.getPlot());
            ps.setString(7, movie.getPosterUrl());

            final boolean isAdded = ps.executeUpdate() == 1;

            ps.close();
            return isAdded;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addBadMovie(String fileName) {
        final String query = "INSERT INTO movies (file_name) VALUES (?);";
        try {
            final PreparedStatement ps = Connection.getConnection().prepareStatement(query);
            ps.setString(1, fileName);
            final boolean isAdded = ps.executeUpdate() == 1;
            ps.close();
            return isAdded;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Movie updatedMovie) {

        System.out.println("Updating movie by adding more details " + updatedMovie);

        final String query = "UPDATE movies SET movie_name = ? , gender= ? ,rating= ? , plot= ? ,poster_url= ? WHERE id = ?";
        try {
            final PreparedStatement ps = Connection.getConnection().prepareStatement(query);

            //Setting params
            ps.setString(1, updatedMovie.getMovieName());
            ps.setString(2, updatedMovie.getGender());
            ps.setString(3, updatedMovie.getRating());
            ps.setString(4, updatedMovie.getPlot());
            ps.setString(5, updatedMovie.getPosterUrl());
            ps.setString(6, updatedMovie.getId());

            final boolean isUpdated = ps.executeUpdate() == 1;
            ps.close();
            return isUpdated;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Movie getBasicMovie(String column, String value) {
        final String query = String.format("SELECT id,imdb_id,rating, IFNULL(DATEDIFF(now(),updated_at),-1) AS rating_updated_before FROM movies WHERE %s = ? LIMIT 1", column);
        try {
            final PreparedStatement ps = Connection.getConnection().prepareStatement(query);
            ps.setString(1, value);
            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {

                final String id = rs.getString(COLUMN_ID);
                final String imdbId = rs.getString(COLUMN_IMDB_ID);
                final String rating = rs.getString(COLUMN_RATING);
                final int ratingUpdatedBefore = rs.getInt(COLUMN_AS_RATING_UPDATED_BEFORE);

                final Movie movie = new Movie(id, null, null, imdbId, rating, null, null, null, ratingUpdatedBefore);

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

    public String get(String whichColumn, String column, String columnValue) {

        final String query = String.format("SELECT %s FROM movies WHERE %s = ?", whichColumn, column);
        try {
            final PreparedStatement ps = Connection.getConnection().prepareStatement(query);
            ps.setString(1, columnValue);

            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {

                final String data = rs.getString(whichColumn);
                rs.close();
                ps.close();
                return data;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
