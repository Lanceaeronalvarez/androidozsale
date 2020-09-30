package au.com.dealsdirect.data.network.model.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MTC on 2020-01-07.
 */
public class RecentlyViewedEventRequest {
    @SerializedName("eventType")
    @Expose
    private int eventType;
    @SerializedName("recentlyViewInfo")
    @Expose
    private RecentlyViewedInfo recentlyViewInfo;
    @SerializedName("frontEndInfo")
    @Expose
    private FrontEndInfo frontEndInfo;
    @SerializedName("visitorInfo")
    @Expose
    private VisitorInfo visitorInfo;

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public RecentlyViewedInfo getRecentlyViewInfo() {
        return recentlyViewInfo;
    }

    public void setRecentlyViewInfo(RecentlyViewedInfo recentlyViewInfo) {
        this.recentlyViewInfo = recentlyViewInfo;
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

    public static class RecentlyViewedInfo {
        @SerializedName("productId")
        @Expose
        private String productId;
        @SerializedName("productsQty")
        @Expose
        private int productsQty;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getProductsQty() {
            return productsQty;
        }

        public void setProductsQty(int productsQty) {
            this.productsQty = productsQty;
        }
    }
}
