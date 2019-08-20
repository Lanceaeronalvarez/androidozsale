package au.com.dealsdirect.data.network.model.promoinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

public class PromoInfoResponse extends ArrayList<PromoInfoResponse.PromoInfo> {

    private static String KEY_PERCENTOFF = "PercentOff";
    static String KEY_PERCENTOFFTEXT = "PercentOffText";
    static String KEY_FREEDELIVERY = "FreeDelivery";
    static String KEY_ISFREEDELIVERY = "IsFreeDelivery";
    static String KEY_PRICE = "Price";
    static String KEY_PRICE_DISCOUNTED = "DiscountedPrice";
    static String KEY_AFTERPAYENABLED = "AfterPayEnabled";

    private HashMap<String, String> rearrangedDataStructure = null;

    private void rearrangeDataStructure() {
        if (rearrangedDataStructure != null) {
            return;
        }
        rearrangedDataStructure = new HashMap<>();
        for (PromoInfo promoInfo : this) {
            rearrangedDataStructure.put(promoInfo.name, promoInfo.value);
            if (promoInfo.discount != null && !promoInfo.discount.isEmpty()) {
                rearrangedDataStructure.put(KEY_PRICE_DISCOUNTED, promoInfo.discount);
            }
        }
    }

    public String getPercentOff() {
        rearrangeDataStructure();
        return rearrangedDataStructure.get(KEY_PERCENTOFF);
    }

    public String getPercentOffText() {
        rearrangeDataStructure();
        return rearrangedDataStructure.get(KEY_PERCENTOFFTEXT);
    }

    public boolean getFreeDelivery() {
        rearrangeDataStructure();
        String freeDelivery = rearrangedDataStructure.get(KEY_FREEDELIVERY);
        return freeDelivery != null && Boolean.getBoolean(freeDelivery);
    }

    public boolean getIsFreeDelivery() {
        rearrangeDataStructure();
        String isFreeDelivery = rearrangedDataStructure.get(KEY_FREEDELIVERY);
        return isFreeDelivery != null && Boolean.getBoolean(isFreeDelivery);
    }

    public String getPrice() {
        rearrangeDataStructure();
        return rearrangedDataStructure.get(KEY_PRICE);
    }

    public String getDiscountedPrice() {
        rearrangeDataStructure();
        return rearrangedDataStructure.get(KEY_PRICE_DISCOUNTED);
    }

    public boolean getAfterpayEnabled() {
        rearrangeDataStructure();
        String afterpayEnabled = rearrangedDataStructure.get(KEY_AFTERPAYENABLED);
        return afterpayEnabled != null && Boolean.parseBoolean(afterpayEnabled);

    }

    public class PromoInfo {
        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("value")
        @Expose
        private String value;

        @SerializedName("discount")
        @Expose
        private String discount;
    }
}
