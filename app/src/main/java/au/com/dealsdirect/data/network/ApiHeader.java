package au.com.dealsdirect.data.network;


import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import au.com.dealsdirect.data.pref.PreferencesHelper;



@Singleton
public class ApiHeader {

    private PreferencesHelper preferencesHelper;

    @Inject
    public ApiHeader(String apiKey, PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public Map<String, String> get() {
        Map<String, String> map = new HashMap<>();
        map.put("User-Agent", preferencesHelper.getUserAgent());

        return map;
    }
}
