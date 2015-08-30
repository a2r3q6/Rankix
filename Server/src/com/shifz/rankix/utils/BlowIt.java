package com.shifz.rankix.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shifar Shifz on 8/28/2015.
 */
public class BlowIt {
    private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_RESULT = "result";

    public static String getJSONError(final String reason){
        final JSONObject jError = new JSONObject();
        try {
            jError.put(KEY_ERROR,true);
            jError.put(KEY_MESSAGE,reason);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jError.toString();
    }

    public static String getSuccessJSON(final JSONObject jResult){
        final JSONObject jResp = new JSONObject();
        try {
            jResp.put(KEY_ERROR,false);
            jResp.put(KEY_RESULT,jResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jResp.toString();
    }

    public static String getSuccessJSON(final String message){
        final JSONObject jResp = new JSONObject();
        try {
            jResp.put(KEY_ERROR,false);
            jResp.put(KEY_MESSAGE,message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jResp.toString();
    }

    public static boolean parseBoolean(String string) {
        return string == null || string.equals("false");
    }
}
