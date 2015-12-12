package com.shifz.rankix.servlets;

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
public class IMDBServlet extends HttpServlet {

    private static final String KEY_IMDB_ID = "imdbId";
    private static final String REGEX_IMDBID = "tt\\d{7}";
    private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGE = "message";
    private static final String IMDB_URL_FORMAT = "http://imdb.com/title/%s/";

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
                    sb.append(line).append("\n");
                }

                out.write(sb.toString());

                br.close();
            } else {
                out.write(getJSONError("Movie not found"));
            }

        } else {
            out.write(getJSONError("Invalid imdbId"));
        }

        out.flush();
        out.close();

    }

    private static String getJSONError(final String errorMessage) {
        final JSONObject jError = new JSONObject();
        try {
            jError.put(KEY_ERROR, true);
            jError.put(KEY_MESSAGE, errorMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jError.toString();
    }


}
