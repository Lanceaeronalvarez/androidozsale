
package au.com.dealsdirect.data.network.model.contacthistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
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
    @SerializedName("escalate")
    @Expose
    private Escalate escalate;

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

    public Escalate getEscalate() {
        return escalate;
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

        private transient List<Attachment> imageAttachments = null;
        private transient List<Attachment> linkAttachments = null;

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

        public List<Attachment> getImageAttachments() {
            separateAttachmentsByType();
            return imageAttachments;
        }

        public List<Attachment> getLinkAttachments() {
            separateAttachmentsByType();
            return linkAttachments;
        }

        private void separateAttachmentsByType() {
            if (attachments == null || (imageAttachments != null && linkAttachments != null)) {
                return;
            }

            imageAttachments = new LinkedList<>();
            linkAttachments = new LinkedList<>();

            for (Attachment attachment : attachments) {
                if (attachment.isImage()) {
                    imageAttachments.add(attachment);
                } else {
                    linkAttachments.add(attachment);
                }
            }
        }

        public static class Attachment {
            private final static String ATTACHMENT_TYPE_IMAGE = "image";

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

            public boolean isImage() {
                return type.toLowerCase().contains(ATTACHMENT_TYPE_IMAGE);
            }
        }
    }

    public static class Escalate {
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("text_color")
        @Expose
        private String textColor;
        @SerializedName("background_color")
        @Expose
        private String backgroundColor;

        public String getText() {
            return text;
        }

        public String getTextColor() {
            return textColor;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }
    }
}
