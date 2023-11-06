package au.com.dealsdirect.data.network.model.category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetCategoryTreeRequest {
   @SerializedName("platform")
   @Expose
   private String platform = "android";
   public String getPlatform() {
      return platform;
   }
   public void setPlatform(String platform) {
      this.platform = platform;
   }
}
