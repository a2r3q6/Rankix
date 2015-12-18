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




    public static void main(String[] args) throws IOException {

        final String fileName = "4.4 # Sacrifice.2011 - Rankix";
        System.out.println(FileAnalyzer.getClearedRandixName(fileName));

    }


}
