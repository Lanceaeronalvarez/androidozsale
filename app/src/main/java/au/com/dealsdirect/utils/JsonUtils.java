package au.com.dealsdirect.utils;
/*
 * Created by CodeineBot on 5/18/17.
 */

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class JsonUtils {

    static Gson gson = new Gson();

    public static JSONObject convertToJsonObject(Object object) {
        try {
            return new JSONObject(gson.toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static <T> T convertStringToObject(String jsonString, Class<T> clasz) {
        try {
            return gson.fromJson(jsonString, clasz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T convertStringToObject(String jsonString, Type type) {
        try {
            return gson.fromJson(jsonString, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getStringFromLinkedTreeMap(LinkedTreeMap linkedTreeMap, String key) {
        return (linkedTreeMap.get(key) != null && linkedTreeMap.get(key) instanceof String) ?
                (String) linkedTreeMap.get(key) : "";
    }

    public static String getStringDateFromLinkedTreeMap(LinkedTreeMap linkedTreeMap, String key) {

        LinkedTreeMap childTreeMap = (LinkedTreeMap) linkedTreeMap.get(key);

        if (childTreeMap == null) return "";

        String month = linkedTreeMap.get("Month") != null ? (String) linkedTreeMap.get("Month") + " " : "";

        String date = linkedTreeMap.get("Date") != null ? (String) linkedTreeMap.get("Date") + ", " : "";

        String year = linkedTreeMap.get("Year") != null ? (String) linkedTreeMap.get("Year") : "";

        return month + date + year;
    }
}
