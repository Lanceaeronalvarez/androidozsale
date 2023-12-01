package au.com.dealsdirect.data.network.model.returns.currentreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.com.dealsdirect.data.network.model.returns.newreturn.ImageAttachment;
import au.com.dealsdirect.data.network.model.returns.step.Step;

/**
 * dp Created by Admin on 6/29/17.
 */

public class CurrentReturn {
    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("invoice_number")
    @Expose
    int invoiceNumber;
    @SerializedName("requested_date")
    @Expose
    String requestedDate;
    @SerializedName("approved_date")
    @Expose
    String approvedDate;
    @SerializedName("contact_number")
    @Expose
    int contactNumber;
    @SerializedName("items")
    @Expose
    List<Item> items;
    @SerializedName("ran")
    @Expose
    String ran;
    @SerializedName("status")
    @Expose
    String status;
    @SerializedName("attachment_id")
    @Expose
    String attachmentId;
    @SerializedName("attachments")
    @Expose
    List<ImageAttachment> attachments;
    @SerializedName("total_amount")
    @Expose
    double totalAmount;
    @SerializedName("credit_amount")
    @Expose
    String creditAmount;
    @SerializedName("refunded_amount")
    @Expose
    String refundedAmount;
    @SerializedName("messages")
    @Expose
    List<Message> messages;
    @SerializedName("steps")
    @Expose
    List<Step> steps;
    @SerializedName("tracking_postages")
    @Expose
    List<TrackingPostage> trackingPostages;
    @SerializedName("reason")
    @Expose
    String reason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        this.requestedDate = requestedDate;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }

    public int getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(int contactNumber) {
        this.contactNumber = contactNumber;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getRan() {
        return ran;
    }

    public void setRan(String ran) {
        this.ran = ran;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public List<ImageAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ImageAttachment> attachments) {
        this.attachments = attachments;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(String refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<TrackingPostage> getTrackingPostages() {
        return trackingPostages;
    }

    public void setTrackingPostages(List<TrackingPostage> trackingPostages) {
        this.trackingPostages = trackingPostages;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public static class Item {
        @SerializedName("quantity")
        @Expose
        int quantity;
        @SerializedName("price")
        @Expose
        double price;
        @SerializedName("price_total")
        @Expose
        double priceTotal;
        @SerializedName("type")
        @Expose
        String type;
        @SerializedName("id")
        @Expose
        String id;
        @SerializedName("name")
        @Expose
        String name;
        @SerializedName("brand")
        @Expose
        String brand;
        @SerializedName("brand_link")
        @Expose
        String brandLink;
        @SerializedName("product_link")
        @Expose
        String productLink;
        @SerializedName("size")
        @Expose
        String size;
        @SerializedName("image_url")
        @Expose
        String imageUrl;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getPriceTotal() {
            return priceTotal;
        }

        public void setPriceTotal(double priceTotal) {
            this.priceTotal = priceTotal;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getBrandLink() {
            return brandLink;
        }

        public void setBrandLink(String brandLink) {
            this.brandLink = brandLink;
        }

        public String getProductLink() {
            return productLink;
        }

        public void setProductLink(String productLink) {
            this.productLink = productLink;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }

    public static class Message {
        @SerializedName("type")
        @Expose
        String type;
        @SerializedName("text")
        @Expose
        String text;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class TrackingPostage {
        @SerializedName("action_type")
        @Expose
        String actionType;
        @SerializedName("text")
        @Expose
        String text;
        @SerializedName("icon_url")
        @Expose
        String iconUrl;
        @SerializedName("value")
        @Expose
        String value;

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
