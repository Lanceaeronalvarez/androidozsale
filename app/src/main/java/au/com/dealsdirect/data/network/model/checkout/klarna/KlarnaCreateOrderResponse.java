package au.com.dealsdirect.data.network.model.checkout.klarna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class KlarnaCreateOrderResponse {
    @SerializedName("d")
    @Expose
    private D d;

    @SerializedName("Result")
    @Expose
    private boolean result;

    @SerializedName("Message")
    @Expose
    private String message;

    public D getD() {
        return d;
    }

    public static class D {
        @SerializedName("IsAuthenticated")
        @Expose
        private boolean isAuthenticated;

        @SerializedName("Value")
        @Expose
        private Value value;

        public boolean isAuthenticated() {
            return isAuthenticated;
        }

        public Value getValue() {
            return value;
        }
    }

    public static class Value {
        @SerializedName("AddressString")
        @Expose
        private String addressString;

        @SerializedName("CurrentAddress")
        @Expose
        private CurrentAddress currentAddress;

        @SerializedName("errorMessage")
        @Expose
        private String errorMessage;

        @SerializedName("invoiceNo")
        @Expose
        private String invoiceNo;

        @SerializedName("isPaid")
        @Expose
        private boolean isPaid;

        @SerializedName("OrderInfoResult")
        @Expose
        private OrderInfoResult orderInfoResult;

        @SerializedName("PaymentID")
        @Expose
        private String paymentID;

        @SerializedName("PaymentReceipt")
        @Expose
        private String paymentReceipt;

        @SerializedName("PaymentType")
        @Expose
        private int paymentType;

        @SerializedName("TransactionStatus")
        @Expose
        private String transactionStatus;

        @SerializedName("un")
        @Expose
        private String un;

        public String getAddressString() {
            return addressString;
        }

        public CurrentAddress getCurrentAddress() {
            return currentAddress;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getInvoiceNo() {
            return invoiceNo;
        }

        public boolean isPaid() {
            return isPaid;
        }

        public OrderInfoResult getOrderInfoResult() {
            return orderInfoResult;
        }

        public String getPaymentID() {
            return paymentID;
        }

        public String getPaymentReceipt() {
            return paymentReceipt;
        }

        public int getPaymentType() {
            return paymentType;
        }

        public String getTransactionStatus() {
            return transactionStatus;
        }

        public String getUn() {
            return un;
        }
    }

    public static class CurrentAddress {
        @SerializedName("ID")
        @Expose
        private String id;

        @SerializedName("Name")
        @Expose
        private String name;

        @SerializedName("Phone")
        @Expose
        private String phone;

        @SerializedName("State")
        @Expose
        private String state;

        @SerializedName("City")
        @Expose
        private String city;

        @SerializedName("Suburb")
        @Expose
        private String suburb;

        @SerializedName("Postcode")
        @Expose
        private String postcode;

        @SerializedName("AddressLines")
        @Expose
        private String addressLines;

        @SerializedName("AuthToLeave")
        @Expose
        private boolean authToLeave;

        @SerializedName("AuthComment")
        @Expose
        private String authComment;

        @SerializedName("AdditionalData")
        @Expose
        private String additionalData;

        @SerializedName("AddressType")
        @Expose
        private int addressType;

        @SerializedName("PostService")
        @Expose
        private String postService;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getState() {
            return state;
        }

        public String getCity() {
            return city;
        }

        public String getSuburb() {
            return suburb;
        }

        public String getPostcode() {
            return postcode;
        }

        public String getAddressLines() {
            return addressLines;
        }

        public boolean isAuthToLeave() {
            return authToLeave;
        }

        public String getAuthComment() {
            return authComment;
        }

        public String getAdditionalData() {
            return additionalData;
        }

        public int getAddressType() {
            return addressType;
        }

        public String getPostService() {
            return postService;
        }
    }

    public static class OrderInfoResult {
        @SerializedName("OrderID")
        @Expose
        private String orderId;

        @SerializedName("SaleName")
        @Expose
        private String saleName;

        @SerializedName("InvoiceNo")
        @Expose
        private String invoiceNo;

        @SerializedName("Total")
        @Expose
        private double total;

        @SerializedName("Tax")
        @Expose
        private double tax;

        @SerializedName("Shipping")
        @Expose
        private double shipping;

        @SerializedName("CustomerCity")
        @Expose
        private String customerCity;

        @SerializedName("CustomerState")
        @Expose
        private String customerState;

        @SerializedName("CustomerCountry")
        @Expose
        private String customerCountry;

        @SerializedName("Items")
        @Expose
        private List<Item> items;

        @SerializedName("EstimatedDeliveryText")
        @Expose
        private String estimatedDeliveryText;

        @SerializedName("DeliveryDateFrom")
        @Expose
        private String deliveryDateFrom;

        @SerializedName("DeliveryDateTo")
        @Expose
        private String deliveryDateTo;

        public String getOrderId() {
            return orderId;
        }

        public String getSaleName() {
            return saleName;
        }

        public String getInvoiceNo() {
            return invoiceNo;
        }

        public double getTotal() {
            return total;
        }

        public double getTax() {
            return tax;
        }

        public double getShipping() {
            return shipping;
        }

        public String getCustomerCity() {
            return customerCity;
        }

        public String getCustomerState() {
            return customerState;
        }

        public String getCustomerCountry() {
            return customerCountry;
        }

        public List<Item> getItems() {
            return items;
        }

        public String getEstimatedDeliveryText() {
            return estimatedDeliveryText;
        }

        public String getDeliveryDateFrom() {
            return deliveryDateFrom;
        }

        public String getDeliveryDateTo() {
            return deliveryDateTo;
        }

        public static class Item {
            @SerializedName("ID")
            @Expose
            private String id;

            @SerializedName("ProductName")
            @Expose
            private String productName;

            @SerializedName("Sku")
            @Expose
            private String sku;

            @SerializedName("SizeName")
            @Expose
            private String sizeName;

            @SerializedName("Price")
            @Expose
            private BigDecimal price;

            @SerializedName("Quantity")
            @Expose
            private int quantity;

            public String getId() {
                return id;
            }

            public String getProductName() {
                return productName;
            }

            public String getSku() {
                return sku;
            }

            public String getSizeName() {
                return sizeName;
            }

            public BigDecimal getPrice() {
                return price;
            }

            public int getQuantity() {
                return quantity;
            }
        }
    }
}
