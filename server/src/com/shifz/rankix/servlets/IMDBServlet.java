package com.shifz.rankix.servlets;

import com.shifz.rankix.database.Connection;
import com.shifz.rankix.database.tables.Movies;
import com.shifz.rankix.models.Movie;
import com.shifz.rankix.utils.IMDBDotComHelper;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shifar on 12/12/15.
 */
@WebServlet(urlPatterns = {"/imdbServlet"})
public class IMDBServlet extends BaseServlet {

    protected static final String KEY_IMDB_ID = "imdbId";
    protected static final String REGEX_IMDBID = "tt\\d{7}";
    private static final String IMDB_URL_FORMAT = "http://imdb.com/title/%s/";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_RATING = "rating";
    private static final String KEY_PLOT = "plot";
    private static final String KEY_POSTER_URL = "poster_url";
    private static final String POSTER_URL_FORMAT = "http://theapache64.xyz:8080/rankix/posters/%s.jpg";
    private static final String DEBUG_POSTER_URL_FORMAT = "http://localhost:8080/posters/%s.jpg";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        final PrintWriter out = resp.getWriter();

        final String imdbId = req.getParameter(KEY_IMDB_ID);
        System.out.println("imdbId : " + imdbId);
        if (imdbId != null && imdbId.matches(REGEX_IMDBID)) {

            //Checking db
            final Movies movies = Movies.getInstance();
            Movie dbMovie = movies.getMovie(Movies.COLUMN_IMDB_ID, imdbId);

            if (dbMovie != null && dbMovie.isValid()) {
                System.out.println("Rating not expired, so showing from local");
                out.write(getJSONMovieString(dbMovie));
            } else {

                System.out.println("Downloading movie data from...");

                //Download new data
                final URL imdbUrl = new URL(String.format(IMDB_URL_FORMAT, imdbId));
                final HttpURLConnection con = (HttpURLConnection) imdbUrl.openConnection();

                if (con.getResponseCode() == 200) {

                    final BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    final StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line.trim());
                    }

                    br.close();

                    final IMDBDotComHelper imdbHelper = new IMDBDotComHelper(imdbId, sb.toString());

                    if (dbMovie != null) {

                        //Updating rating
                        dbMovie.setRating(imdbHelper.getRating());

                        if (dbMovie.hasMoreDetails()) {

                            System.out.println("Movie has more details, so only updating the rating");

                            //Updating new rating...
                            System.out.println("Updating db...");

                            //Updating rating column only
                            final boolean isUpdated = movies.updateRating(dbMovie.getId(), dbMovie.getRating());

                            if (!isUpdated) {
                                throw new Error("Failed to update the rating");
                            }

                        } else {

                            System.out.println("Movie hasn't more detail so updating more details");

                            final Movie newMovie = imdbHelper.getMovie();

                            dbMovie.setMovieName(newMovie.getMovieName());
                            dbMovie.setGender(newMovie.getGender());
                            dbMovie.setPlot(newMovie.getPlot());
                            dbMovie.setPosterUrl(newMovie.getPosterUrl());

                            final boolean isMoreDetailsAdded = movies.update(dbMovie);
                            if (!isMoreDetailsAdded) {
                                throw new Error("Failed to add more details to the movie");
                            }
                        }

                        //Finally showing result
                        out.write(getJSONMovieString(dbMovie));

                    } else {


                        //New movie
                        final Movie newMovie = imdbHelper.getMovie();
                        final boolean isMovieAdded = movies.add(newMovie);

                        if (!isMovieAdded) {
                            throw new Error("Failed to add new movie");
                        }

                        //Finally showing result
                        out.write(getJSONMovieString(newMovie));
                    }

                } else {
                    out.write(getJSONError("Movie not found"));
                }
            }


        } else {
            out.write(getJSONError("Invalid imdbId"));
        }

        out.flush();
        out.close();

    }

    private static String getJSONMovieString(Movie movie) {
        final JSONObject jMovie = new JSONObject();
        try {
            jMovie.put(KEY_ERROR, false);
            jMovie.put(KEY_NAME, movie.getMovieName());
            jMovie.put(KEY_GENDER, movie.getGender());
            jMovie.put(KEY_RATING, movie.getRating());
            jMovie.put(KEY_PLOT, movie.getPlot());
            jMovie.put(KEY_POSTER_URL, String.format(Connection.debugMode ? DEBUG_POSTER_URL_FORMAT : POSTER_URL_FORMAT, movie.getImdbId()));

            System.out.println("Showing movie : " + movie);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jMovie.toString();
    }


}
