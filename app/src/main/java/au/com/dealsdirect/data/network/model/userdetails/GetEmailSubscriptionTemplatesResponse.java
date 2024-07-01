package au.com.dealsdirect.data.network.model.userdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetEmailSubscriptionTemplatesResponse {
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("options")
    @Expose
    private List<Option> options;
    @SerializedName("fetch-property")
    @Expose
    private String fetchProperty;
    @SerializedName("select-all-text")
    @Expose
    private String selectAllText;

    public String getTitle() {
        return title;
    }

    public List<Option> getOptions() {
        return options;
    }
    public String getProperty() {
        return fetchProperty;
    }
    public String getSelectAllText() {
        return selectAllText;
    }

    public static class Option {
        @SerializedName("value")
        @Expose
        private String preference;
        @SerializedName("text")
        @Expose
        private String text;

        public String getPreference() {
            return preference;
        }

        public String getText() {
            return text;
        }
    }
}
