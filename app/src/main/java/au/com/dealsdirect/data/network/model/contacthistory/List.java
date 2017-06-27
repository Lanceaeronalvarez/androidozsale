
package au.com.dealsdirect.data.network.model.contacthistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class List {

    @SerializedName("IsStaff")
    @Expose
    private Boolean isStaff;
    @SerializedName("Subject")
    @Expose
    private String subject;
    @SerializedName("InvoiceNo")
    @Expose
    private Integer invoiceNo;
    @SerializedName("Text")
    @Expose
    private String text;
    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("Date")
    @Expose
    private String date;

    public Boolean getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(Boolean isStaff) {
        this.isStaff = isStaff;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(Integer invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
