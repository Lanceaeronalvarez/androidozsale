package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.os.Bundle;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.PhoneVerification;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Shipment;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value.GetOurPaySelect;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Voucher;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartResponse;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.JsonUtils;

public class CheckoutDetailsMapper {
    private Value sourceValue;

    private HashMap<String, Item> itemMap;
    private ArrayList<MappedShipment> mappedShipments;

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

    public Boolean getAgeRestricted() {
        return sourceValue.getAgeRestricted();
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

    public GetCurrentOrderOurpay getOurpay() {
        return sourceValue.getOurpay();
    }

    public GetOurPaySelect getOurPaySelect() {
        return sourceValue.getOurPaySelect();
    }

    public int getOurPaySelectTermsAndConditions() {
        return sourceValue.getOurPaySelectTermsAndConditions();
    }

    public Value.GetCurrentOrderAfterpay getAfterpay() {
        return sourceValue.getAfterpay();
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
}
