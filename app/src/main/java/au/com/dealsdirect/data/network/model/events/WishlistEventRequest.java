package au.com.dealsdirect.data.network.model.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WishlistEventRequest {

    @SerializedName("eventType")
    @Expose
    private Integer eventType;
    @SerializedName("frontEndInfo")
    @Expose
    private FrontEndInfo frontEndInfo;
    @SerializedName("visitorInfo")
    @Expose
    private VisitorInfo visitorInfo;
    @SerializedName("wishlistInfo")
    @Expose
    private WishListInfo wishlistInfo;

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public FrontEndInfo getFrontEndInfo() {
        return frontEndInfo;
    }

    public void setFrontEndInfo(FrontEndInfo frontEndInfo) {
        this.frontEndInfo = frontEndInfo;
    }

    public VisitorInfo getVisitorInfo() {
        return visitorInfo;
    }

    public void setVisitorInfo(VisitorInfo visitorInfo) {
        this.visitorInfo = visitorInfo;
    }

    public WishListInfo getWishlistInfo() {
        return wishlistInfo;
    }

    public void setWishlistInfo(WishListInfo wishlistInfo) {
        this.wishlistInfo = wishlistInfo;
    }

    public static class WishListInfo {
        public static class ReferrerValue {
            public static final String PRODUCT_LIST = "ProductList";
            public static final String PRODUCT_PAGE = "ProductPage";
            public static final String WISHLIST = "Wishlist";
            public static final String HEADER = "Header";

            private ReferrerValue() {

            }
        }

        @SerializedName("referrer")
        @Expose
        private String referrer;
        @SerializedName("productId")
        @Expose
        private String productId;
        @SerializedName("operation")
        @Expose
        private Integer operation;
        @SerializedName("productsQty")
        @Expose
        private Integer productsQuantity;

        public String getReferrer() {
            return referrer;
        }

        public void setReferrer(String referrer) {
            this.referrer = referrer;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public Integer getOperation() {
            return operation;
        }

        public void setOperation(Integer operation) {
            this.operation = operation;
        }

        public Integer getProductsQuantity() {
            return productsQuantity;
        }

        public void setProductsQuantity(Integer productsQuantity) {
            this.productsQuantity = productsQuantity;
        }
    }
}
