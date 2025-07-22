package au.com.dealsdirect.ui.controller.saleitems;

import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;

public class SaleItemSkeletonPlaceholder extends SaleItemProduct {

    public SaleItemSkeletonPlaceholder() {
        this(null, null, null, null, null, false, false);
    }

    public SaleItemSkeletonPlaceholder(
            String imageURL,
            String seoIdentifierId,
            String productName,
            String productBrand,
            String price,
            boolean isFreeDelivery,
            Boolean isSoldOut) {
        super(imageURL, seoIdentifierId, productName, productBrand, price, isFreeDelivery, isSoldOut);
    }
}
