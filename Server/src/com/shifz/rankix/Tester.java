package com.shifz.rankix;


import com.shifz.rankix.utils.FileAnalyzer;
import com.shifz.rankix.utils.IMDBDotComHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shifar Shifz on 8/28/2015.
 */
public class Tester {


    final static String imageUrl = "http://ia.media-imdb.com/images/M/MV5BMjI5OTYzNjI0Ml5BMl5BanBnXkFtZTcwMzM1NDA1OQ@@._V1_UY268_CR1,0,182,268_AL_.jpg";


    public static void main(String[] args) throws IOException {

        final URL url = new URL(imageUrl);
        final InputStream in = new BufferedInputStream(url.openStream());
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n = 0;

        while ((n = in.read(buffer)) != -1) {
            baos.write(buffer,0,n);
        }
        baos.flush();
        baos.close();
        in.close();

        final byte[] response = baos.toByteArray();
        final FileOutputStream fos = new FileOutputStream("image.jpg");
        fos.write(response);
        fos.flush();
        fos.close();

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


}
