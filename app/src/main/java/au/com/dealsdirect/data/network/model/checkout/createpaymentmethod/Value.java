package au.com.dealsdirect.data.network.model.checkout.createpaymentmethod;
/*
 * Created by CodeineBot on 1/20/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Value {

    @SerializedName("PaymentMethods")
    @Expose
    private List<PaymentMethod> paymentMethods = null;
    @SerializedName("LastPaidToken")
    @Expose
    private String lastPaidToken;

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public String getLastPaidToken() {
        return lastPaidToken;
    }

    public void setLastPaidToken(String lastPaidToken) {
        this.lastPaidToken = lastPaidToken;
    }
}
