
package au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPaymentPlansResponse {

    @SerializedName("d")
    @Expose
    private D d;

    public Boolean getIsAuthenticated() {
        return d.getIsAuthenticated();
    }

    public void setIsAuthenticated(Boolean isAuthenticated) {
        d.setIsAuthenticated(isAuthenticated);
    }

    public Value getValue() {
        return d.getValue();
    }

    public void setValue(Value value) {
        d.setValue(value);
    }

    public Boolean getResult() {
        return d.getResult();
    }

    public void setResult(Boolean result) {
        d.setResult(result);
    }

    public String getMessage() {
        return d.getMessage();
    }

    public void setMessage(String message) {
        d.setMessage(message);
    }

}
