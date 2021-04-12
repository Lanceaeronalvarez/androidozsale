package au.com.dealsdirect.data.network.model.returns.step;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Step {
    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("date_type")
    @Expose
    private String dateType;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("action")
    @Expose
    private String action;

    @SerializedName("icon_url")
    @Expose
    private String iconUrl;

    @SerializedName("progress")
    @Expose
    private Float progress;

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getDateType() {
        return dateType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public String getAction() {
        return action;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public float getProgress() {
        return progress == null ? 0 : progress;
    }
}
