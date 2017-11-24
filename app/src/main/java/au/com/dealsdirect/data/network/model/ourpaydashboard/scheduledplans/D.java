
package au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class D {

    @SerializedName("IsAuthenticated")
    @Expose
    private Boolean isAuthenticated;
    @SerializedName("ScheduledPlan")
    @Expose
    private List<ScheduledPlan> scheduledPlan = null;
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

    public List<ScheduledPlan> getScheduledPlan() {
        return scheduledPlan;
    }

    public void setScheduledPlan(List<ScheduledPlan> scheduledPlan) {
        this.scheduledPlan = scheduledPlan;
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
