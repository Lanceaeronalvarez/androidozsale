
package au.com.dealsdirect.data.network.model.country;

import com.google.gson.annotations.Expose;

public class Country {

    @Expose
    private String shopCode;

    @Expose
    private String country;

    @Expose
    private String shopName;

    @Expose
    private String currency;


    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
