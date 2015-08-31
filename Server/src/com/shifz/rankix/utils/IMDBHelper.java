package com.shifz.rankix.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shifar Shifz on 8/31/2015 12:04 PM.
 */
public class IMDBHelper {

    private static final String FAKE_USER_AGENT = "ExampleBot 1.0 (+http://example.com/bot)";
    private String movieName;

    private static final String RATING_PATTERN = "Rating:\\s(?<Rating>\\d.\\d)\\/10";
    private static final Pattern ratingPattern = Pattern.compile(RATING_PATTERN);
    private static final String GOOGLE_URL_FORMAT = "http://www.google.com/search?q=%s%%20imdb%%20rating";

    public IMDBHelper(final String movieName) {
        this.movieName = movieName;
    }

    public String getRating() throws IOException {

        URL url = new URL(String.format(GOOGLE_URL_FORMAT, URLEncoder.encode(movieName, "UTF-8")));

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.addRequestProperty("User-Agent",FAKE_USER_AGENT);
        final BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();
        while((line=br.readLine())!=null){
            sb.append(line);
        }

        br.close();

        final String data = sb.toString();

        Matcher ratingMatcher = ratingPattern.matcher(data);
        if(ratingMatcher.find()){
            return ratingMatcher.group("Rating");
        }

        return null;

    }

}
