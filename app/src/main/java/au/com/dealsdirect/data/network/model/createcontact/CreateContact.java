
package au.com.dealsdirect.data.network.model.createcontact;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateContact {

    @SerializedName("IsAuthenticated")
    @Expose
    private Boolean isAuthenticated;
    @SerializedName("ScheduledPayment")
    @Expose
    private Integer value;
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

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
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
