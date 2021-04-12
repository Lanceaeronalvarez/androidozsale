package au.com.dealsdirect.data.network.model.returns.message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("type")
    @Expose
    String type;
    @SerializedName("text")
    @Expose
    String text;
}
