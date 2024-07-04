package au.com.dealsdirect.data.network.model.zippay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZipCreateChargeRequest {
   @SerializedName("countryID")
   @Expose
   private String countryId;

   @SerializedName("languageID")
   @Expose
   private String languageId;

   @SerializedName("data")
   @Expose
   private Data data;

   public ZipCreateChargeRequest(String countryId, String languageId, String checkoutId, String zipOrderId, String paymentStatus) {
      this.countryId = countryId;
      this.languageId = languageId;
      this.data = new Data(checkoutId, zipOrderId, paymentStatus);
   }

   public static class Data {
      @SerializedName("checkoutId")
      @Expose
      private String checkoutId;
      @SerializedName("zipOrderId")
      @Expose
      private String zipOrderId;
      @SerializedName("paymentStatus")
      @Expose
      private String paymentStatus;

      public Data(String checkoutId, String zipOrderId, String paymentStatus) {
         this.checkoutId = checkoutId;
         this.zipOrderId = zipOrderId;
         this.paymentStatus = paymentStatus;
      }
   }
}
