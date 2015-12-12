package com.shifz.rankix.servlets;

import com.shifz.rankix.utils.BlowIt;
import com.shifz.rankix.utils.FileAnalyzer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shifar Shifz on 11/5/2015.
 */

@WebServlet(urlPatterns = {"/Tree"})
public class TreeServlet extends HttpServlet {

    private static final String KEY_TREE = "tree";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    private static final Pattern TREE_FILE_PATTERN = Pattern.compile("(?:(?:\\|\\s{3})|(?:\\+\\-{3})).+");

    private static final Pattern VIDEO_FILE_NAME_PATTERN = Pattern.compile(
            "(?:^[\\|\\s]+)(.+)\\.(?:webm|mkv|flv|vob|ogv|ogg|drc|mng|avi|mov|qt|wmv|rm|rmvb|asf|mp4|m4p|m4v|mpg|mp2|mpeg|mpe|mpv|mpg|mpeg|m2v|m4v|svi|3gp|3g2|mxf|roq|nsv)+",
            Pattern.MULTILINE
    );

    private static final String KEY_ERROR = "error";
    private static final String KEY_TOTAL_ELEMENTS_FOUND = "total_elements_found";
    private static final String KEY_RANKIXED_FILE_COUNT = "rankixed_file_count";
    private static final String KEY_MOVIE_FILE_COUNT = "movie_file_count";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_IGNORED_ELEMENT_COUNT = "ignored_element_count";


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");

        final PrintWriter out = resp.getWriter();

        //Retrieving post data
        String treeString = req.getParameter(KEY_TREE);

        if (treeString != null) {

            //Has data

            treeString = URLDecoder.decode(treeString, "UTF-8");

            if (TREE_FILE_PATTERN.matcher(treeString).find()) {

                treeString = treeString.toLowerCase();

                //Collecting video files from treeString
                final Matcher videoFileNameMatcher = VIDEO_FILE_NAME_PATTERN.matcher(treeString);

                if (videoFileNameMatcher.find()) {

                    int id = 0;
                    int rankixedFileCount = 0;
                    final int totalFileCount = treeString.split("\n").length;
                    final JSONArray jResults = new JSONArray();

                    do {

                        String videoFileName = videoFileNameMatcher.group(1);

                        if (FileAnalyzer.isRankixed(videoFileName)) {
                            rankixedFileCount++;
                            continue;
                        }

                        videoFileName = FileAnalyzer.getMovieNameFromFileName(videoFileName);
                        try {
                            final JSONObject jFilmNode = new JSONObject();
                            jFilmNode.put(KEY_ID, id++);
                            jFilmNode.put(KEY_NAME, videoFileName);
                            jResults.put(jFilmNode);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } while (videoFileNameMatcher.find());

                    final JSONObject jResponse = new JSONObject();
                    try {
                        jResponse.put(KEY_ERROR, false);
                        jResponse.put(KEY_TOTAL_ELEMENTS_FOUND, totalFileCount);
                        jResponse.put(KEY_RANKIXED_FILE_COUNT, rankixedFileCount);
                        jResponse.put(KEY_IGNORED_ELEMENT_COUNT, (totalFileCount - rankixedFileCount) - jResults.length());
                        jResponse.put(KEY_MOVIE_FILE_COUNT, jResults.length());
                        jResponse.put(KEY_RESULTS, jResults);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    out.write(jResponse.toString());

                } else {
                    out.write(BlowIt.getJSONError("No movie file found"));
                }


            } else {
                out.write(BlowIt.getJSONError("Invalid TREE Format"));
            }

        } else {
            //Parameter missing
            out.write(BlowIt.getJSONError("Missing tree parameter"));
        }

        out.flush();
        out.close();

    }
}
