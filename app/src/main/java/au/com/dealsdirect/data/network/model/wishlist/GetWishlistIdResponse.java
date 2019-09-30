package au.com.dealsdirect.data.network.model.wishlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.wishlist.WishlistObject;

public class GetWishlistIdResponse implements WishlistObject {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("productId")
    @Expose
    private String productId;

    @SerializedName("seoIdentifier")
    @Expose
    private String seoIdentifier;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("visitorId")
    @Expose
    private String visitorId;

    @SerializedName(value = "masterProductId", alternate = {"MasterProductId"})
    @Expose
    private String masterProductId;

    @Override
    public String getProductId() {
        return productId;
    }

    @Override
    public void setProductId(String id) {
        productId = id;
    }

    @Override
    public String getSeoId() {
        return seoIdentifier;
    }

    @Override
    public void setSeoId(String id) {
        seoIdentifier = id;
    }

    @Override
    public String getMasterProductId() {
        return masterProductId;
    }

    @Override
    public void setMasterProductId(String masterProductId) {
        this.masterProductId = masterProductId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

}
