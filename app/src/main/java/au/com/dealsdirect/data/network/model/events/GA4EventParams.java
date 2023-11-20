package au.com.dealsdirect.data.network.model.events;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GA4EventParams {
    @SerializedName("currency")
    @Expose
    private String currency = null;
    @SerializedName("value")
    @Expose
    private Double value = null;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    public GA4EventParams() {
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        if (currency != null) {
            bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        }
        if (value != null) {
            bundle.putDouble(FirebaseAnalytics.Param.VALUE, value);
        }
        if (items != null) {
            ArrayList<Bundle> itemBundles = new ArrayList<>();
            for (Item item : items) {
                itemBundles.add(item.toBundle());
            }

            bundle.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, itemBundles);
        }

        return bundle;
    }

    public static class Item {
        @SerializedName("item_id")
        @Expose
        private String itemId = null;
        @SerializedName("item_name")
        @Expose
        private String itemName = null;
        @SerializedName("affiliation")
        @Expose
        private String affiliation = "Android";
        @SerializedName("price")
        @Expose
        private Double price = null;
        @SerializedName("quantity")
        @Expose
        private Integer quantity = null;
        @SerializedName("item_brand")
        @Expose
        private String itemBrand = null;
        @SerializedName("item_category")
        @Expose
        private String itemCategory;
        @SerializedName("item_category2")
        @Expose
        private String itemCategory2;
        @SerializedName("item_category3")
        @Expose
        private String itemCategory3;
        @SerializedName("item_category4")
        @Expose
        private String itemCategory4;
        @SerializedName("item_category5")
        @Expose
        private String itemCategory5;
        @SerializedName("discount")
        @Expose
        private Double discount;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getAffiliation() {
            return affiliation;
        }

        public void setAffiliation(String affiliation) {
            this.affiliation = affiliation;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getItemBrand() {
            return itemBrand;
        }

        public void setItemBrand(String itemBrand) {
            this.itemBrand = itemBrand;
        }

        public String getItemCategory() {
            return itemCategory;
        }

        public void setItemCategory(String itemCategory) {
            this.itemCategory = itemCategory;
        }

        public String getItemCategory2() {
            return itemCategory2;
        }

        public void setItemCategory2(String itemCategory2) {
            this.itemCategory2 = itemCategory2;
        }

        public String getItemCategory3() {
            return itemCategory3;
        }

        public void setItemCategory3(String itemCategory3) {
            this.itemCategory3 = itemCategory3;
        }

        public String getItemCategory4() {
            return itemCategory4;
        }

        public void setItemCategory4(String itemCategory4) {
            this.itemCategory4 = itemCategory4;
        }

        public String getItemCategory5() {
            return itemCategory5;
        }

        public void setItemCategory5(String itemCategory5) {
            this.itemCategory5 = itemCategory5;
        }

        public void setItemCategory(String itemCategory, int index) {
            switch (index) {
                case 0:
                    this.itemCategory = itemCategory;
                    break;
                case 1:
                    this.itemCategory2 = itemCategory;
                    break;
                case 2:
                    this.itemCategory3 = itemCategory;
                    break;
                case 3:
                    this.itemCategory4 = itemCategory;
                    break;
                case 4:
                    this.itemCategory5 = itemCategory;
                    break;
                default:
                    break;
            }
        }

        public String getItemCategory(int index) {
            switch (index) {
                case 0:
                    return itemCategory;
                case 1:
                    return itemCategory2;
                case 2:
                    return itemCategory3;
                case 3:
                    return itemCategory4;
                case 4:
                    return itemCategory5;
                default:
                    return null;
            }
        }

        public void setItemCategories(List<String> categories) {
            for (int i = 0; i < 5; i++) {
                setItemCategory(null, i);
            }
            if (categories == null) {
                return;
            }
            for (int i = 0; i < categories.size(); i++) {
                setItemCategory(categories.get(i), i);
            }
        }

        public Double getDiscount() {
            return discount;
        }

        public void setDiscount(Double discount) {
            this.discount = discount;
        }

        public Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
            if (price != null) {
                bundle.putDouble(FirebaseAnalytics.Param.PRICE, price);
            }
            if (quantity != null) {
                bundle.putInt(FirebaseAnalytics.Param.QUANTITY, quantity);
            }
            if (affiliation != null) {
                bundle.putString(FirebaseAnalytics.Param.AFFILIATION, affiliation);
            }
            if (itemBrand != null) {
                bundle.putString(FirebaseAnalytics.Param.ITEM_BRAND, itemBrand);
            }
            if (itemCategory != null) {
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, itemCategory);
            }
            if (itemCategory2 != null) {
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY2, itemCategory2);
            }
            if (itemCategory3 != null) {
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY3, itemCategory3);
            }
            if (itemCategory4 != null) {
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY4, itemCategory4);
            }
            if (itemCategory5 != null) {
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY5, itemCategory5);
            }
            if (discount != null) {
                bundle.putDouble(FirebaseAnalytics.Param.DISCOUNT, discount);
            }
            return bundle;
        }
    }

    public static class GA4AddPaymentInfoParams extends GA4EventParams {
        @SerializedName("payment_type")
        @Expose
        private String paymentType = null;

        @SerializedName("coupon")
        @Expose
        private String coupon = null;

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public String getCoupon() {
            return coupon;
        }

        public void setCoupon(String coupon) {
            this.coupon = coupon;
        }

        @Override
        public Bundle toBundle() {
            Bundle bundle = super.toBundle();
            if (paymentType != null) {
                bundle.putString(FirebaseAnalytics.Param.PAYMENT_TYPE, paymentType);
            }
            if (coupon != null) {
                bundle.putString(FirebaseAnalytics.Param.COUPON, coupon);
            }
            return bundle;
        }
    }

    public static class GA4AddShippingInfoParams extends GA4EventParams {
        @SerializedName("shipping_tier")
        @Expose
        private String shippingTier = null;

        @SerializedName("coupon")
        @Expose
        private String coupon = null;

        public String getShippingTier() {
            return shippingTier;
        }

        public void setShippingTier(String shippingTier) {
            this.shippingTier = shippingTier;
        }

        public String getCoupon() {
            return coupon;
        }

        public void setCoupon(String coupon) {
            this.coupon = coupon;
        }

        @Override
        public Bundle toBundle() {
            Bundle bundle = super.toBundle();
            if (shippingTier != null) {
                bundle.putString(FirebaseAnalytics.Param.SHIPPING_TIER, shippingTier);
            }
            if (coupon != null) {
                bundle.putString(FirebaseAnalytics.Param.COUPON, coupon);
            }
            return bundle;
        }
    }

    public static class GA4AddToCartParams extends GA4EventParams {
    }

    public static class GA4AddToWishlistParams extends GA4EventParams {
    }

    public static class GA4BeginCheckoutParams extends GA4EventParams {
        @SerializedName("coupon")
        @Expose
        private String coupon = null;

        public String getCoupon() {
            return coupon;
        }

        public void setCoupon(String coupon) {
            this.coupon = coupon;
        }

        @Override
        public Bundle toBundle() {
            Bundle bundle = super.toBundle();
            if (coupon != null) {
                bundle.putString(FirebaseAnalytics.Param.COUPON, coupon);
            }
            return bundle;
        }
    }

    public static class GA4PurchaseParams extends GA4EventParams {
        @SerializedName("transaction_id")
        @Expose
        private String transactionId = null;

        @SerializedName("coupon")
        @Expose
        private String coupon = null;

        @SerializedName("shipping")
        @Expose
        private Double shipping = null;

        @SerializedName("tax")
        @Expose
        private Double tax = null;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getCoupon() {
            return coupon;
        }

        public void setCoupon(String coupon) {
            this.coupon = coupon;
        }

        public Double getShipping() {
            return shipping;
        }

        public void setShipping(Double shipping) {
            this.shipping = shipping;
        }

        public Double getTax() {
            return tax;
        }

        public void setTax(Double tax) {
            this.tax = tax;
        }

        @Override
        public Bundle toBundle() {
            Bundle bundle = super.toBundle();
            if (transactionId != null) {
                bundle.putString(FirebaseAnalytics.Param.TRANSACTION_ID, transactionId);
            }
            if (coupon != null) {
                bundle.putString(FirebaseAnalytics.Param.COUPON, coupon);
            }
            if (shipping != null) {
                bundle.putDouble(FirebaseAnalytics.Param.SHIPPING, shipping);
            }
            if (tax != null) {
                bundle.putDouble(FirebaseAnalytics.Param.TAX, tax);
            }
            return bundle;
        }
    }

    public static class GA4RemoveFromCartParams extends GA4EventParams {

    }

    public static class GA4SelectItemParams extends GA4EventParams {
        @SerializedName("item_list_id")
        @Expose
        private String itemListId = null;
        @SerializedName("item_list_name")
        @Expose
        private String itemListName = null;

        public String getItemListId() {
            return itemListId;
        }

        public void setItemListId(String itemListId) {
            this.itemListId = itemListId;
        }

        public String getItemListName() {
            return itemListName;
        }

        public void setItemListName(String itemListName) {
            this.itemListName = itemListName;
        }

        @Override
        public Bundle toBundle() {
            Bundle bundle = super.toBundle();
            if (itemListId != null) {
                bundle.putString(FirebaseAnalytics.Param.ITEM_LIST_ID, itemListId);
            }
            if (itemListName != null) {
                bundle.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, itemListName);
            }
            return bundle;
        }
    }

    public static class GA4ViewCartParams extends GA4EventParams {

    }

    public static class GA4ViewItemParams extends GA4EventParams {

    }

    public static class GA4ViewItemListParams extends GA4EventParams {
        @SerializedName("item_list_id")
        @Expose
        private String itemListId = null;
        @SerializedName("item_list_name")
        @Expose
        private String itemListName = null;

        public String getItemListId() {
            return itemListId;
        }

        public void setItemListId(String itemListId) {
            this.itemListId = itemListId;
        }

        public String getItemListName() {
            return itemListName;
        }

        public void setItemListName(String itemListName) {
            this.itemListName = itemListName;
        }

        @Override
        public Bundle toBundle() {
            Bundle bundle = super.toBundle();
            if (itemListId != null) {
                bundle.putString(FirebaseAnalytics.Param.ITEM_LIST_ID, itemListId);
            }
            if (itemListName != null) {
                bundle.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, itemListName);
            }
            return bundle;
        }
    }
}
