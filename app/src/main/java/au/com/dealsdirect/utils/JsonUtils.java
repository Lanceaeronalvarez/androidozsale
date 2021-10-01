package au.com.dealsdirect.utils;
/*
 * Created by CodeineBot on 5/18/17.
 */

import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static JSONObject convertToJsonObject(Object object) {
        return convertToJsonObject(object, false);
    }

    public static JSONObject convertToJsonObject(Object object, boolean willSerializeNulls) {
        return convertToJsonObject(object, willSerializeNulls, new ArrayList<>());
    }

    public static JSONObject convertToJsonObject(Object object, List<Pair<Class, Object>> adapters) {
        return convertToJsonObject(object, false, adapters);
    }

    public static JSONObject convertToJsonObject(Object object, Pair<Class, Object> adapter) {
        List<Pair<Class, Object>> adapters = new ArrayList<Pair<Class, Object>>() {{
            add(adapter);
        }};
        return convertToJsonObject(object, false, adapters);
    }

    public static JSONObject convertToJsonObject(Object object, boolean willSerializeNulls, Pair<Class, Object> adapter) {
        List<Pair<Class, Object>> adapters = new ArrayList<Pair<Class, Object>>() {{
            add(adapter);
        }};
        return convertToJsonObject(object, willSerializeNulls, adapters);
    }

    public static JSONObject convertToJsonObject(Object object, boolean willSerializeNulls, List<Pair<Class, Object>> adapters) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (willSerializeNulls) {
            gsonBuilder = gsonBuilder.serializeNulls();
        }

        if (adapters != null) {
            for (Pair<Class, Object> classTypeAdapterPair : adapters) {
                gsonBuilder.registerTypeAdapter(classTypeAdapterPair.first, classTypeAdapterPair.second);
            }
        }

        Gson gson = gsonBuilder.create();
        try {
            return new JSONObject(gson.toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static JSONArray convertToJsonArray(List array) {
        return convertToJsonArray(array, false);
    }

    public static JSONArray convertToJsonArray(List array, boolean willSerializeNulls) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (willSerializeNulls) {
            gsonBuilder = gsonBuilder.serializeNulls();
        }
        Gson gson = gsonBuilder.create();
        try {
            return new JSONArray(gson.toJson(array));
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static <T> T convertStringToObject(String jsonString, Class<T> clasz) {
        return convertStringToObject(jsonString, clasz, false);
    }

    public static <T> T convertStringToObject(String jsonString, Class<T> clasz, boolean willSerializeNulls) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (willSerializeNulls) {
            gsonBuilder = gsonBuilder.serializeNulls();
        }
        Gson gson = gsonBuilder.create();
        try {
            return gson.fromJson(jsonString, clasz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T convertStringToObject(String jsonString, Type type) {
        return convertStringToObject(jsonString, type, false);
    }

    public static <T> T convertStringToObject(String jsonString, Type type, boolean willSerializeNulls) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (willSerializeNulls) {
            gsonBuilder = gsonBuilder.serializeNulls();
        }
        Gson gson = gsonBuilder.create();
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
