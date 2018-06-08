package au.com.dealsdirect.data.network.model.checkout;

/**
 * Created by smartwave on 30/05/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.List;

public class GetDeliveryServicePackageDetails {

    public static class RequestValue{
        @SerializedName("countryID")
        @Expose
        private String countryId;
        @SerializedName("languageID")
        @Expose
        private String languageId;

        public RequestValue(String countryId, String languageId) {
            this.countryId = countryId;
            this.languageId = languageId;
        }
    }

    public static class ResponseValue{
        @SerializedName("d")
        @Expose
        private D d;

        public D getD() {
            return d;
        }

        public void setD(D d) {
            this.d = d;
        }

        public static class D extends LegacyBaseResponseValue {
            @SerializedName("Value")
            @Expose
            private List<Value> value = null;

            public List<Value> getValue() {
                return value;
            }

            public void setValue(List<Value> value) {
                this.value = value;
            }
        }

        public static class Value {

            @SerializedName("ID")
            @Expose
            private String iD;
            @SerializedName("Name")
            @Expose
            private String name;
            @SerializedName("Amount")
            @Expose
            private Double amount;
            @SerializedName("Count")
            @Expose
            private Integer count;

            private transient boolean isSelected = false;

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

            public Double getAmount() {
                return amount;
            }

            public void setAmount(Double amount) {
                this.amount = amount;
            }

            public Integer getCount() {
                return count;
            }

            public void setCount(Integer count) {
                this.count = count;
            }

            public boolean getSelected() {
                return isSelected;
            }

            public void setSelected(boolean selected) {
                isSelected = selected;
            }
        }
    }
}