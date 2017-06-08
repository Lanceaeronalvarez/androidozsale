package au.com.dealsdirect.data.network.model.saleitems;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 6/8/17.
 */

public class GetPublicSaleItemsRequest {

    @Expose
    @SerializedName("saleID")
    private String saleID;

    @Expose
    @SerializedName("imageSize")
    private int imageSize;

    @Expose
    @SerializedName("languageID")
    private String languageID;

    @Expose
    @SerializedName("countryID")
    private String countryID;

    @Expose
    @SerializedName("userGroup")
    private String userGroup;

    public GetPublicSaleItemsRequest(String saleID, int imageSize, String languageID,
            String countryID, String userGroup) {
        this.saleID = saleID;
        this.imageSize = imageSize;
        this.languageID = languageID;
        this.countryID = countryID;
        this.userGroup = userGroup;
    }


    public String getSaleID() {
        return saleID;
    }

    public void setSaleID(String saleID) {
        this.saleID = saleID;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
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
