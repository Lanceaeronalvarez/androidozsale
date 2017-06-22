package au.com.dealsdirect.data.network.model.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 6/22/17.
 */

public class GetBannerRequest {

    @Expose
    @SerializedName("offset")
    private String offset;

    @Expose
    @SerializedName("limit")
    private String limit;

    @Expose
    @SerializedName("category")
    private String category;

    @Expose
    @SerializedName("categoryId")
    private String categoryId;

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getLimit() {
        return limit;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
