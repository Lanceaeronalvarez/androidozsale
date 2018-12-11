package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;

/**
 * Created by smartwave on 29/05/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryServicePackageDetail {
        @SerializedName(value = "Name", alternate = {"name"})
        @Expose
        private String name;
        @SerializedName(value = "InitialCount", alternate = {"initialCount"})
        @Expose
        private Integer initialCount;
        @SerializedName(value = "CurrentCount", alternate = {"currentCount"})
        @Expose
        private Integer currentCount;
        @SerializedName(value = "RemainingCount", alternate = {"remainingCount"})
        @Expose
        private Integer remainingCount;
        @SerializedName(value = "ExpiryDate", alternate = {"expiryDate"})
        @Expose
        private String expiryDate;
        @SerializedName(value = "Amount", alternate = {"amount"})
        @Expose
        private Double amount;
        @SerializedName(value = "Purchased", alternate = {"purchased"})
        @Expose
        private Boolean purchased;
        @SerializedName(value = "DeliveryServicePackageDetailID", alternate = {"deliveryServicePackageDetailID"})
        @Expose
        private String deliveryServicePackageDetailID;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getInitialCount() {
            return initialCount;
        }

        public void setInitialCount(Integer initialCount) {
            this.initialCount = initialCount;
        }

        public Integer getCurrentCount() {
            return currentCount;
        }

        public void setCurrentCount(Integer currentCount) {
            this.currentCount = currentCount;
        }

        public Integer getRemainingCount() {
            return remainingCount;
        }

        public void setRemainingCount(Integer remainingCount) {
            this.remainingCount = remainingCount;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public Boolean getPurchased() {
            return purchased;
        }

        public void setPurchased(Boolean purchased) {
            this.purchased = purchased;
        }

        public String getDeliveryServicePackageDetailID() {
            return deliveryServicePackageDetailID;
        }

        public void setDeliveryServicePackageDetailID(String deliveryServicePackageDetailID) {
            this.deliveryServicePackageDetailID = deliveryServicePackageDetailID;
        }
}