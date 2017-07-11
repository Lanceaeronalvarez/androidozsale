package au.com.dealsdirect.data.network.model.saleitemdetails;

/**
 * Created by smartwave on 11/07/2017.
 */

public class AddToCartRequest {
    private final String itemID;
    private final String saleID;
    private final String modificator;
    private final String languageID;
    private final String imageSize;
    private final String countryID;
    private final String sizeID;
    private final String quantity;

    public AddToCartRequest(String itemId, String saleId, String modificator,
                            String languageId, String imageSize, String countryId,
                            String sizeId, String quantity) {
        this.itemID = itemId;
        this.saleID = saleId;
        this.modificator = modificator;
        this.languageID = languageId;
        this.imageSize = imageSize;
        this.countryID = countryId;
        this.sizeID = sizeId;
        this.quantity = quantity;
    }
}
