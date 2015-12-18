package com.shifz.rankix.servlets;

import com.shifz.rankix.database.tables.SharedData;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by shifar on 18/12/15.
 */
@WebServlet(urlPatterns = {"/shared/*"})
public class SharedDataServlet extends BaseServlet {
    private static final int VALID_PATH_INFO_LENGTH = 11;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(CONTENT_TYPE_JSON);
        final PrintWriter out = resp.getWriter();
        String sharedDataKey = req.getPathInfo();

        if (sharedDataKey == null || sharedDataKey.length() != VALID_PATH_INFO_LENGTH) {
            out.write(getJSONError("Invalid data key"));
        } else {

            sharedDataKey = sharedDataKey.substring(1);
            //System.out.println("Original key = " + sharedDataKey);
            final SharedData sharedDataTable = SharedData.getInstance();
            final String sharedData = sharedDataTable.get(SharedData.COLUMN_DATA, SharedData.COLUMN_DATA_KEY, sharedDataKey);

            if (sharedData == null) {
                out.write(getJSONError("Unable to find data for the specified key"));
            } else {
                out.write(getSuccessJSON(sharedData));
            }

        }

        out.flush();
        out.close();

    }

    private static String getSuccessJSON(String sharedData) {
        final JSONObject jSharedData = new JSONObject();
        try {
            jSharedData.put(KEY_ERROR, false);
            jSharedData.put(SharedData.COLUMN_DATA, sharedData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSharedData.toString();
    }
}
