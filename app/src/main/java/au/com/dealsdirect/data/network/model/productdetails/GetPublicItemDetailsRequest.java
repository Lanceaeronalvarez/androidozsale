package au.com.dealsdirect.data.network.model.productdetails;

/**
 * Created by smartwave on 08/06/2017.
 */

public class GetPublicItemDetailsRequest {

    public String itemID;
    public String saleID;
    public String getBigImages;
    public String includePrices;
    public String languageID;
    public String countryID;

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    public void setIncludePrices(boolean includePrices) {
        this.includePrices = String.valueOf(includePrices);
    }

    public void setGetBigImages(String getBigImages) {
        this.getBigImages = String.valueOf(getBigImages);
    }

    public void setSaleID(String saleID) {
        this.saleID = saleID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String userGroup;

    public GetPublicItemDetailsRequest(String itemID, String saleID, boolean getBigImages, boolean includePrices, String languageID, String countryID, String userGroup) {
        this.itemID = itemID;
        this.saleID = saleID;
        this.getBigImages = String.valueOf(getBigImages);
        this.includePrices = String.valueOf(includePrices);
        this.languageID = languageID;
        this.countryID = countryID;
        this.userGroup = userGroup;
    }
}
