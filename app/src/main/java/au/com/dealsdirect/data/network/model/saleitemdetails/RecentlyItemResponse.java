package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MTC on 2019-12-27.
 */
public class RecentlyItemResponse {
    @Expose
    @SerializedName("id")
    public String id;
    @Expose
    @SerializedName("masterProductId")
    public String masterProductId;
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("images")
    public List<String> images;
    @Expose
    @SerializedName("seoIdentifier")
    public String seoIdentifier;
    @Expose
    @SerializedName("isUnavailable")
    public boolean isUnavailable;
    @Expose
    @SerializedName("isSoldOut")
    public boolean isSoldOut;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMasterProductId() {
        return masterProductId;
    }

    public void setMasterProductId(String masterProductId) {
        this.masterProductId = masterProductId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getSeoIdentifier() {
        return seoIdentifier;
    }

    public void setSeoIdentifier(String seoIdentifier) {
        this.seoIdentifier = seoIdentifier;
    }

    public boolean isUnavailable() {
        return isUnavailable;
    }

    public void setUnavailable(boolean unavailable) {
        isUnavailable = unavailable;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(boolean soldOut) {
        isSoldOut = soldOut;
    }
}
