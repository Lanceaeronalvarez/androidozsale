
package au.com.dealsdirect.data.network.model.returns.createreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateReturnRequestResponse {
    @SerializedName("messages")
    @Expose
    private List<String> messages;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("invoice_number")
    @Expose
    private String invoiceNumber;
    @SerializedName("reason")
    @Expose
    private String reason;

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
