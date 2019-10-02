
package au.com.dealsdirect.data.network.model.returns.createreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateReturnRequestResponse {

    @SerializedName("__type")
    @Expose
    private String type;
    @SerializedName("IsAuthenticated")
    @Expose
    private Boolean isAuthenticated;
    @SerializedName("Value")
    @Expose
    private Value value;
    @SerializedName("Result")
    @Expose
    private Boolean result;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("InvoiceNo")
    @Expose
    private String invoiceNumber;
    @SerializedName("ReasonForReturn")
    @Expose
    private String reasonForReturn;

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

    public Value getValue() {
        return value;
    }

    public class Value {
        @SerializedName("ID")
        private String returnId;
        @SerializedName("InvoiceNo")
        private String invoiceNumber;
        @SerializedName("ReasonForReturn")
        private String reasonForReturn;

        public String getReturnId() {
            return returnId;
        }

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public String getReasonForReturn() {
            return reasonForReturn;
        }
    }

    public String getId() {
        return id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getReasonForReturn() {
        return reasonForReturn;
    }

}
