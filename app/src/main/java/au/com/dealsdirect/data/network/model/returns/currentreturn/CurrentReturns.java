package au.com.dealsdirect.data.network.model.returns.currentreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

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
    @SerializedName("ItemImages")
    @Expose
    private List<ItemImages> itemImagesList;
    @SerializedName("AttachmentID")
    @Expose
    private String attachmentID;
    @SerializedName("Attachments")
    @Expose
    private List<AttachmentItems> attachmentItemsList;

    public List<ItemImages> getItemImagesList() {
        return itemImagesList;
    }

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

    public String getAttachmentID() {
        return attachmentID;
    }

    public List<AttachmentItems> getAttachmentItemsList() {
        return attachmentItemsList;
    }

    @Override
    public boolean equals(Object object) {
        if (object != null) {
            if (object == this) {
                return true;
            } else if (object instanceof CurrentReturns) {
                CurrentReturns other = (CurrentReturns) object;
                return (getID() == null ? other.getID() == getID() : getID().equals(other.getID())) &&
                        (getInvoiceNo() == null ? other.getInvoiceNo() == getInvoiceNo() : getInvoiceNo().equals(other.getInvoiceNo())) &&
                        (getOrderNumber() == null ? other.getOrderNumber() == getOrderNumber() : getOrderNumber().equals(other.getOrderNumber())) &&
                        (getInvoiceNoRef() == null ? other.getInvoiceNoRef() == getInvoiceNoRef() : getInvoiceNoRef().equals(other.getInvoiceNoRef())) &&
                        (getLastSavedDate() == null ? other.getLastSavedDate() == getLastSavedDate() : getLastSavedDate().equals(other.getLastSavedDate())) &&
                        (getApprovedDate() == null ? other.getApprovedDate() == getApprovedDate() : getApprovedDate().equals(other.getApprovedDate())) &&
                        (getRan() == null ? other.getRan() == getRan() : getRan().equals(other.getRan())) &&
                        (getReturnStatus() == null ? other.getReturnStatus() == getReturnStatus() : getReturnStatus().equals(other.getReturnStatus())) &&
                        (getDescription() == null ? other.getDescription() == getDescription() : getDescription().equals(other.getDescription()));
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(),
                getInvoiceNo(),
                getOrderNumber(),
                getInvoiceNoRef(),
                getLastSavedDate(),
                getApprovedDate(),
                getRan(),
                getReturnStatus(),
                getDescription());
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

    public class AttachmentItems {
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
