package au.com.dealsdirect.data.network.model.saleitemdetails;

/**
 * Created by smartwave on 11/07/2017.
 */

public class AddToCartResponse {

    private final boolean Result;

    public AddToCartResponse(boolean val) {
        Result = val;
    }

    public boolean getResult() {
        return Result;
    }

}
