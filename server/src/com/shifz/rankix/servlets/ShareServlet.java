package com.shifz.rankix.servlets;

import com.shifz.rankix.database.Connection;
import com.shifz.rankix.database.tables.SharedData;
import com.shifz.rankix.models.ShareData;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by shifar on 17/12/15."
 */
@WebServlet(urlPatterns = {"/shareServlet"})
public class ShareServlet extends BaseServlet {

    private static final String KEY_SHARE_DATA = "share_data";
    private static final String SHARED_DATA_URL = "shared_data_url";
    private static final String SHARED_DATA_URL_FORMAT = Connection.debugMode
            ? "http://localhost:8080/index.htm?key=%s "
            : "http://theapache64.xyz:8080/rankix/index.htm?key=%s";
    private static final String VALID_NODE_REGEX = "^<p r=\"\\d+(?:\\.\\d)?\".+<\\/p>$";
    private static Random random;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(CONTENT_TYPE_JSON);
        final PrintWriter out = resp.getWriter();

        final String shareData = request.getParameter(KEY_SHARE_DATA);

        if (shareData == null || shareData.trim().isEmpty() || !isValidData(shareData)) {

            out.write(getJSONError("Invalid share data."));

        } else {

            final SharedData sharedDataTable = SharedData.getInstance();

            //Checking if the data is already saved
            final String sharedDataKey = sharedDataTable.get(SharedData.COLUMN_DATA_KEY, SharedData.COLUMN_DATA, shareData);

            if (sharedDataKey != null) {
                //Data already saved
                out.write(getShareSuccessMessage(sharedDataKey));
            } else {
                //New data
                final String shareDataKey = getRandomShareDataKey();

                final boolean isAdded = sharedDataTable.add(shareDataKey, shareData);

                if (isAdded) {
                    out.write(getShareSuccessMessage(shareDataKey));
                } else {
                    out.write(getJSONError("Failed to share data, Try again!"));
                }
            }

        }

        out.flush();
        out.close();
    }

    private static boolean isValidData(String shareData) {
        final String[] nodes = shareData.split("\n");

        if (nodes.length == 0) {
            return false;
        }

        for (final String node : nodes) {
            if (!node.matches(VALID_NODE_REGEX)) {
                System.out.println("Invalid data @ " + node);
                return false;
            }
        }

        return true;
    }

    private static String getShareSuccessMessage(String shareDataKey) {
        final JSONObject jShare = new JSONObject();
        try {
            jShare.put(KEY_ERROR, false);
            jShare.put(SHARED_DATA_URL, String.format(SHARED_DATA_URL_FORMAT, shareDataKey));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jShare.toString();
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
