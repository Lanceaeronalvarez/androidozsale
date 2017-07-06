
package au.com.dealsdirect.data.network.model.contactorder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactOrderList {

    @SerializedName("InvoiceNo")
    @Expose
    private String invoiceNo;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("OrderNumber")
    @Expose
    private String orderNumber;

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

}
