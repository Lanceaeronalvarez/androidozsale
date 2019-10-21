package au.com.dealsdirect.data.wishlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallAddToWishlistRequest {
    @SerializedName("productId")
    @Expose
    private String productId;

    @SerializedName("masterSkuId")
    @Expose
    private String seoIdentifier;

    public CallAddToWishlistRequest(String productId, String seoIdentifier) {
        this.productId = productId;
        this.seoIdentifier = seoIdentifier;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSeoIdentifier() {
        return seoIdentifier;
    }

    public void setSeoIdentifier(String seoIdentifier) {
        this.seoIdentifier = seoIdentifier;
    }
}
