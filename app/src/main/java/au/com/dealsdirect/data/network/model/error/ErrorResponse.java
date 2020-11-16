package au.com.dealsdirect.data.network.model.error;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("detail")
    @Expose
    private String detail;

    @SerializedName("instance")
    @Expose
    private String instance;

    public String getTitle() {
        return title;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public String getInstance() {
        return instance;
    }
}
