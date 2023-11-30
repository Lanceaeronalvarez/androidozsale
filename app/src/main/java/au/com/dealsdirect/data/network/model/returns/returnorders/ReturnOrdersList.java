
package au.com.dealsdirect.data.network.model.returns.returnorders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReturnOrdersList {

    @SerializedName("InvoiceNo")
    @Expose
    private Integer invoiceNo;
    @SerializedName("OrderNumber")
    @Expose
    private Integer orderNumber;
    @SerializedName("InvoiceNoRef")
    @Expose
    private Integer invoiceNoRef;
    @SerializedName("ItemsCount")
    @Expose
    private Integer itemsCount;
    @SerializedName("Total")
    @Expose
    private Double total;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("ConsignmentNo")
    @Expose
    private String consignmentNo;
    @SerializedName("ReturnRequested")
    @Expose
    private Boolean returnRequested;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("ItemImages")
    private java.util.List<ItemImages> itemImagesList;
    @SerializedName("AttachmentID")
    private String attachmentID;
    @SerializedName("Attachments")
    private java.util.List<AttachmentsImages> attachmentsList;

    public java.util.List<ItemImages> getItemImagesList() {
        return itemImagesList;
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

    public Integer getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(Integer itemsCount) {
        this.itemsCount = itemsCount;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConsignmentNo() {
        return consignmentNo;
    }

    public void setConsignmentNo(String consignmentNo) {
        this.consignmentNo = consignmentNo;
    }

    public Boolean getReturnRequested() {
        return returnRequested;
    }

    public void setReturnRequested(Boolean returnRequested) {
        this.returnRequested = returnRequested;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachmentID() {
        return attachmentID;
    }

    public java.util.List<AttachmentsImages> getAttachmentsList() {
        return attachmentsList;
    }

    public class ItemImages {
        @SerializedName("OrderItemID")
        private String orderItemId;
        @SerializedName("BrandID")
        private String brandId;
        @SerializedName("ImageID")
        private String imageId;
        @SerializedName("FileName")
        private String fileName;

        public String getOrderItemId() {
            return orderItemId;
        }

        public String getBrandId() {
            return brandId;
        }

        public String getImageId() {
            return imageId;
        }

        public String getFileName() {
            return fileName;
        }
    }

    public class AttachmentsImages {
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
