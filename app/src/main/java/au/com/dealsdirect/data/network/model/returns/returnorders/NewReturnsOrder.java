package au.com.dealsdirect.data.network.model.returns.returnorders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

/**
 * dp Created by Admin on 11/9/16.
 */

public class NewReturnsOrder {


    @NonNull
    private final String mId;

    private final String mProductImageUrl;

    @Nullable
    private final String mProductName;

    private final int mProductItemCount;

    private final String mProductOrderTotalCost;

    @Nullable
    private final String mProductOrderStatus;

    @Nullable
    private final String mProductOrderTracking;


    public NewReturnsOrder(
            @Nullable String productName,
            @Nullable String productImageUrl,
            int productItemCount,
            @Nullable String productOrderTotalCost,
            @Nullable String productOrderStatus,
            @Nullable String productOrderTracking) {

        this(UUID.randomUUID().toString(),
             productName,
             productImageUrl,
             productItemCount,
             productOrderTotalCost,
             productOrderStatus,
             productOrderTracking);
    }


    public NewReturnsOrder(
            String productId,
            @Nullable String productName,
            @Nullable String productImageUrl,
            @Nullable int productItemCount,
            @Nullable String productOrderTotalCost,
            @Nullable String productOrderStatus,
            @Nullable String productOrderTracking) {

        mId = productId;
        mProductName = productName;
        mProductImageUrl = productImageUrl;
        mProductItemCount = productItemCount;
        mProductOrderTotalCost = productOrderTotalCost;
        mProductOrderStatus = productOrderStatus;
        mProductOrderTracking = productOrderTracking;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getProductName() {return mProductName;}

    @Nullable
    public String getProductImageUrl() {return mProductImageUrl;}

    public int getProductItemCount(){return mProductItemCount;}

    @Nullable
    public String getProductOrderTotalCost(){return mProductOrderTotalCost;}

    public String getProductOrderStatus(){ return mProductOrderStatus;}

    @Nullable
    public String getProductOrderTracking() {
        return mProductOrderTracking;
    }

}
