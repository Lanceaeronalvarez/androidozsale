
package au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledpayments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetScheduledPaymentsResponse {

    @SerializedName("d")
    @Expose
    private D d;

    public Boolean getIsAuthenticated() {
        return d.getIsAuthenticated();
    }

    public void setIsAuthenticated(Boolean isAuthenticated) {
        d.setIsAuthenticated(isAuthenticated);
    }

    public List<ScheduledPayment> getScheduledPayment() {
        return d.getScheduledPayment();
    }

    public void setScheduledPayment(List<ScheduledPayment> scheduledPayment) {
        d.setScheduledPayment(scheduledPayment);
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
