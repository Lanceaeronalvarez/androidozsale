
package au.com.dealsdirect.data.network.model.forgotpassword;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForgotPasswordResponseBody {

    @SerializedName("d")
    @Expose
    private ForgotPasswordResponse forgotPasswordResponse;

    public ForgotPasswordResponse getForgotPasswordResponse() {
        return forgotPasswordResponse;
    }

    public void setForgotPasswordResponse(ForgotPasswordResponse forgotPasswordResponse) {
        this.forgotPasswordResponse = forgotPasswordResponse;
    }

}
