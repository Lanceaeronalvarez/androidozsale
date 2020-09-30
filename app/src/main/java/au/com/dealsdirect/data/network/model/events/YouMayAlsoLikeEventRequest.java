package au.com.dealsdirect.data.network.model.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MTC on 2020-03-23.
 */
public class YouMayAlsoLikeEventRequest {

    @SerializedName("eventType")
    @Expose
    private Integer eventType;
    @SerializedName("frontEndInfo")
    @Expose
    private FrontEndInfo frontEndInfo;
    @SerializedName("visitorInfo")
    @Expose
    private VisitorInfo visitorInfo;
    @SerializedName("recommendationsViewInfo")
    @Expose
    private RecommendationsViewInfo recommendationsViewInfo;

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

    public RecommendationsViewInfo getRecommendationsViewInfo() {
        return recommendationsViewInfo;
    }

    public void setRecommendationsViewInfo(RecommendationsViewInfo recommendationsViewInfo) {
        this.recommendationsViewInfo = recommendationsViewInfo;
    }

    public static class RecommendationsViewInfo {
        @SerializedName("productId")
        @Expose
        private String productId;
        @SerializedName("productsQty")
        @Expose
        private int productsQty;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("position")
        @Expose
        private String position;

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }
    }
}
