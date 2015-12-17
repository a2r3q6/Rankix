package com.shifz.rankix.servlets;

import com.shifz.rankix.database.tables.SharedData;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by shifar on 17/12/15.
 */
@WebServlet(urlPatterns = {"/shareServlet"})
public class ShareServlet extends BaseServlet {

    private static final String KEY_SHARE_DATA = "share_data";
    private static Random random;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(CONTENT_TYPE_JSON);
        final PrintWriter out = resp.getWriter();

        final String shareData = request.getParameter(KEY_SHARE_DATA);

        if (shareData == null || shareData.trim().isEmpty()) {
            out.write(getJSONError("Invalid share data."));
        } else {
            final String shareDataKey = getRandomShareDataKey();
            final SharedData sharedData = SharedData.getInstance();
            final boolean isAdded = sharedData.add(shareDataKey, shareData);
        }

        out.flush();
        out.close();
    }

    private static final String apiEngine = "0123456789AaBbCcDdEeFfGgHhIiJjKkLkMmNnOoPpQqRrSsTtUuVvWwXxYyZ";
    private static final int MAX_SHARE_DATA_KEY_LENGTH = 10;


    /**
     * Return a brand new shareData key
     *
     * @return String shareDataKey
     */
    public static String getRandomShareDataKey() {
        if (random == null) {
            random = new Random();
        }
        final StringBuilder shareKeyBuilder = new StringBuilder(MAX_SHARE_DATA_KEY_LENGTH);
        for (int i = 0; i < MAX_SHARE_DATA_KEY_LENGTH; i++) {
            shareKeyBuilder.append(apiEngine.charAt(random.nextInt(apiEngine.length())));
        }
        return shareKeyBuilder.toString();
    }
}
