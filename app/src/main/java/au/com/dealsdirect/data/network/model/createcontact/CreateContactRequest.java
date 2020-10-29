package au.com.dealsdirect.data.network.model.createcontact;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 1/8/17.
 */

public class CreateContactRequest {

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("subject")
    @Expose
    private String subject;

    @SerializedName("invoice_number")
    @Expose
    private int invoiceNumber;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
