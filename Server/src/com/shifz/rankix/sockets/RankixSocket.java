package com.shifz.rankix.sockets;


import com.shifz.rankix.models.Movie;
import com.shifz.rankix.utils.BlowIt;
import com.shifz.rankix.utils.IMDBHelper;
import org.json.JSONArray;
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

    @OnOpen
    public void onOpen() {
        System.out.println("Connected to RankixSocket");
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
            final IMDBHelper imdbHelper = new IMDBHelper(id, name);
            final Movie movie = imdbHelper.getMovie();

            if (movie != null) {

                final JSONObject jMovieData = new JSONObject();
                jMovieData.put(ERROR, false);
                jMovieData.put(ID, movie.getId());
                jMovieData.put(DATA, movie.getRating());
                jMovieData.put(IMDB_ID, movie.getImdbId());

                client.sendText(jMovieData.toString());

            } else {
                client.sendText(BlowIt.getJSONError("Invalid movie name : " + name));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            try {
                client.sendText(BlowIt.getJSONError("Invalid JSON format for " + name));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                client.sendText(BlowIt.getJSONError("Error while handling " + name));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
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
