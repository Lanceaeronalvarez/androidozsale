package au.com.dealsdirect.data.network.model.productdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MTC on 2019-12-17.
 */
public class GetYouMayAlsoLikeResponse {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("masterProductId")
    @Expose
    private String masterProductId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("images")
    @Expose
    private List<String> imageList;
    @SerializedName("seoUrl")
    @Expose
    private String seoUrl;
    @SerializedName("seoIdentifier")
    @Expose
    private String seoIdentifier;

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

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public String getSeoUrl() {
        return seoUrl;
    }

    public void setSeoUrl(String seoUrl) {
        this.seoUrl = seoUrl;
    }

    public String getSeoIdentifier() {
        return seoIdentifier;
    }

    public void setSeoIdentifier(String seoIdentifier) {
        this.seoIdentifier = seoIdentifier;
    }
}
