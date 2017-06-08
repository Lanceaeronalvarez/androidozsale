package au.com.dealsdirect.data.network.model.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 6/7/17.
 */

public class GetPublicSalesBannerResponse {

    @SerializedName("d")
    @Expose
    private Banner banner;

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner d) {
        this.banner = d;
    }


    public class Banner {

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

        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("Sales")
        @Expose
        private java.util.List<Sale> sales = null;
        @SerializedName("NextGroup")
        @Expose
        private Integer nextGroup;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public java.util.List<Sale> getSales() {
            return sales;
        }

        public void setSales(java.util.List<Sale> sales) {
            this.sales = sales;
        }

        public Integer getNextGroup() {
            return nextGroup;
        }

        public void setNextGroup(Integer nextGroup) {
            this.nextGroup = nextGroup;
        }

    }


    public class Sale {

        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("Desc")
        @Expose
        private String desc;
        @SerializedName("PercentOff")
        @Expose
        private Integer percentOff;
        @SerializedName("Start")
        @Expose
        private String start;
        @SerializedName("End")
        @Expose
        private String end;
        @SerializedName("SkipItemsList")
        @Expose
        private Boolean skipItemsList;
        @SerializedName("UseProductCombine")
        @Expose
        private Boolean useProductCombine;
        @SerializedName("ImageID")
        @Expose
        private String imageID;
        @SerializedName("File")
        @Expose
        private String file;

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

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Integer getPercentOff() {
            return percentOff;
        }

        public void setPercentOff(Integer percentOff) {
            this.percentOff = percentOff;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public Boolean getSkipItemsList() {
            return skipItemsList;
        }

        public void setSkipItemsList(Boolean skipItemsList) {
            this.skipItemsList = skipItemsList;
        }

        public Boolean getUseProductCombine() {
            return useProductCombine;
        }

        public void setUseProductCombine(Boolean useProductCombine) {
            this.useProductCombine = useProductCombine;
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
    }

}
