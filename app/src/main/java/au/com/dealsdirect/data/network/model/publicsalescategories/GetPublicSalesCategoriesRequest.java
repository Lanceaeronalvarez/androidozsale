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

    public GetPublicSalesCategoriesRequest(String modificatorVal, String languageIDVal, String getSalesVal, String userGroupVal, String countryIDVal){
        modificator = modificatorVal;
        languageID = languageIDVal;
        getSales = getSalesVal;
        userGroup = userGroupVal;
        countryID = countryIDVal;
    }

    public String getModificator() {
        return modificator;
    }

    public void setModificator(String modificator) {
        this.modificator = modificator;
    }

    public String getLanguageID() {
        return languageID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    public String getGetSales() {
        return getSales;
    }

    public void setGetSales(String getSales) {
        this.getSales = getSales;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }
}
