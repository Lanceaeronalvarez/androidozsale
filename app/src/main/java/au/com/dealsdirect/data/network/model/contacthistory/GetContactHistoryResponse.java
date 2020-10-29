
package au.com.dealsdirect.data.network.model.contacthistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetContactHistoryResponse {
    @SerializedName("number")
    @Expose
    private Integer number;

    @SerializedName("invoiceNumber")
    @Expose
    private Integer invoiceNumber;

    @SerializedName("subject")
    @Expose
    private String subject;

    @SerializedName("messages")
    @Expose
    private List<Message> messages;

    @SerializedName("actions")
    @Expose
    private List<String> actions;

    public Integer getNumber() {
        return number;
    }

    public Integer getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getSubject() {
        return subject;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<String> getActions() {
        return actions;
    }

    public static class Message {
        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("message_date")
        @Expose
        private String messageDate;

        @SerializedName("is_staff")
        @Expose
        private Boolean isStaff;

        @SerializedName("user_name")
        @Expose
        private String userName;

        @SerializedName("attachments")
        @Expose
        private List<Attachment> attachments;

        @SerializedName("text")
        @Expose
        private String text;

        public String getId() {
            return id;
        }

        public String getMessageDate() {
            return messageDate;
        }

        public Boolean isStaff() {
            return isStaff;
        }

        public String getUserName() {
            return userName;
        }

        public List<Attachment> getAttachments() {
            return attachments;
        }

        public String getText() {
            return text;
        }

        public static class Attachment {
            @SerializedName("type")
            @Expose
            private String type;

            @SerializedName("url")
            @Expose
            private String url;

            public String getType() {
                return type;
            }

            public String getUrl() {
                return url;
            }
        }
    }
}
