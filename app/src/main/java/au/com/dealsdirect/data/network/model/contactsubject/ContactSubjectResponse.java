
package au.com.dealsdirect.data.network.model.contactsubject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContactSubjectResponse {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("require_invoice")
    @Expose
    private Boolean requiresInvoice;

    @SerializedName("actions")
    @Expose
    private List<String> actions;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getRequiresInvoice() {
        return requiresInvoice;
    }

    public List<String> getActions() {
        return actions;
    }
}
