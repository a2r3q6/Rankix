package com.shifz.rankix.servlets;

import com.shifz.rankix.database.tables.Movies;
import com.shifz.rankix.exceptions.RequestException;
import com.shifz.rankix.models.Movie;
import com.shifz.rankix.utils.IMDBDotComHelper;
import com.shifz.rankix.utils.MovieBuff;
import com.shifz.rankix.utils.Response;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

/**
 * Created by theapache64 on 30/6/17.
 */
@WebServlet(urlPatterns = {"/rankix"})
public class RankixServlet extends AdvancedBaseServlet {

    private static final String KEY_SEARCH = "search";
    private static final String[] REQ_PARAMS = {KEY_SEARCH};

    @Override
    protected String[] getRequiredParameters() {
        return REQ_PARAMS;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doAdvancedPost() throws JSONException, SQLException, RequestException, IOException, ServletException {

        System.out.println("-----------------------");

        //Getting keyword first
        final String name = getStringParameter(KEY_SEARCH);

        System.out.println("Search: " + name);

        final Movies movies = Movies.getInstance();
        final Movie dbMovie = movies.getMovieLike(name);

        System.out.println("DB movie is " + dbMovie);

        if (dbMovie != null && dbMovie.hasMoreDetails() && dbMovie.hasValidRating()) {

            if (dbMovie.getRating() != null) {
                setResponse(dbMovie);
            } else {
                throw new RequestException("Invalid movie name " + name);
            }

        } else {

            final MovieBuff movieBuff = new MovieBuff(null, name);
            final Movie newMovie = movieBuff.getMovie();

            if (newMovie != null) {

                if (dbMovie != null) {
                    //Updating rating
                    final boolean isRatingUpdated = movies.updateRating(dbMovie.getId(), newMovie.getRating());
                    if (!isRatingUpdated) {
                        throw new RequestException("Failed to update rating...");
                    }

                } else {

                    final boolean isMovieAdded = movies.add(newMovie);
                    if (!isMovieAdded) {
                        throw new RequestException("Failed to add new movie " + name);
                    }

                }

                //Getting more details about the movie
                System.out.println("Downloading movie data from...");

                //Download new data
                final URL imdbUrl = new URL(String.format(IMDBServlet.IMDB_URL_FORMAT, newMovie.getImdbId()));
                final HttpURLConnection con = (HttpURLConnection) imdbUrl.openConnection();

                if (con.getResponseCode() == 200) {

                    final BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    final StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line.trim());
                    }

                    br.close();

                    final IMDBDotComHelper imdbHelper = new IMDBDotComHelper(newMovie.getImdbId(), sb.toString());

                    if (dbMovie != null) {

                        //Updating rating
                        dbMovie.setRating(imdbHelper.getRating());

                        if (dbMovie.hasMoreDetails()) {

                            //Updating rating column only
                            final boolean isUpdated = movies.updateRating(dbMovie.getId(), dbMovie.getRating());

                            if (!isUpdated) {
                                throw new Error("Failed to update the rating");
                            }

                        } else {

                            System.out.println("Movie hasn't more detail so updating more details");

                            final Movie newMovie2 = imdbHelper.getMovie();

                            dbMovie.setMovieName(newMovie2.getMovieName());
                            dbMovie.setGender(newMovie2.getGenre());
                            dbMovie.setPlot(newMovie2.getPlot());
                            dbMovie.setPosterUrl(newMovie2.getPosterUrl());

                            final boolean isMoreDetailsAdded = movies.update(dbMovie);
                            if (!isMoreDetailsAdded) {
                                throw new Error("Failed to add more details to the movie");
                            }
                        }

                        setResponse(dbMovie);

                    } else {


                        //New movie
                        final Movie newMovie2 = imdbHelper.getMovie();
                        final boolean isMovieAdded = movies.add(newMovie2);

                        if (!isMovieAdded) {
                            throw new Error("Failed to add new movie");
                        }

                        setResponse(newMovie2);
                    }

                } else {
                    throw new RequestException("Movie not found");
                }

            } else {

                //Invalid movie, adding to history
                final boolean isBadMovieAdded = movies.addBadMovie(name);
                if (!isBadMovieAdded) {
                    throw new RequestException("Failed to add bad movie " + name);
                }

                throw new RequestException("Invalid movie name : " + name);
            }
        }

        System.out.println("-----------------------");

    }

    private void setResponse(Movie movie) throws JSONException {
        final JSONObject joMovie = new JSONObject();
        joMovie.put(Movies.COLUMN_MOVIE_NAME, movie.getMovieName());
        joMovie.put(Movies.COLUMN_RATING, movie.getRating());
        joMovie.put("genre", movie.getGenre());
        joMovie.put(Movies.COLUMN_PLOT, movie.getPlot());
        joMovie.put(Movies.COLUMN_POSTER_URL, movie.getPosterUrl());

        getWriter().write(new Response("Movie found", joMovie).getResponse());
    }
}
