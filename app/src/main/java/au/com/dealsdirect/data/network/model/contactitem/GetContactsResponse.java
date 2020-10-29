
package au.com.dealsdirect.data.network.model.contactitem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetContactsResponse {

    @SerializedName("number")
    @Expose
    private Integer number;

    @SerializedName("invoice_number")
    @Expose
    private Integer invoiceNumber;

    @SerializedName("message_count")
    @Expose
    private Integer messageCount;

    @SerializedName("last_message_date")
    @Expose
    private String lastMessageDate;

    @SerializedName("last_message")
    @Expose
    private String lastMessage;

    @SerializedName("subject")
    @Expose
    private String subject;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Integer invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public String getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(String lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
