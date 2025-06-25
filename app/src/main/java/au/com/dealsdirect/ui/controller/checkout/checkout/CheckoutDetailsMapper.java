package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.PhoneVerification;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Shipment;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartResponse;
import au.com.dealsdirect.data.network.model.vouchers.Voucher;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.JsonUtils;

public class CheckoutDetailsMapper {

    private Value sourceValue;

    private HashMap<String, Item> itemMap;
    private ArrayList<MappedShipment> mappedShipments;
    private HashSet<PaymentOption> availablePaymentOptions;

    public CheckoutDetailsMapper(Value value) {
        this.sourceValue = value;

        init();
    }

    public CheckoutDetailsMapper(AddToCartResponse.Response responseValue) {
        this.sourceValue = responseValue.getValue();

        init();
    }

    public CheckoutDetailsMapper(GetCurrentOrder.ResponseValue responseValue) {
        this.sourceValue = responseValue.getD().getValue();

        init();
    }

    private void init() {
        itemMap = new HashMap<>();
        mappedShipments = new ArrayList<>();
        availablePaymentOptions = new HashSet<>();

        if (getItems() != null && getItems().size() > 0) {
            for (Item item : getItems()) {
                itemMap.put(item.getId(), item);
            }

            if (getShipments() != null && getShipments().size() > 0) {
                for (Shipment shipment : getShipments()) {
                    mappedShipments.add(new MappedShipment(shipment, getItemsAsMap()));
                }
            } else {
                // Display only items as is
                mappedShipments.add(new MappedShipment(getItems()));
            }
        }

        List<Value.PaymentOption> paymentOptions = sourceValue.getAvailablePaymentOptions();
        if (paymentOptions != null) {
            for (Value.PaymentOption paymentOption : paymentOptions) {
                String name = paymentOption.getName();
                if (name != null) {
                    availablePaymentOptions.add(PaymentOption.fromValue(name));
                }
            }
        }
    }

    public static CheckoutDetailsMapper decompress(byte[] compressed) {
        if (compressed == null || compressed.length == 0) {
            return null;
        }
        try {
            final int BUFFER_SIZE = 32;
            ByteArrayInputStream is = new ByteArrayInputStream(compressed);
            GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
            StringBuilder string = new StringBuilder();
            byte[] data = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = gis.read(data)) != -1) {
                string.append(new String(data, 0, bytesRead));
            }
            gis.close();
            is.close();
            return new CheckoutDetailsMapper(
                    JsonUtils.convertStringToObject(string.toString(),
                            Value.class));
        } catch (IOException ex) {
            return null;
        }
    }

    public BundleBuilder putInBundle(BundleBuilder bundleBuilder, String key) {
        return bundleBuilder.putByteArray(key, compress());
    }

    public void putInBundle(Bundle bundle, String key) {
        bundle.putByteArray(key, compress());
    }

    public byte[] compress() {
        try {
            String json = new Gson().toJson(sourceValue);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(json.length());
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(json.getBytes());
            gzip.close();
            byte[] compressed = bos.toByteArray();
            bos.close();
            return compressed;
        } catch (IOException exception) {
            return new byte[]{};
        }
    }

    public boolean isEmpty() {
        return sourceValue.isEmpty();
    }

    public String getNotificationMessage() {
        return sourceValue.getNotificationMessage();
    }

    public Boolean getPickupPointsEnabled() {
        return sourceValue.getPickupPointsEnabled();
    }

    public PhoneVerification getPhoneVerification() {
        return sourceValue.getPhoneVerification();
    }

    public Boolean getThreeDSecureRequired() {
        return sourceValue.getThreeDSecureRequired();
    }

    public String getLastPaymentMethod() {
        return sourceValue.getLastPaymentMethod();
    }

    public List<DecorationInfoList> getDecorationInfoList() {
        return sourceValue.getDecorationInfoList();
    }

    public DeliveryAddress getDeliveryAddress() {
        return sourceValue.getDeliveryAddress();
    }

    public Boolean isAgeRestricted() {
        return sourceValue.isAgeRestricted();
    }

    public Summary getSummary() {
        return sourceValue.getSummary();
    }

    public List<Voucher> getVouchers() {
        return sourceValue.getVouchers();
    }

    public Integer getItemsCount() {
        return sourceValue.getItemsCount();
    }

    public String getSaleID() {
        return sourceValue.getSaleID();
    }

    public List<DeliveryOption> getDeliveryOptions() {
        return sourceValue.getDeliveryOptions();
    }

    public DeliveryServicePackageDetail getDeliveryServicePackageDetail() {
        return sourceValue.getDeliveryServicePackageDetail();
    }

    public Set<PaymentOption> getAvailablePaymentOptions() {
        return availablePaymentOptions;
    }

    public Value.GetCurrentOrderAfterpay getAfterpay() {
        return sourceValue.getAfterpay();
    }

    public List<Value.PromoCode> getPromoCodeList() {
        return sourceValue.getPromoCodeList();
    }

    public List<Item> getItems() {
        return sourceValue.getItems();
    }

    public Map<String, Item> getItemsAsMap() {
        return itemMap;
    }

    public List<Shipment> getShipments() {
        return sourceValue.getShipments();
    }

    public List<MappedShipment> getMappedShipments() {
        return mappedShipments;
    }

    public static class MappedShipment extends Shipment {
        private ArrayList<Item> mappedItems = null;

        public MappedShipment(List<Item> source) {
            mappedItems = new ArrayList<>(source);
        }

        public MappedShipment(Shipment shipment, Map<String, Item> source) {
            setName(shipment.getName());
            setDeliveryPrice(shipment.getDeliveryPrice());
            setMinimumSpendForPromoPrice(shipment.getMinimumSpendForPromoPrice());
            setAmountToPromoPrice(shipment.getAmountToPromoPrice());
            setItems(shipment.getItems());
            setLocationFilterHash(shipment.getLocationFilterHash());
            setEstimateShipmentPostcode(shipment.getEstimateShipmentPostcode());
            setShippingAvailability(shipment.getShippingAvailability());
            mapItems(source);
        }

        public List<Item> getMappedItems() {
            return mappedItems;
        }

        private void mapItems(Map<String, Item> source) {
            mappedItems = new ArrayList<>();
            for (String id : getItems()) {
                Item item = source.get(id);
                if (item != null) {
                    mappedItems.add(item);
                }
            }
        }
    }

    public static class PaymentOption {
        public static final PaymentOption AFTERPAY = new PaymentOption("Afterpay");
        public static final PaymentOption BRAINTREE = new PaymentOption("BrainTree");
        public static final PaymentOption BRAINTREEPAYPAL = new PaymentOption("BrainTreePayPal");
        public static final PaymentOption MASTERPASSPAYMENT = new PaymentOption("MasterPassPayment");
        public static final PaymentOption VISACHECKOUT = new PaymentOption("VisaCheckout");
        public static final PaymentOption IPAY88PAYMENTS = new PaymentOption("IPay88Payments");
        public static final PaymentOption STRIPE = new PaymentOption("Stripe");
        public static final PaymentOption LATITUDEPAY = new PaymentOption("LatitudePay");
        public static final PaymentOption KLARNA = new PaymentOption("Klarna");

        public static final PaymentOption ZIPPAYAU = new PaymentOption("ZipPayAU");
        public static final PaymentOption ZIPPAYNZ = new PaymentOption("ZipPayNZ");

        private static final HashMap<String, PaymentOption> paymentOptions =
                new HashMap<String, PaymentOption>() {{
                    put("Afterpay".toLowerCase(), AFTERPAY);
                    put("BrainTree".toLowerCase(), BRAINTREE);
                    put("BrainTreePayPal".toLowerCase(), BRAINTREEPAYPAL);
                    put("MasterPassPayment".toLowerCase(), MASTERPASSPAYMENT);
                    put("VisaCheckoutBrainTree".toLowerCase(), VISACHECKOUT);
                    put("VisaCheckoutCyberSource".toLowerCase(), VISACHECKOUT);
                    put("IPay88Payments".toLowerCase(), IPAY88PAYMENTS);
                    put("Stripe".toLowerCase(), STRIPE);
                    put("LatitudePay".toLowerCase(), LATITUDEPAY);
                    put("Klarna".toLowerCase(), KLARNA);
                    put("ZipPayAU".toLowerCase(), ZIPPAYAU);
                    put("ZipPayNZ".toLowerCase(), ZIPPAYNZ);
                }};

        private String value;

        private PaymentOption(String value) {
            this.value = value;
        }

        public static PaymentOption fromValue(String value) {
            return paymentOptions.get(value.toLowerCase());
        }

        @NonNull
        public String toString() {
            return value;
        }
    }
}
