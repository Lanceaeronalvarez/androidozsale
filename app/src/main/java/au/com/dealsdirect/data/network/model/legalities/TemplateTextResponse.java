package au.com.dealsdirect.data.network.model.legalities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TemplateTextResponse {
    @SerializedName("type")
    @Expose
    int type;

    @SerializedName("text")
    @Expose
    String text;

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}
