
package au.com.dealsdirect.data.network.model.contacthistory;

import com.google.android.gms.common.api.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

public class GetContactHistoryResponse {

    public static class ResponseValue {
        public Response d;

        public static class Response {
            @SerializedName("IsAuthenticated")
            @Expose
            private Boolean isAuthenticated;
            @SerializedName("List")
            @Expose
            private java.util.List<List> list = null;
            @SerializedName("Result")
            @Expose
            private Boolean result;
            @SerializedName("Message")
            @Expose
            private String message;

        }
        public Boolean getIsAuthenticated() {
            return d.isAuthenticated;
        }

        public void setIsAuthenticated(Boolean isAuthenticated) {
            d.isAuthenticated = isAuthenticated;
        }

        public java.util.List<List> getList() {
            return d.list;
        }

        public void setList(java.util.List<List> list) {
            d.list = list;
        }

        public Boolean getResult() {
            return d.result;
        }

        public void setResult(Boolean result) {
            d.result = result;
        }

        public String getMessage() {
            return d.message;
        }

        public void setMessage(String message) {
            d.message = message;
        }
    }
}
