package au.com.dealsdirect.data.network.model.attachment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attachment {
    @SerializedName("type")
    @Expose
    String type;
    @SerializedName("title")
    @Expose
    String title;
    @SerializedName("description")
    @Expose
    String description;
    @SerializedName("url")
    @Expose
    String url;
}
