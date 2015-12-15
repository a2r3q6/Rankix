package com.shifz.rankix.servlets;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;

/**
 * Created by shifar on 14/12/15.
 */
public class BaseServlet extends HttpServlet {

    protected static final String CONTENT_TYPE_JSON = "application/json";
    protected static final String KEY_ID = "id";
    protected static final String KEY_NAME = "name";

    private static final String KEY_ERROR = "error";
    private static final String KEY_DATA = "data";

    public static String getJSONError(final String reason){
        final JSONObject jError = new JSONObject();
        try {
            jError.put(KEY_ERROR,true);
            jError.put(KEY_DATA,reason);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jError.toString();
    }
}
