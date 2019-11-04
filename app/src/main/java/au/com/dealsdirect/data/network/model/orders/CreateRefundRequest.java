package au.com.dealsdirect.data.network.model.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by MTC on 2019-07-10.
 */
public class CreateRefundRequest {
    @SerializedName("invoiceNo")
    @Expose
    private String invoiceNo;

    @SerializedName("reason")
    @Expose
    private String reason;

    @SerializedName("items")
    @Expose
    private HashMap<String, Integer> items;

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }

    public void setItems(HashMap<String, Integer> items) {
        this.items = items;
    }

}
