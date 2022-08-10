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

    public String getTitle() {
        return title;
    }

    public List<Option> getOptions() {
        return options;
    }

    public static class Option {
        @SerializedName("preference")
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
