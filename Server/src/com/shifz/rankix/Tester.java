package com.shifz.rankix;


import com.shifz.rankix.utils.FileAnalyzer;
import com.shifz.rankix.utils.IMDBDotComHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shifar Shifz on 8/28/2015.
 */
public class Tester {

    private static final String KEY_RESULT = "result";
    private static final Pattern RATING_PATTERN = Pattern.compile("^(?<para><p r=\"(?<rating>\\d+(?:\\.\\d)?)\".+<\\/p>)$", Pattern.MULTILINE);
    private static final String KEY_RATING = "rating";
    private static final String KEY_PARA = "para";


    public static void main(String[] args) throws IOException {


        //Read data
        final BufferedReader br = new BufferedReader(new FileReader("sort_test_data"));
        final StringBuilder treeStringBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            treeStringBuilder.append(line).append("\n");
        }

        br.close();

        final String resultHtml = treeStringBuilder.toString();

        final Matcher ratingMatcher = RATING_PATTERN.matcher(resultHtml);
        final String[] nodes = resultHtml.split("\n");

        final List<Result> resultList = new ArrayList<>(nodes.length);

        for (int i = 0; i < nodes.length; i++) {
            if (ratingMatcher.find()) {
                final float rating = Float.parseFloat(ratingMatcher.group(KEY_RATING));
                final String para = ratingMatcher.group(KEY_PARA);
                final Result result = new Result(rating, para);
                resultList.add(result);
            }
        }


        //Sorting by rating
        Collections.sort(resultList, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                return o2.getRating().compareTo(o1.getRating());
            }
        });


        for (final Result m : resultList) {
            System.out.println(m);
        }





        /*final IMDBDotComHelper imdbHelper = new IMDBDotComHelper(treeString);

        //Parsing ResultName
        final String resultName = imdbHelper.getResultName();
        //Parsing gender
        final String gender = imdbHelper.getGender();
        //Parsing Gender
        final String rating = imdbHelper.getRating();
        //Parsing Plot
        final String plot = imdbHelper.getPlot();
        //Parsing PosterUrl
        final String posterUrl = imdbHelper.getPosterUrl(resultName);

        System.out.println("ResultName: " + resultName);
        System.out.println("Gender: " + gender);
        System.out.println("Rating: " + rating);
        System.out.println("Plot: " + plot);
        System.out.println("PosterUrl: " + posterUrl);*/

    }

    public static class Result {
        private final Float rating;
        private final String html;

        public Result(Float rating, String html) {
            this.rating = rating;
            this.html = html;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "rating=" + rating +
                    ", html='" + html + '\'' +
                    '}';
        }

        public Float getRating() {
            return rating;
        }
    }

}
