package au.com.dealsdirect.data.network.model.returns.newreturn;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MTC on 2019-08-14.
 */
public class UploadImageResponse {
    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }
}
