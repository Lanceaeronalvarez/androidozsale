
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.List;

public class GetVouchersResponse {
    private Response d;

    public static class Response extends LegacyBaseResponseValue{

        @SerializedName("List")
        @Expose
        private List<VoucherList> list = null;

        public List<VoucherList> getList() {
            return list;
        }

        public void setList(List<VoucherList> list) {
            this.list = list;
        }

    }

    public Response getValue() {
        return d;
    }

    public static class VoucherList{
        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("Description")
        @Expose
        private String description;

        public String getiD() {
            return iD;
        }

        public String getDescription() {
            return description;
        }
    }

}
