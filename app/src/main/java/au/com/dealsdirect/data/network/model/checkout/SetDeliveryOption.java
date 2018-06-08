package au.com.dealsdirect.data.network.model.checkout;

/**
 * Created by smartwave on 29/05/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SetDeliveryOption {

    @SerializedName("optionParameters")
    @Expose
    private OptionParameters optionParameters;
    @SerializedName("imageSize")
    @Expose
    private Integer imageSize;
    @SerializedName("countryID")
    @Expose
    private String countryId;
    @SerializedName("languageID")
    @Expose
    private String languageId;

    public OptionParameters getOptionParameters() {
        return optionParameters;
    }

    public void setOptionParameters(OptionParameters optionParameters) {
        this.optionParameters = optionParameters;
    }

    public Integer getImageSize() {
        return imageSize;
    }

    public void setImageSize(Integer imageSize) {
        this.imageSize = imageSize;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public static class OptionParameters {

        @SerializedName("deliveryAddressID")
        @Expose
        private String deliveryAddressID;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("deliveryoption")
        @Expose
        private String deliveryoption;
        @SerializedName("servicepackagedetailid")
        @Expose
        private String servicepackagedetailid;

        public OptionParameters(String deliveryAddressID, String type, String deliveryoption, String servicepackagedetailid) {
            this.deliveryAddressID = deliveryAddressID;
            this.type = type;
            this.deliveryoption = deliveryoption;
            this.servicepackagedetailid = servicepackagedetailid;
        }

        public String getDeliveryAddressID() {
            return deliveryAddressID;
        }

        public void setDeliveryAddressID(String deliveryAddressID) {
            this.deliveryAddressID = deliveryAddressID;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDeliveryoption() {
            return deliveryoption;
        }

        public void setDeliveryoption(String deliveryoption) {
            this.deliveryoption = deliveryoption;
        }

        public String getServicepackagedetailid() {
            return servicepackagedetailid;
        }

        public void setServicepackagedetailid(String servicepackagedetailid) {
            this.servicepackagedetailid = servicepackagedetailid;
        }
    }
}
