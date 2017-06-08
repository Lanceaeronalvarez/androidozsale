package au.com.dealsdirect.data.network.model.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 6/7/17.
 */

public class GetPublicSalesBannerRequest {

    @Expose
    @SerializedName("saleCategoryID")
    private String saleCategoryID;

    @Expose
    @SerializedName("topSalesCount")
    private int topSalesCount;

    @Expose
    @SerializedName("useOzsaleSize")
    private boolean useOzsaleSize;

    @Expose
    @SerializedName("getPromotion")
    private boolean getPromotion;

    @Expose
    @SerializedName("groupNo")
    private int groupNo;

    @Expose
    @SerializedName("languageID")
    private String languageID;

    @Expose
    @SerializedName("countryID")
    private String countryID;

    @Expose
    @SerializedName("userGroup")
    private String userGroup;


    private GetPublicSalesBannerRequest() {
        // This class is not publicly instantiable
    }

    public GetPublicSalesBannerRequest(String saleCategoryID, int topSalesCount, boolean useOzsaleSize,
            boolean getPromotion, int groupNo, String languageID, String countryID,
            String userGroup) {

        this.saleCategoryID = saleCategoryID;
        this.topSalesCount = topSalesCount;
        this.useOzsaleSize = useOzsaleSize;
        this.getPromotion = getPromotion;
        this.groupNo = groupNo;
        this.languageID = languageID;
        this.countryID = countryID;
        this.userGroup = userGroup;
    }

    public String getSaleCategoryID() {
        return saleCategoryID;
    }

    public void setSaleCategoryID(String saleCategoryID) {
        this.saleCategoryID = saleCategoryID;
    }

    public int getTopSalesCount() {
        return topSalesCount;
    }

    public void setTopSalesCount(int topSalesCount) {
        this.topSalesCount = topSalesCount;
    }

    public boolean isUseOzsaleSize() {
        return useOzsaleSize;
    }

    public void setUseOzsaleSize(boolean useOzsaleSize) {
        this.useOzsaleSize = useOzsaleSize;
    }

    public boolean isGetPromotion() {
        return getPromotion;
    }

    public void setGetPromotion(boolean getPromotion) {
        this.getPromotion = getPromotion;
    }

    public int getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(int groupNo) {
        this.groupNo = groupNo;
    }

    public String getLanguageID() {
        return languageID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }
}
