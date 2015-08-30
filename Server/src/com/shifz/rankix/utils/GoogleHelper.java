package com.shifz.rankix.utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shifar Shifz on 8/28/2015.
 */
public class GoogleHelper {

    private static final String KEY_RESPONSE_DATA = "responseData";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_URL = "url";
    private static final String SEARCH_API_URL_FORMAT = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=%s%%20imdb%%20rating";
    private final JSONObject jResponse;
    private String rating;
    private String imdbUrl;
    private static final Pattern ratingPattern = Pattern.compile("(\\d\\.\\d)\\/10");
    private static final Pattern imdbUrlPattern = Pattern.compile("http:\\/\\/www.imdb.com\\/title\\/tt[\\d]+\\/");
    public GoogleHelper(final String response) throws JSONException {
        jResponse = new JSONObject(response);
    }

    public boolean hasRating() {

        try {
            final JSONArray jaResults = jResponse.getJSONObject(KEY_RESPONSE_DATA).getJSONArray(KEY_RESULTS);

            if(jaResults.length()>0){
                final JSONObject firstResult = jaResults.getJSONObject(0);
                final String content = firstResult.getString(KEY_CONTENT);
                final Matcher patternMatcher =  ratingPattern.matcher(content);
                if(patternMatcher.find()){
                    rating = patternMatcher.group(1);
                    return true;
                }else{
                    final String imdbURL = firstResult.getString(KEY_URL);
                    final Matcher imdbUrlMatcher = imdbUrlPattern.matcher(imdbURL);
                    if(imdbUrlMatcher.matches()){
                        imdbUrl = imdbURL;
                    }
                    return false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getRating(){
        if(rating==null){
            throw new IllegalArgumentException("You must call hasRating() and get TRUE inorder tocall getRating()");
        }
        return rating;
    }

    public String getImdbUrl(){
        return imdbUrl;
    }

    public static String getQueryUrl(String query){
        try {
            return String.format(SEARCH_API_URL_FORMAT ,URLEncoder.encode(query,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
