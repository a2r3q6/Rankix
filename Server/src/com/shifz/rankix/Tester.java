package com.shifz.rankix;


import com.shifz.rankix.utils.FileAnalyzer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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
        final BufferedReader br = new BufferedReader(new FileReader("test_tree.txt"));
        final StringBuilder treeStringBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            treeStringBuilder.append(line).append("\n");
        }

        br.close();

        final String treeString = treeStringBuilder.toString();
        System.out.println(URLEncoder.encode(treeString,"UTF-8"));



    }

}
