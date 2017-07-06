package au.com.dealsdirect.data.network.model.contactorder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 7/5/17.
 */
public class ContactOrderResponse {


    @SerializedName("IsAuthenticated")
    @Expose
    private Boolean isAuthenticated;
    @SerializedName("List")
    @Expose
    private java.util
            .List<ContactOrderList> list = null;
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

    public java.util.List<ContactOrderList> getList() {
        return list;
    }

    public void setList(java.util.List<ContactOrderList> list) {
        this.list = list;
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
