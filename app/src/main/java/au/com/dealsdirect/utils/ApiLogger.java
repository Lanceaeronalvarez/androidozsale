package au.com.dealsdirect.utils;

import android.util.Log;

import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ApiLogger implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {
        String logName = "ApiLogger";
        if (message.startsWith("{") || message.startsWith("[")) {
            try {
                String prettyPrintJson = new GsonBuilder().setPrettyPrinting()
                        .create().toJson(JsonParser.parseString(message));
                Log.d(logName, prettyPrintJson);
            } catch (JsonSyntaxException m) {
                Log.d(logName, message);
            }
        } else {
            Log.d(logName, message);
        }
    }
}
