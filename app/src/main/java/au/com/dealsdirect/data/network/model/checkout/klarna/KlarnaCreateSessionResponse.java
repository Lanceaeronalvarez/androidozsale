package au.com.dealsdirect.data.network.model.checkout.klarna;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.List;

public class KlarnaCreateSessionResponse {
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
        @SerializedName("token")
        @Expose
        private String token;

        @SerializedName("paymentmethodcategories")
        @Expose
        private List<PaymentMethodCategory> paymentMethodCategories;

        @SerializedName("isSuccess")
        @Expose
        private boolean isSuccess;

        // unkown type
//        @SerializedName("error")
//        @Expose
//        private Object error;

        @SerializedName("paymentType")
        @Expose
        private int paymentType;

        @SerializedName("klarnaSessionModel")
        @Expose
        private JSONObject klarnaSessionModel;

        public String getToken() {
            return token;
        }

        public List<PaymentMethodCategory> getPaymentMethodCategories() {
            return paymentMethodCategories;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public int getPaymentType() {
            return paymentType;
        }

        public JSONObject getKlarnaSessionModel() {
            return klarnaSessionModel;
        }
    }

    public static class PaymentMethodCategory {
        @SerializedName("asset_urls")
        @Expose
        private AssetUrls assetUrls;

        @SerializedName("identifier")
        @Expose
        private String identifier;

        @SerializedName("name")
        @Expose
        private String name;

        public AssetUrls getAssetUrls() {
            return assetUrls;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getName() {
            return name;
        }

        private static class AssetUrls {
            @SerializedName("descriptive")
            @Expose
            private String descriptive;

            @SerializedName("standard")
            @Expose
            private String standard;

            public String getDescriptive() {
                return descriptive;
            }

            public String getStandard() {
                return standard;
            }
        }
    }

    public static class KlarnaSessionModel {
        // unknown type
//        @SerializedName("acquiring_channel")
//        @Expose
//        private String acquiring_channel;

        // unknown type
//        @SerializedName("attachment")
//        @Expose
//        private String attachment;

        @SerializedName("authorization_token")
        @Expose
        private String authorization_token;

        @SerializedName("billing_address")
        @Expose
        private JsonObject billingAddress;

        @SerializedName("client_token")
        @Expose
        private String clientToken;

        // unknown type
//        @SerializedName("custom_payment_method_ids")
//        @Expose
//        private String customPaymentMethodIds;

        // unknown type;
//        @SerializedName("customer")
//        @Expose
//        private String customer;

        // unknown type
//        @SerializedName("design")
//        @Expose
//        private String design;

        // unknown type
//        @SerializedName("expires_at")
//        @Expose
//        private String expiresAt;

        @SerializedName("locale")
        @Expose
        private String locale;

        @SerializedName("merchant_data")
        @Expose
        private String merchantData;

        // unknown type
//        @SerializedName("merchant_reference1")
//        @Expose
//        private String merchantReference1;

        // unknown type
//        @SerializedName("merchant_reference2")
//        @Expose
//        private String merchantReference2;

        // unknown type
//        @SerializedName("merchant_urls")
//        @Expose
//        private String merchantUrls;

        // unknown type
//        @SerializedName("options")
//        @Expose
//        private String options;

        @SerializedName("order_amount")
        @Expose
        private int orderAmount;

        @SerializedName("order_lines")
        @Expose
        private JsonArray orderLines;

        @SerializedName("order_tax_amount")
        @Expose
        private int orderTaxAmount;

        @SerializedName("payment_method_categories")
        @Expose
        private List<PaymentMethodCategory> paymentMethodCateogries;

        @SerializedName("purchase_country")
        @Expose
        private String purchaseCountry;

        @SerializedName("purchase_currency")
        @Expose
        private String purchaseCurrency;

        @SerializedName("ShippingAddress")
        @Expose
        private JsonObject shippingAddress;

        // unknown type
//        @SerializedName("status")
//        @Expose
//        private String status;

        public String getAuthorization_token() {
            return authorization_token;
        }

        public JsonObject getBillingAddress() {
            return billingAddress;
        }

        public String getClientToken() {
            return clientToken;
        }

        public String getLocale() {
            return locale;
        }

        public String getMerchantData() {
            return merchantData;
        }

        public int getOrderAmount() {
            return orderAmount;
        }

        public JsonArray getOrderLines() {
            return orderLines;
        }

        public int getOrderTaxAmount() {
            return orderTaxAmount;
        }

        public List<PaymentMethodCategory> getPaymentMethodCateogries() {
            return paymentMethodCateogries;
        }

        public String getPurchaseCountry() {
            return purchaseCountry;
        }

        public String getPurchaseCurrency() {
            return purchaseCurrency;
        }

        public JsonObject getShippingAddress() {
            return shippingAddress;
        }
    }
}
