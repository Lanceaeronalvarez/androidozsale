package au.com.dealsdirect.data.network.model.productdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by smartwave on 09/06/2017.
 */

public class GetPublicSaleDetailsRequest {

    public GetPublicSaleDetailsRequest(String saleId, String countryId, String userGroup, String languageId) {
        this.saleId = saleId;
        this.countryId = countryId;
        this.userGroup = userGroup;
        this.languageId = languageId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    @Expose
    @SerializedName("saleID")
    public String saleId;
    @Expose
    @SerializedName("countryID")
    public String countryId;
    @Expose
    @SerializedName("userGroup")
    public String userGroup;
    @Expose
    @SerializedName("languageID")
    public String languageId;

}
