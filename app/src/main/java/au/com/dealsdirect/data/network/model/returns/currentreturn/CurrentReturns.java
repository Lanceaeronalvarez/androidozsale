package au.com.dealsdirect.data.network.model.returns.currentreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 6/29/17.
 */

public class CurrentReturns {
    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("InvoiceNo")
    @Expose
    private Integer invoiceNo;
    @SerializedName("OrderNumber")
    @Expose
    private Integer orderNumber;
    @SerializedName("InvoiceNoRef")
    @Expose
    private Integer invoiceNoRef;
    @SerializedName("LastSavedDate")
    @Expose
    private String lastSavedDate;
    @SerializedName("ApprovedDate")
    @Expose
    private Object approvedDate;
    @SerializedName("Ran")
    @Expose
    private String ran;
    @SerializedName("ReturnStatus")
    @Expose
    private String returnStatus;
    @SerializedName("Description")
    @Expose
    private String description;

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public Integer getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(Integer invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getInvoiceNoRef() {
        return invoiceNoRef;
    }

    public void setInvoiceNoRef(Integer invoiceNoRef) {
        this.invoiceNoRef = invoiceNoRef;
    }

    public String getLastSavedDate() {
        return lastSavedDate;
    }

    public void setLastSavedDate(String lastSavedDate) {
        this.lastSavedDate = lastSavedDate;
    }

    public Object getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Object approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getRan() {
        return ran;
    }

    public void setRan(String ran) {
        this.ran = ran;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
