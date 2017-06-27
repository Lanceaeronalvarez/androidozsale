
package au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods;

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

    public String getLastPaidToken() {
        return lastPaidToken;
    }

    public PaymentMethod getLastPaymentMethod() {

        if (paymentMethods == null || paymentMethods.isEmpty())
            return null;

        PaymentMethod lastPaymentMethod = null;
        for (int i = 0; i < paymentMethods.size(); i++) {
            PaymentMethod paymentMethod = paymentMethods.get(i);
            if (paymentMethod.getToken().equalsIgnoreCase(getLastPaidToken())) {
                lastPaymentMethod = paymentMethod;
            }
        }
        return lastPaymentMethod;
    }
}
