package au.com.dealsdirect.data.network.model.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.List;

/**
 * Created by smartwave on 09/01/2017.
 */

public class GetOrderPaymentDetails {

    public static final class RequestValues {

        public int imageSize;
        public String paymentReferenceNo;

        public RequestValues(String invoiceNo) {
            this.imageSize = 100;
            this.paymentReferenceNo = invoiceNo;
        }

    }


    public static final class ResponseValue {

        @SerializedName("d")
        @Expose
        private D d;

        public D getD() {
            return d;
        }

        public void setD(D d) {
            this.d = d;
        }

        public class D extends LegacyBaseResponseValue {
            public Value getValue() {
                return value;
            }

            @SerializedName("Value")
            @Expose
            private Value value;

        }

        public class Item {

            @SerializedName("Qty")
            @Expose
            private Integer qty;
            @SerializedName("ItemID")
            @Expose
            private String itemID;
            @SerializedName("Description")
            @Expose
            private String description;
            @SerializedName("BrandID")
            @Expose
            private String brandID;
            @SerializedName("SubTotal")
            @Expose
            private SubTotal subTotal;
            @SerializedName("ImageID")
            @Expose
            private String imageID;
            @SerializedName("Item")
            @Expose
            private String item;
            @SerializedName("FileName")
            @Expose
            private String fileName;
            @SerializedName("DeliveryAddress")
            @Expose
            private String deliveryAddress;
            @SerializedName("Price")
            @Expose
            private Double price;
            @SerializedName("ID")
            @Expose
            private String iD;
            @SerializedName("Size")
            @Expose
            private String size;
            @SerializedName("Actions")
            @Expose
            private List<String> actions;
            @SerializedName("ReturnID")
            @Expose
            private String returnID;

            public List<String> getActions() {
                return actions;
            }

            public void setActions(List<String> actions) {
                this.actions = actions;
            }

            public String getReturnID() {
                return returnID;
            }

            public void setReturnID(String returnID) {
                this.returnID = returnID;
            }

            public Integer getQty() {
                return qty;
            }

            public void setQty(Integer qty) {
                this.qty = qty;
            }

            public String getItemID() {
                return itemID;
            }

            public void setItemID(String itemID) {
                this.itemID = itemID;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getBrandID() {
                return brandID;
            }

            public void setBrandID(String brandID) {
                this.brandID = brandID;
            }

            public SubTotal getSubTotal() {
                return subTotal;
            }

            public void setSubTotal(SubTotal subTotal) {
                this.subTotal = subTotal;
            }

            public String getImageID() {
                return imageID;
            }

            public void setImageID(String imageID) {
                this.imageID = imageID;
            }

            public String getItem() {
                return item;
            }

            public void setItem(String item) {
                this.item = item;
            }

            public String getFileName() {
                return fileName;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName;
            }

            public String getDeliveryAddress() {
                return deliveryAddress;
            }

            public void setDeliveryAddress(String deliveryAddress) {
                this.deliveryAddress = deliveryAddress;
            }

            public Double getPrice() {
                return price;
            }

            public void setPrice(Double price) {
                this.price = price;
            }

            public String getID() {
                return iD;
            }

            public void setID(String iD) {
                this.iD = iD;
            }

            public String getSize() {
                return size;
            }

            public void setSize(String size) {
                this.size = size;
            }

        }

        public class Labels {

            @SerializedName("TaxIncluded")
            @Expose
            private Boolean taxIncluded;
            @SerializedName("CreditCardAmount")
            @Expose
            private String creditCardAmount;
            @SerializedName("TotalAmounExclVat")
            @Expose
            private String totalAmounExclVat;
            @SerializedName("DeliveryAmount")
            @Expose
            private String deliveryAmount;
            @SerializedName("TotalAmount")
            @Expose
            private String totalAmount;
            @SerializedName("InternationalDeliveryAmount")
            @Expose
            private Object internationalDeliveryAmount;
            @SerializedName("TaxAmount")
            @Expose
            private String taxAmount;
            @SerializedName("DiscountAmount")
            @Expose
            private String discountAmount;
            @SerializedName("ItemsAmount")
            @Expose
            private String itemsAmount;

            public Boolean getTaxIncluded() {
                return taxIncluded;
            }

            public void setTaxIncluded(Boolean taxIncluded) {
                this.taxIncluded = taxIncluded;
            }

            public String getCreditCardAmount() {
                return creditCardAmount;
            }

            public void setCreditCardAmount(String creditCardAmount) {
                this.creditCardAmount = creditCardAmount;
            }

            public String getTotalAmounExclVat() {
                return totalAmounExclVat;
            }

            public void setTotalAmounExclVat(String totalAmounExclVat) {
                this.totalAmounExclVat = totalAmounExclVat;
            }

            public String getDeliveryAmount() {
                return deliveryAmount;
            }

            public void setDeliveryAmount(String deliveryAmount) {
                this.deliveryAmount = deliveryAmount;
            }

            public String getTotalAmount() {
                return totalAmount;
            }

            public void setTotalAmount(String totalAmount) {
                this.totalAmount = totalAmount;
            }

            public Object getInternationalDeliveryAmount() {
                return internationalDeliveryAmount;
            }

            public void setInternationalDeliveryAmount(Object internationalDeliveryAmount) {
                this.internationalDeliveryAmount = internationalDeliveryAmount;
            }

            public String getTaxAmount() {
                return taxAmount;
            }

            public void setTaxAmount(String taxAmount) {
                this.taxAmount = taxAmount;
            }

            public String getDiscountAmount() {
                return discountAmount;
            }

            public void setDiscountAmount(String discountAmount) {
                this.discountAmount = discountAmount;
            }

            public String getItemsAmount() {
                return itemsAmount;
            }

            public void setItemsAmount(String itemsAmount) {
                this.itemsAmount = itemsAmount;
            }

        }

        public class SubTotal {

            @SerializedName("ItemsCount")
            @Expose
            private Integer itemsCount;
            @SerializedName("ItemsAmount")
            @Expose
            private Double itemsAmount;
            @SerializedName("DeliveryAmount")
            @Expose
            private Integer deliveryAmount;

            public Integer getDeliveryAmount() {
                return deliveryAmount;
            }

            public void setDeliveryAmount(Integer deliveryAmount) {
                this.deliveryAmount = deliveryAmount;
            }

            public Integer getItemsCount() {
                return itemsCount;
            }

            public void setItemsCount(Integer itemsCount) {
                this.itemsCount = itemsCount;
            }

            public Double getItemsAmount() {
                return itemsAmount;
            }

            public void setItemsAmount(Double itemsAmount) {
                this.itemsAmount = itemsAmount;
            }

        }

        public class Total {

            @SerializedName("ItemsCount")
            @Expose
            private Integer itemsCount;
            @SerializedName("CreditCardAmount")
            @Expose
            private Double creditCardAmount;
            @SerializedName("TotalAmounExclVat")
            @Expose
            private Double totalAmounExclVat;
            @SerializedName("DeliveryAmount")
            @Expose
            private Double deliveryAmount;
            @SerializedName("TotalAmount")
            @Expose
            private Double totalAmount;
            @SerializedName("InternationalDeliveryAmount")
            @Expose
            private Double internationalDeliveryAmount;
            @SerializedName("TaxAmount")
            @Expose
            private Double taxAmount;
            @SerializedName("DiscountAmount")
            @Expose
            private Double discountAmount;
            @SerializedName("ItemsAmount")
            @Expose
            private Double itemsAmount;

            public Integer getItemsCount() {
                return itemsCount;
            }

            public void setItemsCount(Integer itemsCount) {
                this.itemsCount = itemsCount;
            }

            public Double getCreditCardAmount() {
                return creditCardAmount;
            }

            public void setCreditCardAmount(Double creditCardAmount) {
                this.creditCardAmount = creditCardAmount;
            }

            public Double getTotalAmounExclVat() {
                return totalAmounExclVat;
            }

            public void setTotalAmounExclVat(Double totalAmounExclVat) {
                this.totalAmounExclVat = totalAmounExclVat;
            }

            public Double getDeliveryAmount() {
                return deliveryAmount;
            }

            public void setDeliveryAmount(Double deliveryAmount) {
                this.deliveryAmount = deliveryAmount;
            }

            public Double getTotalAmount() {
                return totalAmount;
            }

            public void setTotalAmount(Double totalAmount) {
                this.totalAmount = totalAmount;
            }

            public Double getInternationalDeliveryAmount() {
                return internationalDeliveryAmount;
            }

            public void setInternationalDeliveryAmount(Double internationalDeliveryAmount) {
                this.internationalDeliveryAmount = internationalDeliveryAmount;
            }

            public Double getTaxAmount() {
                return taxAmount;
            }

            public void setTaxAmount(Double taxAmount) {
                this.taxAmount = taxAmount;
            }

            public Double getDiscountAmount() {
                return discountAmount;
            }

            public void setDiscountAmount(Double discountAmount) {
                this.discountAmount = discountAmount;
            }

            public Double getItemsAmount() {
                return itemsAmount;
            }

            public void setItemsAmount(Double itemsAmount) {
                this.itemsAmount = itemsAmount;
            }

        }

        public static class Value {

            @SerializedName("ApprovedDate")
            @Expose
            private String approvedDate;
            @SerializedName("Orders")
            @Expose
            private List<Order> orders = null;
            @SerializedName("Labels")
            @Expose
            private Labels labels;
            @SerializedName("PaymentReferenceNo")
            @Expose
            private Integer paymentReferenceNo;
            @SerializedName("Total")
            @Expose
            private Total total;
            @SerializedName("PaymentGroupID")
            @Expose
            private Integer paymentGroupID;

            public String getApprovedDate() {
                return approvedDate;
            }

            public void setApprovedDate(String approvedDate) {
                this.approvedDate = approvedDate;
            }

            public List<Order> getOrders() {
                return orders;
            }

            public void setOrders(List<Order> orders) {
                this.orders = orders;
            }

            public Labels getLabels() {
                return labels;
            }

            public void setLabels(Labels labels) {
                this.labels = labels;
            }

            public Integer getPaymentReferenceNo() {
                return paymentReferenceNo;
            }

            public void setPaymentReferenceNo(Integer paymentReferenceNo) {
                this.paymentReferenceNo = paymentReferenceNo;
            }

            public Total getTotal() {
                return total;
            }

            public void setTotal(Total total) {
                this.total = total;
            }

            public Integer getPaymentGroupID() {
                return paymentGroupID;
            }

            public void setPaymentGroupID(Integer paymentGroupID) {
                this.paymentGroupID = paymentGroupID;
            }

        }

        public class Order {

            @SerializedName("Items")
            @Expose
            private List<Item> items = null;
            @SerializedName("Customer")
            @Expose
            private String customer;
            @SerializedName("Description")
            @Expose
            private String description;
            @SerializedName("RefundItems")
            @Expose
            private Object refundItems;
            @SerializedName("InvoiceNo")
            @Expose
            private Integer invoiceNo;
            @SerializedName("OrderID")
            @Expose
            private String orderID;
            @SerializedName("DeliveryAddress")
            @Expose
            private String deliveryAddress;
            @SerializedName("Received")
            @Expose
            private String received;
            @SerializedName("Tracker")
            @Expose
            private Tracker tracker;
            @SerializedName("Actions")
            @Expose
            private List<String> actions;

            public List<String> getActions() {
                return actions;
            }

            public void setActions(List<String> actions) {
                this.actions = actions;
            }

            public List<Item> getItems() {
                return items;
            }

            public void setItems(List<Item> items) {
                this.items = items;
            }

            public String getCustomer() {
                return customer;
            }

            public void setCustomer(String customer) {
                this.customer = customer;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public Object getRefundItems() {
                return refundItems;
            }

            public void setRefundItems(Object refundItems) {
                this.refundItems = refundItems;
            }

            public Integer getInvoiceNo() {
                return invoiceNo;
            }

            public void setInvoiceNo(Integer invoiceNo) {
                this.invoiceNo = invoiceNo;
            }

            public String getOrderID() {
                return orderID;
            }

            public void setOrderID(String orderID) {
                this.orderID = orderID;
            }

            public String getDeliveryAddress() {
                return deliveryAddress;
            }

            public void setDeliveryAddress(String deliveryAddress) {
                this.deliveryAddress = deliveryAddress;
            }

            public String getReceived() {
                return received;
            }

            public void setReceived(String received) {
                this.received = received;
            }

            public Tracker getTracker() {
                return tracker;
            }

            public void setTracker(Tracker tracker) {
                this.tracker = tracker;
            }

        }

        public static class Tracker {
            @SerializedName("Step")
            @Expose
            private int step;
            @SerializedName("ApprovedDate")
            @Expose
            private String approvedDate;
            @SerializedName("StockDate")
            @Expose
            private String stockDate;
            @SerializedName("DispatchedDate")
            @Expose
            private String dispatchedDate;
            @SerializedName("ClosedDate")
            @Expose
            private String closedDate;

            public int getStep() {
                return step;
            }

            public void setStep(int step) {
                this.step = step;
            }

            public String getApprovedDate() {
                return approvedDate;
            }

            public void setApprovedDate(String approvedDate) {
                this.approvedDate = approvedDate;
            }

            public String getStockDate() {
                return stockDate;
            }

            public void setStockDate(String stockDate) {
                this.stockDate = stockDate;
            }

            public String getDispatchedDate() {
                return dispatchedDate;
            }

            public void setDispatchedDate(String dispatchedDate) {
                this.dispatchedDate = dispatchedDate;
            }

            public String getClosedDate() {
                return closedDate;
            }

            public void setClosedDate(String closedDate) {
                this.closedDate = closedDate;
            }

        }
    }
}
