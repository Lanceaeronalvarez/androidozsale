
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
    @SerializedName("AttachmentID")
    @Expose
    private String attachmentId;
    @SerializedName("Attachments")
    @Expose
    private java.util.List<Attachments> attachments;
    @SerializedName("ID")
    @Expose
    private String id;

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

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public java.util.List<Attachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(java.util.List<Attachments> attachments) {
        this.attachments = attachments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class Attachments {
        @SerializedName("Type")
        @Expose
        private String type;
        @SerializedName("Title")
        @Expose
        private String title;
        @SerializedName("Description")
        @Expose
        private String description;
        @SerializedName("Url")
        @Expose
        private String url;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}
