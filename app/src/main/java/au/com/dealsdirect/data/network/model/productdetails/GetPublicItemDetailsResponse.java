package au.com.dealsdirect.data.network.model.productdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by smartwave on 08/06/2017.
 */

public class GetPublicItemDetailsResponse {

    public Response getD() {
        return d;
    }

    private Response d;

    public static class Response {

        @SerializedName("Value")
        @Expose
        private Value value;
        @SerializedName("Result")
        @Expose
        private Boolean result;
        @SerializedName("Message")
        @Expose
        private String message;

        public Value getValue() {
            return value;
        }

        public void setValue(Value value) {
            this.value = value;
        }

        public Boolean getResult() {
            return result;
        }

        public void setResult(Boolean result) {
            this.result = result;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    public static class Image {

        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("SmallThumbnail")
        @Expose
        private String smallThumbnail;
        @SerializedName("Thumbnail")
        @Expose
        private String thumbnail;
        @SerializedName("Preview")
        @Expose
        private String preview;
        @SerializedName("PreviewWidth")
        @Expose
        private Integer previewWidth;
        @SerializedName("PreviewHeight")
        @Expose
        private Integer previewHeight;

        public String getID() {
            return iD;
        }

        public void setID(String iD) {
            this.iD = iD;
        }

        public String getSmallThumbnail() {
            return smallThumbnail;
        }

        public void setSmallThumbnail(String smallThumbnail) {
            this.smallThumbnail = smallThumbnail;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getPreview() {
            return preview;
        }

        public void setPreview(String preview) {
            this.preview = preview;
        }

        public Integer getPreviewWidth() {
            return previewWidth;
        }

        public void setPreviewWidth(Integer previewWidth) {
            this.previewWidth = previewWidth;
        }

        public Integer getPreviewHeight() {
            return previewHeight;
        }

        public void setPreviewHeight(Integer previewHeight) {
            this.previewHeight = previewHeight;
        }

    }

    public static class PaymentConditions {

        @SerializedName("MaxAmountThreshold")
        @Expose
        private Double maxAmountThreshold;
        @SerializedName("MinAmountThreshold")
        @Expose
        private Double minAmountThreshold;

        public Double getMaxAmountThreshold() {
            return maxAmountThreshold;
        }

        public void setMaxAmountThreshold(Double maxAmountThreshold) {
            this.maxAmountThreshold = maxAmountThreshold;
        }

        public Double getMinAmountThreshold() {
            return minAmountThreshold;
        }

        public void setMinAmountThreshold(Double minAmountThreshold) {
            this.minAmountThreshold = minAmountThreshold;
        }

    }

    public static class PaymentPlan {

        @SerializedName("BillingPeriod")
        @Expose
        private Integer billingPeriod;
        @SerializedName("TransactionCount")
        @Expose
        private Integer transactionCount;

        public Integer getBillingPeriod() {
            return billingPeriod;
        }

        public void setBillingPeriod(Integer billingPeriod) {
            this.billingPeriod = billingPeriod;
        }

        public Integer getTransactionCount() {
            return transactionCount;
        }

        public void setTransactionCount(Integer transactionCount) {
            this.transactionCount = transactionCount;
        }

    }

    public static class Size {

        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("Qty")
        @Expose
        private Integer qty;
        @SerializedName("Index")
        @Expose
        private Integer index;

        public String getID() {
            return iD;
        }

        public void setID(String iD) {
            this.iD = iD;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

    }

    public static class Value {

        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("BrandName")
        @Expose
        private String brandName;
        @SerializedName("BrandID")
        @Expose
        private String brandID;
        @SerializedName("ImageID")
        @Expose
        private String imageID;
        @SerializedName("FileName")
        @Expose
        private String fileName;
        @SerializedName("Width")
        @Expose
        private Integer width;
        @SerializedName("Height")
        @Expose
        private Integer height;
        @SerializedName("Description")
        @Expose
        private String description;
        @SerializedName("RegularPrice")
        @Expose
        private Double regularPrice;
        @SerializedName("UserPrice")
        @Expose
        private Double userPrice;
        @SerializedName("Qty")
        @Expose
        private Integer qty;
        @SerializedName("SaleName")
        @Expose
        private String saleName;
        @SerializedName("CategoryID")
        @Expose
        private String categoryID;
        @SerializedName("CategoryName")
        @Expose
        private String categoryName;
        @SerializedName("CategoryNameHtml")
        @Expose
        private String categoryNameHtml;
        @SerializedName("SubCategoryID")
        @Expose
        private String subCategoryID;
        @SerializedName("SubCategoryName")
        @Expose
        private String subCategoryName;
        @SerializedName("SubCategoryNameHtml")
        @Expose
        private String subCategoryNameHtml;
        @SerializedName("IsOpen")
        @Expose
        private Boolean isOpen;
        @SerializedName("RrpString")
        @Expose
        private String rrpString;
        @SerializedName("PriceLabel")
        @Expose
        private String priceLabel;
        @SerializedName("RrpLabel")
        @Expose
        private Object rrpLabel;
        @SerializedName("SaleEnd")
        @Expose
        private String saleEnd;
        @SerializedName("Sizes")
        @Expose
        private List<Size> sizes = null;
        @SerializedName("Images")
        @Expose
        private List<Image> images = null;
        @SerializedName("BillingAgreement")
        @Expose
        private Object billingAgreement;
        @SerializedName("PaymentPlan")
        @Expose
        private PaymentPlan paymentPlan;
        @SerializedName("PaymentConditions")
        @Expose
        private PaymentConditions paymentConditions;
        @SerializedName("MyPayAmount")
        @Expose
        private Double myPayAmount;
        @SerializedName("MyPayDetails")
        @Expose
        private Object myPayDetails;

        public String getID() {
            return iD;
        }

        public void setID(String iD) {
            this.iD = iD;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        public String getBrandID() {
            return brandID;
        }

        public void setBrandID(String brandID) {
            this.brandID = brandID;
        }

        public String getImageID() {
            return imageID;
        }

        public void setImageID(String imageID) {
            this.imageID = imageID;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getRegularPrice() {
            return regularPrice;
        }

        public void setRegularPrice(Double regularPrice) {
            this.regularPrice = regularPrice;
        }

        public Double getUserPrice() {
            return userPrice;
        }

        public void setUserPrice(Double userPrice) {
            this.userPrice = userPrice;
        }

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }

        public String getSaleName() {
            return saleName;
        }

        public void setSaleName(String saleName) {
            this.saleName = saleName;
        }

        public String getCategoryID() {
            return categoryID;
        }

        public void setCategoryID(String categoryID) {
            this.categoryID = categoryID;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getCategoryNameHtml() {
            return categoryNameHtml;
        }

        public void setCategoryNameHtml(String categoryNameHtml) {
            this.categoryNameHtml = categoryNameHtml;
        }

        public String getSubCategoryID() {
            return subCategoryID;
        }

        public void setSubCategoryID(String subCategoryID) {
            this.subCategoryID = subCategoryID;
        }

        public String getSubCategoryName() {
            return subCategoryName;
        }

        public void setSubCategoryName(String subCategoryName) {
            this.subCategoryName = subCategoryName;
        }

        public String getSubCategoryNameHtml() {
            return subCategoryNameHtml;
        }

        public void setSubCategoryNameHtml(String subCategoryNameHtml) {
            this.subCategoryNameHtml = subCategoryNameHtml;
        }

        public Boolean getIsOpen() {
            return isOpen;
        }

        public void setIsOpen(Boolean isOpen) {
            this.isOpen = isOpen;
        }

        public String getRrpString() {
            return rrpString;
        }

        public void setRrpString(String rrpString) {
            this.rrpString = rrpString;
        }

        public String getPriceLabel() {
            return priceLabel;
        }

        public void setPriceLabel(String priceLabel) {
            this.priceLabel = priceLabel;
        }

        public Object getRrpLabel() {
            return rrpLabel;
        }

        public void setRrpLabel(Object rrpLabel) {
            this.rrpLabel = rrpLabel;
        }

        public String getSaleEnd() {
            return saleEnd;
        }

        public void setSaleEnd(String saleEnd) {
            this.saleEnd = saleEnd;
        }

        public List<Size> getSizes() {
            return sizes;
        }

        public void setSizes(List<Size> sizes) {
            this.sizes = sizes;
        }

        public List<Image> getImages() {
            return images;
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public Object getBillingAgreement() {
            return billingAgreement;
        }

        public void setBillingAgreement(Object billingAgreement) {
            this.billingAgreement = billingAgreement;
        }

        public PaymentPlan getPaymentPlan() {
            return paymentPlan;
        }

        public void setPaymentPlan(PaymentPlan paymentPlan) {
            this.paymentPlan = paymentPlan;
        }

        public PaymentConditions getPaymentConditions() {
            return paymentConditions;
        }

        public void setPaymentConditions(PaymentConditions paymentConditions) {
            this.paymentConditions = paymentConditions;
        }

        public Double getMyPayAmount() {
            return myPayAmount;
        }

        public void setMyPayAmount(Double myPayAmount) {
            this.myPayAmount = myPayAmount;
        }

        public Object getMyPayDetails() {
            return myPayDetails;
        }

        public void setMyPayDetails(Object myPayDetails) {
            this.myPayDetails = myPayDetails;
        }

    }
}
