package au.com.dealsdirect.data.network.model.deeplinkdata;

/**
 * dp Created by Admin on 5/30/18.
 */

public class Meta {

    String categoryName;

    String categoryIdentifier;

    String saleName;

    String saleIdentifier;

    String seoFriendlyCategoryName;

    String seoProductName;

    String encodedMasterSkuIdentifier;

    String encodedSaleId;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryIdentifier() {
        return categoryIdentifier;
    }

    public void setCategoryIdentifier(String categoryIdentifier) {
        this.categoryIdentifier = categoryIdentifier;
    }

    public String getSaleName() {
        return saleName;
    }

    public void setSaleName(String saleName) {
        this.saleName = saleName;
    }

    public String getSaleIdentifier() {
        return saleIdentifier;
    }

    public void setSaleIdentifier(String saleIdentifier) {
        this.saleIdentifier = saleIdentifier;
    }

    public String getSeoFriendlyCategoryName() {
        return seoFriendlyCategoryName;
    }

    public void setSeoFriendlyCategoryName(String seoFriendlyCategoryName) {
        this.seoFriendlyCategoryName = seoFriendlyCategoryName;
    }

    public String getSeoProductName() {
        return seoProductName;
    }

    public void setSeoProductName(String seoProductName) {
        this.seoProductName = seoProductName;
    }

    public String getEncodedMasterSkuIdentifier() {
        return encodedMasterSkuIdentifier;
    }

    public void setEncodedMasterSkuIdentifier(String encodedMasterSkuIdentifier) {
        this.encodedMasterSkuIdentifier = encodedMasterSkuIdentifier;
    }

    public String getEncodedSaleId() {
        return encodedSaleId;
    }

    public void setEncodedSaleId(String encodedSaleId) {
        this.encodedSaleId = encodedSaleId;
    }
}
