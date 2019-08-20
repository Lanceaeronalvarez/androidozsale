
package au.com.dealsdirect.data.network.model.returns.returndetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Value {

    @SerializedName("Items")
    @Expose
    private List<Item> items = null;
    @SerializedName("Total")
    @Expose
    private Double total;
    @SerializedName("Reason")
    private String reason;
    @SerializedName("ContactNo")
    private int contactNumber;
    @SerializedName("AttachmentID")
    private String attachmentId;
    @SerializedName("Attachments")
    private List<Attachments> attachments;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getReason() {
        return reason;
    }

    public int getContactNumber() {
        return contactNumber;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public List<Attachments> getAttachments() {
        return attachments;
    }

    class Attachments {

        @SerializedName("Type")
        private String type;
        @SerializedName("Title")
        private String title;
        @SerializedName("Description")
        private String description;
        @SerializedName("Url")
        private String url;

        public String getType() {
            return type;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getUrl() {
            return url;
        }
    }

}
