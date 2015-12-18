package com.shifz.rankix.sockets;


import com.shifz.rankix.database.tables.Movies;
import com.shifz.rankix.models.Movie;
import com.shifz.rankix.servlets.BaseServlet;
import com.shifz.rankix.utils.MovieBuff;
import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * Created by Shifar Shifz on 8/28/2015.
 */
@ServerEndpoint("/RankixSocket")
public class RankixSocket {

    private static final String ERROR = "error";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DATA = "data";
    private static final String IMDB_ID = "imdb_id";

    private Movies movies;

    @OnOpen
    public void onOpen() {
        //System.out.println("Connected to RankixSocket");
        movies = Movies.getInstance();
    }

    @OnMessage
    public void onMessage(Session session, String movieNameAndId) {

        //Getting client end point
        final RemoteEndpoint.Basic client = session.getBasicRemote();

        //Increasing movie name visibility, so that it can available in catch statements.
        String name = null;

        try {

            final JSONObject jMovie = new JSONObject(movieNameAndId);
            final String id = jMovie.getString(ID);
            name = jMovie.getString(NAME);

            Movie dbMovie = movies.getBasicMovie(Movies.COLUMN_FILE_NAME, name);

            if (dbMovie != null && dbMovie.hasValidRating()) {

                //System.out.println("Movie available in database " + name);
                if (dbMovie.getRating() != null) {
                    //System.out.println("Valid movie and has rating");
                    client.sendText(getJSONMovieData(id, dbMovie));
                } else {
                    //System.out.println("The file name already tried and failed...");
                    client.sendText(BaseServlet.getJSONError("Invalid movie name " + name));
                }

            } else {

                final MovieBuff movieBuff = new MovieBuff(id, name);
                final Movie newMovie = movieBuff.getMovie();

                if (newMovie != null) {

                    if (dbMovie != null) {
                        //Updating rating
                        final boolean isRatingUpdated = movies.updateRating(dbMovie.getId(), newMovie.getRating());
                        if (!isRatingUpdated) {
                            throw new Error("Failed to update rating...");
                        }

                    } else {

                        //Checking if there's any movie with the imdbId
                        final boolean isMovieExistInDB = movies.get(Movies.COLUMN_FILE_NAME, Movies.COLUMN_IMDB_ID, newMovie.getImdbId()) != null;

                        if (isMovieExistInDB) {

                            //Updating rating
                            final boolean isRatingUpdated = movies.update(
                                    Movies.COLUMN_IMDB_ID,
                                    newMovie.getImdbId(),
                                    Movies.COLUMN_RATING,
                                    newMovie.getRating()
                            );

                            if (!isRatingUpdated) {
                                throw new Error("Failed to update rating (SPECIAL CASE)...");
                            }

                        } else {

                            final boolean isMovieAdded = movies.add(newMovie);
                            if (!isMovieAdded) {
                                throw new Error("Failed to add new movie " + name);
                            }
                        }
                    }


                    //Showing result
                    client.sendText(getJSONMovieData(id, newMovie));

                } else {

                    //Invalid movie, adding to history
                    final boolean isBadMovieAdded = movies.addBadMovie(name);
                    if (!isBadMovieAdded) {
                        throw new Error("Failed to add bad movie " + name);
                    }

                    client.sendText(BaseServlet.getJSONError("Invalid movie name : " + name));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            try {
                client.sendText(BaseServlet.getJSONError("Invalid JSON format for " + name));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                client.sendText(BaseServlet.getJSONError("Error while handling " + name));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Returns response JSON string
     *
     * @param id
     * @param movie
     * @return
     */
    private static String getJSONMovieData(String id, Movie movie) {
        final JSONObject jMovieData = new JSONObject();
        try {
            jMovieData.put(ERROR, false);
            jMovieData.put(ID, id);
            jMovieData.put(DATA, movie.getRating());
            jMovieData.put(IMDB_ID, movie.getImdbId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jMovieData.toString();
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
        //System.out.println(e.getMessage());
    }

    @OnClose
    public void onClose() {
        //System.out.println("RankixSocket closed");
    }

}
