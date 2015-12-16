package com.shifz.rankix.utils;

import com.shifz.rankix.models.Movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shifar Shifz on 8/31/2015 12:04 PM.
 */
public class MovieBuff {

    private static final String FAKE_USER_AGENT = "ExampleBot 1.0 (+http://example.com/bot)";

    private static final String KEY_IMDB_ID = "imdbId";
    private static final String KEY_IMDB_URL = "imdbUrl";

    private static final String KEY_RATING = "Rating";

    private static final Pattern GOOGLE_RATING_PATTERN = Pattern.compile("Rating:\\s(?<Rating>\\d+(?:\\.\\d)?)\\/10");
    private static final String GOOGLE_SEARCH_URL_FORMAT = "http://google.com/search?q=%s%%20imdb%%20rating";

    private static final Pattern IMDB_URL_PATTERN = Pattern.compile("(?<imdbUrl>imdb\\.com\\/title\\/(?<imdbId>tt\\d{7}))");
    private static final Pattern IMDB_RATING_PATTERN = Pattern.compile("<div class=\"titlePageSprite star-box-giga-star\"> (\\d+(?:\\.\\d)?) <\\/div>");

    private Movie movie;


    public MovieBuff(final String id, final String movieName) {
        this.movie = new Movie(id, movieName);
    }

    public Movie getMovie() {

        if (this.movie.hasRating()) {
            //Movie has rating
            return this.movie;
        }

        //Un-optimized code - starts
        final String googleUrlFormat;
        try {
            googleUrlFormat = String.format(GOOGLE_SEARCH_URL_FORMAT, URLEncoder.encode(this.movie.getFileName(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        final String googleData = getNetworkResponse(googleUrlFormat, true);

        //Checking if data is downloader
        if (googleData == null) {
            //Failed to downlaod data from google
            return null;
        }

        final Matcher googleRatingMatcher = GOOGLE_RATING_PATTERN.matcher(googleData);

        //Checking if google has the rating
        if (googleRatingMatcher.find()) {

            //Hooooray!! Google has the rating

            //Finding imdb id which'd in the form of tt1234567 for further development.

            //TODO: This is a bad practice, because the google data will be so much big and Regex is not cool to parse html. this must be fixed soon.
            final Matcher imdbIdMatcher = IMDB_URL_PATTERN.matcher(googleData);

            if (imdbIdMatcher.find()) {
                //Saving imdb id in the object
                this.movie.setImdbId(imdbIdMatcher.group(KEY_IMDB_ID));
            }

            //Imdb rating collected through google.
            final String gIMDBRating = googleRatingMatcher.group(KEY_RATING);
            this.movie.setRating(gIMDBRating);

            //returning movie
            return this.movie;

        } else {

            //Google hasn't the IMDB rating. So checking if google has the IMDBUrl.
            System.out.println("GOOGLE failed to find rating for the movie " + this.movie);

            //Finding imdb url from google response.
            final Matcher imdbUrlMatcher = IMDB_URL_PATTERN.matcher(googleData);

            if (imdbUrlMatcher.find()) {

                //Yes, google has the imdb url
                System.out.println("but we've IMDB url, so downloading imdb.com data");

                //Converting url to http instead of www
                final String imdbUrlString = String.format("http://%s", imdbUrlMatcher.group(KEY_IMDB_URL));

                //Setting imdb id for further use
                final String imdbMovieId = imdbUrlMatcher.group(KEY_IMDB_ID);
                this.movie.setImdbId(imdbMovieId);

                //Downloading imdb.com data
                final String imdbDotComData = getNetworkResponse(imdbUrlString, false);

                if (imdbDotComData != null) {

                    //Finding imdb rating from imdb.com
                    final Matcher imdbRatingMatcher = IMDB_RATING_PATTERN.matcher(imdbDotComData);

                    if (imdbRatingMatcher.find()) {

                        //Hooray, we got the rating.
                        final String iIMDBRating = imdbRatingMatcher.group(1);
                        this.movie.setRating(iIMDBRating);

                        return this.movie;
                    }

                    System.out.println("IMDB.com failed to find rating for " + this.movie);
                }
            }
        }

        System.out.println("Failed to find rating for " + this.movie);

        return null;
    }

    /**
     * Used to get live network response of the given url
     *
     * @param urlString A valid url.
     * @return String The response.
     */
    private static String getNetworkResponse(final String urlString, final boolean isFakeUserAgent) {

        try {
            //Creating url object
            URL urlOb = new URL(urlString);

            System.out.println("Url: " + urlString);

            HttpURLConnection con = (HttpURLConnection) urlOb.openConnection();

            //Faking user-agent to mock google.
            if (isFakeUserAgent) {
                con.addRequestProperty("User-Agent", FAKE_USER_AGENT);
            }

            //Downloading response
            final BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            //Closing network resource
            br.close();

            if (sb.length() > 0) {
                return sb.toString();
            }

        } catch (IOException e) {
            //Something went wrong
            e.printStackTrace();
        }

        //The response is empty
        return null;
    }

}
