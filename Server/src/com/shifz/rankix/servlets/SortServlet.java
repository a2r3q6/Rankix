package com.shifz.rankix.servlets;

import org.json.JSONException;
import org.json.JSONObject;

import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shifar on 16/12/15.
 */
@WebServlet(urlPatterns = {"/sortServlet"})
public class SortServlet extends BaseServlet {

    private static final String KEY_RESULTS = "results";
    private static final Pattern RATING_PATTERN = Pattern.compile("^(?<para><p r=\"(?<rating>\\d+(?:\\.\\d)?)\".+<\\/p>)$", Pattern.MULTILINE);
    private static final String KEY_RATING = "rating";
    private static final String KEY_PARA = "para";
    private static final String KEY_MESSAGE = "message";


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");

        final PrintWriter out = resp.getWriter();

        final String resultHtml = req.getParameter(KEY_RESULTS);

        if (resultHtml == null || resultHtml.trim().isEmpty()) {
            //System.out.println("Damaged result");
            out.print(getJSONError("Result html is damaged"));
        } else {

            final Matcher ratingMatcher = RATING_PATTERN.matcher(resultHtml);
            final String[] nodes = resultHtml.split("\n");

            final List<Result> resultList = new ArrayList<>(nodes.length);

            while (ratingMatcher.find()) {
                final float rating = Float.parseFloat(ratingMatcher.group(KEY_RATING));
                final String para = ratingMatcher.group(KEY_PARA);
                final Result result = new Result(rating, para);
                resultList.add(result);
            }


            //Sorting by rating
            Collections.sort(resultList, new Comparator<Result>() {
                @Override
                public int compare(Result o1, Result o2) {
                    return o2.getRating().compareTo(o1.getRating());
                }
            });

            //To store sorted html data
            final StringBuilder responseBuilder = new StringBuilder();

            for (final Result m : resultList) {
                responseBuilder.append(m.getHtml()).append("\n");
            }

            final JSONObject jSorted = new JSONObject();

            if (responseBuilder.length() > 0) {

                try {
                    jSorted.put(KEY_ERROR, false);
                    jSorted.put(KEY_RESULTS, responseBuilder.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

                try {
                    jSorted.put(KEY_ERROR, true);
                    jSorted.put(KEY_MESSAGE, "No valid movie found");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            out.write(jSorted.toString());
        }

        out.flush();
        out.close();

    }


    protected static class Result {
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

        public String getHtml() {
            return this.html;
        }
    }
}
