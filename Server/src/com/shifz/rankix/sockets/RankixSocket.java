package com.shifz.rankix.sockets;


import com.shifz.rankix.utils.BlowIt;
import com.shifz.rankix.utils.GoogleHelper;
import com.shifz.rankix.utils.IMDBHelper;
import com.shifz.rankix.utils.NetworkHelper;
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

    private static final String TYPE = "type";
    private static final String TYPE_PERC = "perc";
    private static final String ERROR = "error";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String RATING = "rating";
    private static final String DATA = "data";
    private static final String RESULT = "result";
    private static final java.lang.String X = RankixSocket.class.getSimpleName() + ": ";
    private static final String TYPE_MSG = "msg";
    private int totalNamesCount;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("RankixSocket: Connected");
    }

    @OnMessage
    public void onMessage(Session session, String movieNames) {

        final RemoteEndpoint.Basic client = session.getBasicRemote();

        try {
            final JSONArray jaMovieNames = new JSONArray(movieNames);
            totalNamesCount = jaMovieNames.length();
            int ratingFoundCount = 0;
            client.sendText(getJSONMessage(totalNamesCount + " Movie name(s) found"));
            client.sendText(getJSONMessage("Initializing..."));
            for (int i = 0; i < totalNamesCount; i++) {
                final JSONObject jMovie = jaMovieNames.getJSONObject(i);
                final int id = jMovie.getInt(ID);
                final String name = jMovie.getString(NAME);
                System.out.println("Movie : "+name);
                final String googleQueryUrl = GoogleHelper.getQueryUrl(name);
                if(googleQueryUrl==null){
                    System.out.println("Google query url is null");
                    continue;
                }
                NetworkHelper networkHelper = new NetworkHelper(googleQueryUrl);
                final String googleResponse = networkHelper.getResponse();
                if(googleResponse!=null){
                    GoogleHelper googleHelper = new GoogleHelper(googleResponse);
                    if (googleHelper.hasRating()) {
                        System.out.println("\tFound rating with google "+googleHelper.getRating());
                        final JSONObject jNew = new JSONObject();
                        jNew.put(ID, id);
                        jNew.put(ERROR,false);
                        jNew.put(DATA, googleHelper.getRating());
                        jNew.put(TYPE,RATING);
                        ratingFoundCount++;
                        client.sendText(jNew.toString());
                    } else if (googleHelper.getImdbUrl() != null) {
                        System.out.println("\tGot imdbUrl :"+googleHelper.getImdbUrl());
                        NetworkHelper imdbNetworkHelper = new NetworkHelper(googleHelper.getImdbUrl());
                        final String imdbResponse = imdbNetworkHelper.getResponse();
                        if(imdbResponse!=null){
                            IMDBHelper imdbHelper = new IMDBHelper(imdbResponse);
                            if (imdbHelper.hasRating()) {
                                System.out.println("\tFound rating with imdb "+imdbHelper.getRating());
                                final JSONObject jNew = new JSONObject();
                                jNew.put(ID, id);
                                jNew.put(ERROR,false);
                                jNew.put(DATA, imdbHelper.getRating());
                                jNew.put(TYPE,RATING);
                                ratingFoundCount++;
                                client.sendText(jNew.toString());
                            }
                        }else{
                            System.out.println("IMDB Response was null");
                        }
                    }else{
                        System.out.println("\tInvalid movie : "+name);
                    }
                }else{
                    System.out.println("Google RESPONSE was null");
                }

                client.sendText(getJSONProgress(i));
            }

            final int totalProgress = (ratingFoundCount * 100 / totalNamesCount);
            client.sendText(getJSONMessage(totalProgress+"% rating found"));
            session.close();

        } catch (JSONException | IOException e) {
            System.out.println("Error:"+e.getMessage());
            e.printStackTrace();
            try {
                client.sendText(BlowIt.getJSONError("NETWORK ERROR OCCURED, Check your connection"));
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
        System.out.println(X+": Closed");
    }

    public String getJSONProgress(int namesAnalyzed) {
        final int perc = (namesAnalyzed+1) * 100 / totalNamesCount;
        final JSONObject jPerc = new JSONObject();
        try {
            jPerc.put(ERROR, false);
            jPerc.put(TYPE, TYPE_PERC);
            jPerc.put(DATA, perc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jPerc.toString();
    }


    public String getJSONMessage(String msg) {
        final JSONObject jPerc = new JSONObject();
        try {
            jPerc.put(ERROR, false);
            jPerc.put(TYPE, TYPE_MSG);
            jPerc.put(DATA, msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jPerc.toString();
    }

}
