package au.com.dealsdirect.data.network.model.zippay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZipPayCreateNzOrderRequest {
   @SerializedName("countryID")
   @Expose
   private String countryId;

   @SerializedName("languageID")
   @Expose
   private String languageId;

   @SerializedName("redirectUrl")
   @Expose
   private String redirectUrl;

   public ZipPayCreateNzOrderRequest(String countryId, String languageId, String redirectUrl) {
      this.countryId = countryId;
      this.languageId = languageId;
      this.redirectUrl = redirectUrl;
   }
}

