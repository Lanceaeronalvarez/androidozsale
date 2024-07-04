package au.com.dealsdirect.data.network.model.zippay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class ZipCreatePublicChargeRequest {
   @SerializedName("countryID")
   @Expose
   private String countryId;

   @SerializedName("languageID")
   @Expose
   private String languageId;

   @SerializedName("data")
   @Expose
   private Map<String, Object> data;

   public ZipCreatePublicChargeRequest(String countryId, String languageId, Map<String, Object> data) {
      this.countryId = countryId;
      this.languageId = languageId;
      this.data = data;
   }
}
