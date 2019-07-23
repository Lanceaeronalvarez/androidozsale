package au.com.dealsdirect.data.network.model.afterpay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class AfterPayCreatePaymentRequest {
    private final static String KEY_TOKEN = "token";

    @SerializedName("countryID")
    @Expose
    private String countryId;

    @SerializedName("languageID")
    @Expose
    private String languageId;

    @SerializedName("data")
    @Expose
    private HashMap<String, String> data;

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

    public void setToken(String token) {
        if(data == null) {
            data = new HashMap<>();
        }

        data.put(KEY_TOKEN, token);
    }

    public String getToken() {
        if(data == null) {
            return null;
        }

        return data.get(KEY_TOKEN);
    }
}
