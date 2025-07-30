package au.com.dealsdirect.data.network.model.userdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class UpdateUserEmailSubscriptionRequest {
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("preference")
    @Expose
    private String preference;
    @SerializedName("categories")
    @Expose
    private HashMap<String, Boolean> categories;
    @SerializedName("receive_sms")
    @Expose
    private boolean receiveSMS;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public HashMap<String, Boolean> getCategories() {
        return categories;
    }

    public void setCategories(HashMap<String, Boolean> categories) {
        this.categories = categories;
    }

    public boolean willReceiveSMS() {
        return receiveSMS;
    }

    public void setWillReceiveSMS(boolean willReceiveSMS) {
        this.receiveSMS = willReceiveSMS;
    }
}

