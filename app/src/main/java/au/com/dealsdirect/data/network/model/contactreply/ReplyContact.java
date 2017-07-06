
package au.com.dealsdirect.data.network.model.contactreply;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReplyContact {

    @SerializedName("__type")
    @Expose
    private String type;
    @SerializedName("IsAuthenticated")
    @Expose
    private Boolean isAuthenticated;
    @SerializedName("Result")
    @Expose
    private Boolean result;
    @SerializedName("Message")
    @Expose
    private String message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsAuthenticated() {
        return isAuthenticated;
    }

    public void setIsAuthenticated(Boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
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
