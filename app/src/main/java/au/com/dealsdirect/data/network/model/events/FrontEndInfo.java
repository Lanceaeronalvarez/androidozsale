package au.com.dealsdirect.data.network.model.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MTC on 3/4/19.
 */

public class FrontEndInfo {
    @SerializedName("frontEnd")
    @Expose
    private String frontEnd;
    @SerializedName("uiVersion")
    @Expose
    private String uiVersion;
    @SerializedName("osVersion")
    @Expose
    private String osVersion;

    public String getFrontEnd() {
        return frontEnd;
    }

    public void setFrontEnd(String frontEnd) {
        this.frontEnd = frontEnd;
    }

    public String getUiVersion() {
        return uiVersion;
    }

    public void setUiVersion(String uiVersion) {
        this.uiVersion = uiVersion;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }
}
