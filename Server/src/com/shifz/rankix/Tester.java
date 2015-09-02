package com.shifz.rankix;


import com.shifz.rankix.utils.IMDBHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shifar Shifz on 8/28/2015.
 */
public class Tester {

    private static final String SEARCH_API_URL_FORMAT = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=%s%%20imdb%%20rating";

    public static void main(String[] args) throws IOException {
        System.out.println(new IMDBHelper("Ironman 3").getRating());
    }


}
