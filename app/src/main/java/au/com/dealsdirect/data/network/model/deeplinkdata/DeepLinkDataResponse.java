package au.com.dealsdirect.data.network.model.deeplinkdata;

import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 5/10/18.
 */

public class DeepLinkDataResponse {

    @SerializedName("meta")
    Meta meta;

    @SerializedName("urlType")
    String urlType;

    @SerializedName("siteCode")
    String siteCode;


    public String getUrlType() {
        return urlType;
    }

    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }
}
