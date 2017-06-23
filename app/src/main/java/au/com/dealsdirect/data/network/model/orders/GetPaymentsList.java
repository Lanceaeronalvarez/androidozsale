package au.com.dealsdirect.data.network.model.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.ArrayList;

/**
 * dp Created by Admin on 11/9/16.
 */
public class GetPaymentsList {

    public static final class RequestValues {

    }


    public static final class ResponseValue {

        public Response getD() {
            return d;
        }

        private Response d;

        public static class Response extends LegacyBaseResponseValue{

            public ArrayList<PaymentItem> getList() {
                return list;
            }

            @SerializedName("List")
            @Expose
            public ArrayList<PaymentItem> list;
        }

        public static class PaymentItem {
            @SerializedName("PaymentGroupID")
            @Expose
            private String paymentGroupID;
            @SerializedName("PaymentReferenceNo")
            @Expose
            private Integer paymentReferenceNo;
            @SerializedName("Orders")
            @Expose
            private ArrayList<Order> orders;

            @SerializedName("Total")
            @Expose
            private Total total;

            public String getPaymentGroupID() {
                return paymentGroupID;
            }

            public void setPaymentGroupID(String paymentGroupID) {
                this.paymentGroupID = paymentGroupID;
            }

            public Integer getPaymentReferenceNo() {
                return paymentReferenceNo;
            }

            public void setPaymentReferenceNo(Integer paymentReferenceNo) {
                this.paymentReferenceNo = paymentReferenceNo;
            }

            public ArrayList<Order> getOrders() {
                return orders;
            }

            public void setOrders(ArrayList<Order> orders) {
                this.orders = orders;
            }

            public Total getTotal() {
                return total;
            }

            public void setTotal(Total total) {
                this.total = total;
            }
        }

        public static class Order {
            @SerializedName("OrderID")
            @Expose
            private String orderID;
            @SerializedName("InvoiceNo")
            @Expose
            private Integer invoiceNo;
            @SerializedName("Status")
            @Expose
            private String status;
            @SerializedName("Description")
            @Expose
            private String description;
            @SerializedName("ConsignmentNo")
            @Expose
            private String consignmentNo;
            @SerializedName("Link")
            @Expose
            private String link;
            @SerializedName("EstimatedDeliveryText")
            @Expose
            private String estimatedDeliveryText;
            @SerializedName("SubTotal")
            @Expose
            private SubTotal subTotal;
            @SerializedName("Tracker")
            @Expose
            private Tracker tracker;

            public String getOrderID() {
                return orderID;
            }

            public void setOrderID(String orderID) {
                this.orderID = orderID;
            }

            public Integer getInvoiceNo() {
                return invoiceNo;
            }

            public void setInvoiceNo(Integer invoiceNo) {
                this.invoiceNo = invoiceNo;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getConsignmentNo() {
                return consignmentNo;
            }

            public void setConsignmentNo(String consignmentNo) {
                this.consignmentNo = consignmentNo;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            public String getEstimatedDeliveryText() {
                return estimatedDeliveryText;
            }

            public void setEstimatedDeliveryText(String estimatedDeliveryText) {
                this.estimatedDeliveryText = estimatedDeliveryText;
            }

            public SubTotal getSubTotal() {
                return subTotal;
            }

            public void setSubTotal(SubTotal subTotal) {
                this.subTotal = subTotal;
            }

            public Tracker getTracker() {
                return tracker;
            }

            public void setTracker(Tracker tracker) {
                this.tracker = tracker;
            }
        }

        public static class SubTotal {
            @SerializedName("ItemsCount")
            @Expose
            private Integer itemsCount;
            @SerializedName("ItemsAmount")
            @Expose
            private Double itemsAmount;
            @SerializedName("DeliveryAmount")
            @Expose
            private Double deliveryAmount;

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

            public Double getDeliveryAmount() {
                return deliveryAmount;
            }

            public void setDeliveryAmount(Double deliveryAmount) {
                this.deliveryAmount = deliveryAmount;
            }
        }

        public static class Total {
            @SerializedName("CreditCardAmount")
            @Expose
            private Double creditCardAmount;
            @SerializedName("DiscountAmount")
            @Expose
            private Double discountAmount;
            @SerializedName("ItemsCount")
            @Expose
            private Integer itemsCount;
            @SerializedName("ItemsAmount")
            @Expose
            private Double itemsAmount;
            @SerializedName("DeliveryAmount")
            @Expose
            private Double deliveryAmount;
            @SerializedName("TotalAmount")
            @Expose
            private Double totalAmount;

            public Double getCreditCardAmount() {
                return creditCardAmount;
            }

            public void setCreditCardAmount(Double creditCardAmount) {
                this.creditCardAmount = creditCardAmount;
            }

            public Double getDiscountAmount() {
                return discountAmount;
            }

            public void setDiscountAmount(Double discountAmount) {
                this.discountAmount = discountAmount;
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

        }

        public static class Tracker {
            @SerializedName("Step")
            @Expose
            private Integer step;
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

            public Integer getStep() {
                return step;
            }

            public void setStep(Integer step) {
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
