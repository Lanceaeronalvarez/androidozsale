package au.com.dealsdirect.data.network.model.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.ArrayList;

/**
 * Created by smartwave on 07/01/2017.
 */

public class ApplyAddressResponse {

    public Response d;

    public static class Response extends LegacyBaseResponseValue {
        public Value Value;
    }

    static class Value{
        public ArrayList<Items> Items;
        public ArrayList<Vouchers> Vouchers;
        public Summary Summary;
        public DeliveryAddress DeliveryAddress;
        public ArrayList<DecorationInfoList> DecorationInfoList;
    }

    public static class Items{
        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("ItemID")
        @Expose
        private String itemID;
        @SerializedName("Item")
        @Expose
        private String item;
        @SerializedName("Size")
        @Expose
        private String size;
        @SerializedName("Qty")
        @Expose
        private int qty;
        @SerializedName("Price")
        @Expose
        private float price;
        @SerializedName("Subtotal")
        @Expose
        private float subtotal;
        @SerializedName("BrandID")
        @Expose
        private String brandID;
        @SerializedName("ImageID")
        @Expose
        private String imageID;
        @SerializedName("FileName")
        @Expose
        private String fileName;

        public String getID() {
            return iD;
        }

        public void setID(String iD) {
            this.iD = iD;
        }

        public String getItemID() {
            return itemID;
        }

        public void setItemID(String itemID) {
            this.itemID = itemID;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public float getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(float subtotal) {
            this.subtotal = subtotal;
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
    }


    static class Vouchers{
        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("Description")
        @Expose
        private String description;

        public String getID() {
            return iD;
        }

        public void setID(String iD) {
            this.iD = iD;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    static class Summary{
        @SerializedName("Subtotal")
        @Expose
        private double subtotal;
        @SerializedName("Delivery")
        @Expose
        private double delivery;
        @SerializedName("Discount")
        @Expose
        private double discount;
        @SerializedName("Total")
        @Expose
        private double total;
        @SerializedName("Tax")
        @Expose
        private double tax;

        public double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(float subtotal) {
            this.subtotal = subtotal;
        }

        public double getDelivery() {
            return delivery;
        }

        public void setDelivery(int delivery) {
            this.delivery = delivery;
        }

        public double getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public double getTax() {
            return tax;
        }

        public void setTax(int tax) {
            this.tax = tax;
        }
    }

    static class DeliveryAddress{
        @SerializedName("ID")
        @Expose
        private String iD;
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

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getSuburb() {
            return suburb;
        }

        public void setSuburb(String suburb) {
            this.suburb = suburb;
        }

        public String getPostcode() {
            return postcode;
        }

        public void setPostcode(String postcode) {
            this.postcode = postcode;
        }

        public String getAddressLines() {
            return addressLines;
        }

        public void setAddressLines(String addressLines) {
            this.addressLines = addressLines;
        }

        public boolean isAuthToLeave() {
            return authToLeave;
        }

        public void setAuthToLeave(boolean authToLeave) {
            this.authToLeave = authToLeave;
        }

        public String getAuthComment() {
            return authComment;
        }

        public void setAuthComment(String authComment) {
            this.authComment = authComment;
        }

        public String getAdditionalData() {
            return additionalData;
        }

        public void setAdditionalData(String additionalData) {
            this.additionalData = additionalData;
        }
    }
}
