package au.com.dealsdirect.data.network.model.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.cachedresponses.CachableRequest;

/**
 * dp Created by Admin on 6/22/17.
 */

public class GetBannerRequest implements CachableRequest {

    @Expose
    @SerializedName("lastGroupType")
    private String lastGroupType;

    @Expose
    @SerializedName("lastGroupOffset")
    private String offset;

    @Expose
    @SerializedName("limit")
    private String limit;

    @Expose
    @SerializedName("category")
    private String category;

    @Expose
    @SerializedName("saleCategoryID")
    private String saleCategoryId;

    @Expose
    @SerializedName("categoryId")
    private String categoryId;

    @Expose
    @SerializedName("includeCampaignBanners")
    private Boolean includeCampaignBanners;

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setSaleCategoryId(String saleCategoryId) {
        this.saleCategoryId = saleCategoryId;
    }

    public void setLastGroupType(String lastGroupType) {
        this.lastGroupType = lastGroupType;
    }

    public Boolean getIncludeCampaignBanners() {
        return includeCampaignBanners;
    }

    public void setIncludeCampaignBanners(Boolean includeCampaignBanners) {
        this.includeCampaignBanners = includeCampaignBanners;
    }

    private String fieldToString(Object field) {
        String string;
        if (field instanceof String) {
            string = (String) field;
        } else if (field != null) {
            string = field.toString();
        } else {
            return "null";
        }
        return string;
    }

    @Override
    public String getCacheKey() {
        return getClass().getSimpleName() + "," +
                fieldToString(lastGroupType) + "," +
                fieldToString(offset) + "," +
                fieldToString(limit) + "," +
                fieldToString(category) + "," +
                fieldToString(saleCategoryId) + "," +
                fieldToString(categoryId) + "," +
                fieldToString(includeCampaignBanners);
    }
}
