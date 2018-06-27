package au.com.dealsdirect.data.network.model.ourpaydata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 3/15/18.
 */

public class OurpayDataRequest {

    @Expose
    @SerializedName("currencyId")
    private String currencyId;

    @Expose
    @SerializedName("amount")
    private Double amount;

    public static OurpayDataRequest init(String currencyId, Double amount) {
        OurpayDataRequest request = new OurpayDataRequest();
        request.currencyId = currencyId;
        request.amount = amount;
        return request;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
