package au.com.dealsdirect.utils;
/*
 * Created by CodeineBot on 5/18/17.
 */

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    public static JSONObject convertToJsonObject(Object object) {
        Gson gson = new Gson();

        try {
            return new JSONObject(gson.toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
}
