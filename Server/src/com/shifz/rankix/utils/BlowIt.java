package com.shifz.rankix.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shifar Shifz on 8/28/2015.
 */
public class BlowIt {
    private static final String KEY_ERROR = "error";
    private static final String KEY_RESULT = "result";
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
