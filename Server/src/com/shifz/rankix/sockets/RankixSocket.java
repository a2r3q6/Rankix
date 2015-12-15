package com.shifz.rankix.sockets;


import com.shifz.rankix.database.tables.Movies;
import com.shifz.rankix.models.Movie;
import com.shifz.rankix.servlets.BaseServlet;
import com.shifz.rankix.utils.BlowIt;
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
        System.out.println("Connected to RankixSocket");
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

            Movie movie = movies.getMovie(Movies.COLUMN_NAME, name);

            if (movie != null) {

                System.out.println("Movie available in database " + name);
                if (movie.isValidMovie()) {
                    client.sendText(getJSONMovieData(movie));
                } else {
                    client.sendText(BaseServlet.getJSONError("Invalid movie name " + name));
                }

            } else {

                final MovieBuff movieBuff = new MovieBuff(id, name);
                movie = movieBuff.getMovie();

                if (movie != null) {

                    final boolean isMovieAdded = movies.add(movie);
                    if (!isMovieAdded) {
                        throw new Error("Failed to add new movie " + name);
                    }

                    client.sendText(getJSONMovieData(movie));
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
     * @param movie
     * @return
     */
    private static String getJSONMovieData(Movie movie) {
        final JSONObject jMovieData = new JSONObject();
        try {
            jMovieData.put(ERROR, false);
            jMovieData.put(ID, movie.getId());
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
        System.out.println(e.getMessage());
    }

    @OnClose
    public void onClose() {
        System.out.println("RankixSocket closed");
    }

}
