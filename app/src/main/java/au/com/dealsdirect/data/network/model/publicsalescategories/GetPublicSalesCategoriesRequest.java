package au.com.dealsdirect.data.network.model.publicsalescategories;

/**
 * Created by smartwave on 07/06/2017.
 */

public class GetPublicSalesCategoriesRequest {
    private String modificator;
    private String languageID;
    private String getSales;
    private String userGroup;
    private String countryID;

    public GetPublicSalesCategoriesRequest(String modificatorVal, String languageIDVal, boolean getSalesVal, String userGroupVal, String countryIDVal){
        modificator = modificatorVal;
        languageID = languageIDVal;
        getSales = String.valueOf(getSalesVal);
        userGroup = userGroupVal;
        countryID = countryIDVal;
    }

    public void setModificator(String modificator) {
        this.modificator = modificator;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    public void setGetSales(String getSales) {
        this.getSales = String.valueOf(getSales);
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }
}
