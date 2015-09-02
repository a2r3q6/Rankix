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

    private static final String RATING_PATTERN = "Rating:\\s(?<Rating>\\d+(?:\\.\\d)?)\\/10";
    private static final Pattern ratingPattern = Pattern.compile(RATING_PATTERN);
    private static final String GOOGLE_URL_FORMAT = "http://www.google.com/search?q=%s%%20imdb%%20rating";
    private static final String IMDB_URL_FORMAT = "(imdb\\.com\\/title\\/tt\\d{7})";
    private static final Pattern IMDB_PATTERN = Pattern.compile(IMDB_URL_FORMAT);
    private static final String IMDB_RATING_PATTERN_REGEX = "<div class=\"titlePageSprite star-box-giga-star\"> (\\d+(?:\\.\\d)?) <\\/div>";
    private static final Pattern IMDB_RATING_PATTERN = Pattern.compile(IMDB_RATING_PATTERN_REGEX);

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
        }else{
            final Matcher imdbUrlMatcher = IMDB_PATTERN.matcher(data);
            if(imdbUrlMatcher.find()){
                final String imdbUrlString = "http://"+imdbUrlMatcher.group(1);
                System.out.println("IMDB url found for "+this.movieName+" "+imdbUrlString);
                final URL imdbUrl = new URL(imdbUrlString);
                BufferedReader imdbSiteReader = new BufferedReader(new InputStreamReader(imdbUrl.openStream()));
                String imdbSiteLine;
                final StringBuilder imdbSiteData = new StringBuilder();
                while((imdbSiteLine=imdbSiteReader.readLine())!=null){
                    imdbSiteData.append(imdbSiteLine);
                }

                final Matcher imdbRatingMatcher = IMDB_RATING_PATTERN.matcher(imdbSiteData.toString());

                if(imdbRatingMatcher.find()){
                    return imdbRatingMatcher.group(1);
                }else{
                    System.out.println("Rating not found in imdb url");
                }
            }
        }

        System.out.println(String.format("Rating not found for %s , and result is %s",movieName,data));

        return null;

    }

}
