
package au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledpayments;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class D {

    @SerializedName("IsAuthenticated")
    @Expose
    private Boolean isAuthenticated;
    @SerializedName("ScheduledPayment")
    @Expose
    private List<ScheduledPayment> scheduledPayment = null;
    @SerializedName("Result")
    @Expose
    private Boolean result;
    @SerializedName("Message")
    @Expose
    private String message;

    public Boolean getIsAuthenticated() {
        return isAuthenticated;
    }

    public void setIsAuthenticated(Boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public List<ScheduledPayment> getScheduledPayment() {
        return scheduledPayment;
    }

    public void setScheduledPayment(List<ScheduledPayment> scheduledPayment) {
        this.scheduledPayment = scheduledPayment;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
