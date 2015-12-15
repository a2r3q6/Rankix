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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shifar Shifz on 8/28/2015.
 */
public class Tester {


    public static void main(String[] args) throws IOException {


        //Read data
        final BufferedReader br = new BufferedReader(new FileReader("ironman.data"));
        final StringBuilder treeStringBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            treeStringBuilder.append(line).append("\n");
        }

        br.close();

        final String treeString = treeStringBuilder.toString();

        final IMDBDotComHelper imdbHelper = new IMDBDotComHelper(treeString);

        //Parsing MovieName
        final String movieName = imdbHelper.getMovieName();
        //Parsing gender
        final String gender = imdbHelper.getGender();
        //Parsing Gender
        final String rating = imdbHelper.getRating();
        //Parsing Plot
        final String plot = imdbHelper.getPlot();
        //Parsing PosterUrl
        final String posterUrl = imdbHelper.getPosterUrl(movieName);

        System.out.println("MovieName: " + movieName);
        System.out.println("Gender: " + gender);
        System.out.println("Rating: " + rating);
        System.out.println("Plot: " + plot);
        System.out.println("PosterUrl: " + posterUrl);


    }

}
