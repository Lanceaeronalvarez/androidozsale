package au.com.dealsdirect.data.network.model.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.cachedresponses.CachableResponse;

public class GetSaleBannerDetailsResponse extends CachableResponse {
    @SerializedName("saleName")
    @Expose
    private String saleName;

    @SerializedName("percentOff")
    @Expose
    private Double percentOff;

    @SerializedName("isFreeDelivery")
    @Expose
    private Boolean isFreeDelivery;

    @SerializedName("endDate")
    @Expose
    private String endDate;

    @SerializedName("defaultSorting")
    @Expose
    String defaultSorting;

    public String getSaleName() {
        return saleName;
    }

    public void setSaleName(String saleName) {
        this.saleName = saleName;
    }

    public Double getPercentOff() {
        return percentOff;
    }

    public void setPercentOff(Double percentOff) {
        this.percentOff = percentOff;
    }

    public Boolean getFreeDelivery() {
        return isFreeDelivery;
    }

    public void setFreeDelivery(Boolean freeDelivery) {
        isFreeDelivery = freeDelivery;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDefaultSorting() {
        return defaultSorting;
    }

    public void setDefaultSorting(String defaultSorting) {
        this.defaultSorting = defaultSorting;
    }
}
