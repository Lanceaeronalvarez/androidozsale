package au.com.dealsdirect.data.network.model.returns.createreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MTC on 2019-07-25.
 */
public class ReturnReceivedRequest {

    @SerializedName("return_id")
    @Expose
    private String returnId;
    @SerializedName("satisfaction")
    @Expose
    private Integer satisfaction;

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public Integer getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(Integer satisfaction) {
        this.satisfaction = satisfaction;
    }

    public Map<String, Integer> getSatisfactionMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("global", satisfaction);
        return map;
    }
}
