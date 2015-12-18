package com.shifz.rankix.servlets;

import com.shifz.rankix.database.tables.Movies;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;

/**
 * Created by shifar on 17/12/15.
 */
@WebServlet(urlPatterns = {"/getPoster/*"})
public class PosterServlet extends BaseServlet {

    protected static final String REGEX_IMDBID = "/tt\\d{7}.jpg";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

        String imdbId = req.getPathInfo();

        if (imdbId == null || !imdbId.matches(REGEX_IMDBID)) {
            System.out.println("Wrong imdb format");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {

            response.setContentType("image/jpeg");

            imdbId = imdbId.substring(1,10);

            System.out.println("Real imdbId : "+imdbId);

            final Movies moviesTable = Movies.getInstance();
            final String posterUrl = moviesTable.get(Movies.COLUMN_POSTER_URL, Movies.COLUMN_IMDB_ID, imdbId);

            if (posterUrl == null) {
                System.out.println("PosterUrl not found in db");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            ServletOutputStream out;
            out = response.getOutputStream();

            final URL url = new URL(posterUrl);

            BufferedInputStream bin = new BufferedInputStream(url.openStream());
            BufferedOutputStream bout = new BufferedOutputStream(out);
            int ch = 0;
            ;
            while ((ch = bin.read()) != -1) {
                bout.write(ch);
            }

            bin.close();
            bout.close();
            out.close();
        }

    }
}
