
package au.com.dealsdirect.data.network.model.contactsubject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContactSubjectResponse {

    @SerializedName("List")
    @Expose
    private List<String> list = null;
    @SerializedName("Result")
    @Expose
    private Boolean result;
    @SerializedName("Message")
    @Expose
    private String message;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
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
