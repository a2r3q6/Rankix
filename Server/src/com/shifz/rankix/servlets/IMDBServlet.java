package com.shifz.rankix.servlets;

import com.shifz.rankix.utils.BlowIt;
import com.shifz.rankix.utils.IMDBDotComHelper;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shifar on 12/12/15.
 */
@WebServlet(urlPatterns = {"/imdbServlet"})
public class IMDBServlet extends BaseServlet {

    private static final String KEY_IMDB_ID = "imdbId";
    private static final String REGEX_IMDBID = "tt\\d{7}";
    private static final String IMDB_URL_FORMAT = "http://imdb.com/title/%s/";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_RATING = "rating";
    private static final String KEY_PLOT = "plot";
    private static final String KEY_POSTER_URL = "poster_url";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        final PrintWriter out = resp.getWriter();

        final String imdbId = req.getParameter(KEY_IMDB_ID);
        if (imdbId != null && imdbId.matches(REGEX_IMDBID)) {

            final URL imdbUrl = new URL(String.format(IMDB_URL_FORMAT, imdbId));
            final HttpURLConnection con = (HttpURLConnection) imdbUrl.openConnection();

            if (con.getResponseCode() == 200) {

                final BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                final StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line.trim());
                }

                br.close();

                final IMDBDotComHelper imdbHelper = new IMDBDotComHelper(sb.toString());

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

                System.out.println("-----------------------------------------------");
                System.out.println("MovieName: " + movieName);
                System.out.println("Gender: " + gender);
                System.out.println("Rating: " + rating);
                System.out.println("Plot: " + plot);
                System.out.println("PosterUrl: " + posterUrl);
                System.out.println("-----------------------------------------------");

                final JSONObject jMovie = new JSONObject();
                try {
                    jMovie.put(KEY_ERROR, false);
                    jMovie.put(KEY_NAME, movieName);
                    jMovie.put(KEY_GENDER, gender);
                    jMovie.put(KEY_RATING, rating);
                    jMovie.put(KEY_PLOT, plot);
                    jMovie.put(KEY_POSTER_URL, posterUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                out.write(jMovie.toString());

            } else {
                out.write(getJSONError("Movie not found"));
            }

        } else {
            out.write(getJSONError("Invalid imdbId"));
        }

        out.flush();
        out.close();

    }


}
