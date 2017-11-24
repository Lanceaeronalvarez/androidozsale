
package au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPaymentPlansResponse {

    @SerializedName("d")
    @Expose
    private D d;

    public D getD() {
        return d;
    }

    public void setD(D d) {
        this.d = d;
    }

    public Boolean getIsAuthenticated() {
        return getD().getIsAuthenticated();
    }

    public void setIsAuthenticated(Boolean isAuthenticated) {
        getD().setIsAuthenticated(isAuthenticated);
    }

    public Value getValue() {
        return getD().getValue();
    }

    public void setValue(Value value) {
        getD().setValue(value);
    }

    public Boolean getResult() {
        return getD().getResult();
    }

    public void setResult(Boolean result) {
        getD().setResult(result);
    }

    public String getMessage() {
        return getD().getMessage();
    }

    public void setMessage(String message) {
        getD().setMessage(message);
    }

}
