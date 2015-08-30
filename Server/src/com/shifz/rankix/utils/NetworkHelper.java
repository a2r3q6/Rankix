package com.shifz.rankix.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Shifar Shifz on 8/28/2015.
 */
public class NetworkHelper {

    public final String url;

    public NetworkHelper(final String url){
        this.url = url;
    }


    public String getResponse() {
        try {
            final URL netUrl = new URL(url);
            final BufferedReader br = new BufferedReader(new InputStreamReader(netUrl.openStream()));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while((line=br.readLine())!=null){
                responseBuilder.append(line);
            }
            br.close();
            return responseBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
