package au.com.dealsdirect.data.network.model.returns.createreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnItem;

/**
 * dp Created by Admin on 7/25/17.
 */
public class CreateReturnRequest {
    @SerializedName("invoice_number")
    @Expose
    int invoiceNumber;
    @SerializedName("items")
    @Expose
    List<NewReturnItem> items;
    @SerializedName("reason")
    @Expose
    String reason;

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public List<NewReturnItem> getItems() {
        return items;
    }

    public void setItems(List<NewReturnItem> items) {
        this.items = items;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
