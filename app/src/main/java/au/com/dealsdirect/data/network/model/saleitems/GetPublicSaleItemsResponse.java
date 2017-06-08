package au.com.dealsdirect.data.network.model.saleitems;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 6/8/17.
 */

public class GetPublicSaleItemsResponse {

    @SerializedName("d")
    @Expose
    private GetPublicSaleItemsObject getPublicSaleItemsObject;

    public GetPublicSaleItemsObject getGetPublicSaleItemsObject() {
        return getPublicSaleItemsObject;
    }

    public void setD(GetPublicSaleItemsObject getPublicSaleItemsObject) {
        this.getPublicSaleItemsObject = getPublicSaleItemsObject;
    }


    public class GetPublicSaleItemsObject {

        @SerializedName("List")
        @Expose
        private java.util.List<List> list = null;
        @SerializedName("Result")
        @Expose
        private Boolean result;
        @SerializedName("Message")
        @Expose
        private String message;

        public java.util.List<List> getList() {
            return list;
        }

        public void setList(java.util.List<List> list) {

            this.list = list;
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


    public class List {

        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("HtmlName")
        @Expose
        private String htmlName;
        @SerializedName("SubCategories")
        @Expose
        private java.util.List<SubCategory> subCategories = null;

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

        public String getHtmlName() {
            return htmlName;
        }

        public void setHtmlName(String htmlName) {
            this.htmlName = htmlName;
        }

        public java.util.List<SubCategory> getSubCategories() {
            return subCategories;
        }

        public void setSubCategories(java.util.List<SubCategory> subCategories) {
            this.subCategories = subCategories;
        }


    }


    public class SubCategory {

        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("HtmlName")
        @Expose
        private String htmlName;
        @SerializedName("Items")
        @Expose
        private java.util.List<Item> items = null;

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

        public String getHtmlName() {
            return htmlName;
        }

        public void setHtmlName(String htmlName) {
            this.htmlName = htmlName;
        }

        public java.util.List<Item> getItems() {
            return items;
        }

        public void setItems(java.util.List<Item> items) {
            this.items = items;
        }

    }


    public class Item {

        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("BrandID")
        @Expose
        private String brandID;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("BrandName")
        @Expose
        private String brandName;
        @SerializedName("ImageID")
        @Expose
        private String imageID;
        @SerializedName("File")
        @Expose
        private String file;
        @SerializedName("Width")
        @Expose
        private Integer width;
        @SerializedName("Height")
        @Expose
        private Integer height;
        @SerializedName("RP")
        @Expose
        private Double rP;
        @SerializedName("Price")
        @Expose
        private Double price;
        @SerializedName("Available")
        @Expose
        private Boolean available;
        @SerializedName("Sizes")
        @Expose
        private java.util.List<Size> sizes = null;

        public String getID() {
            return iD;
        }

        public void setID(String iD) {
            this.iD = iD;
        }

        public String getBrandID() {
            return brandID;
        }

        public void setBrandID(String brandID) {
            this.brandID = brandID;
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

        public String getImageID() {
            return imageID;
        }

        public void setImageID(String imageID) {
            this.imageID = imageID;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
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

        public Double getRP() {
            return rP;
        }

        public void setRP(Double rP) {
            this.rP = rP;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Boolean getAvailable() {
            return available;
        }

        public void setAvailable(Boolean available) {
            this.available = available;
        }

        public java.util.List<Size> getSizes() {
            return sizes;
        }

        public void setSizes(java.util.List<Size> sizes) {
            this.sizes = sizes;
        }

    }


    public class Size {

        @SerializedName("SizeID")
        @Expose
        private String sizeID;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("HtmlName")
        @Expose
        private String htmlName;

        public String getSizeID() {
            return sizeID;
        }

        public void setSizeID(String sizeID) {
            this.sizeID = sizeID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHtmlName() {
            return htmlName;
        }

        public void setHtmlName(String htmlName) {
            this.htmlName = htmlName;
        }

    }


}