package au.com.dealsdirect.data.network.model.returns.createreturn;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

/**
 * dp Created by Admin on 11/9/16.
 */

public class NewReturnsOrderDetail {


    @NonNull
    private final String mId;

    @Nullable
    private final String mProductImageUrl;

    @Nullable
    private final String mProductName;

    private final int mProductItemCount;

    private final int mProductItemCost;

    private final int mProductOrderTotalCost;

    @Nullable
    private final String mProductOrderReturnReason;

    public NewReturnsOrderDetail(
            @Nullable String productName,
            String productImageUrl,
            int productCost,
            int productTotalCost,
            int productItemCount,
            @Nullable String productReturnReason) {

        this(UUID.randomUUID().toString(),
             productName,
             productImageUrl,
             productCost,
             productTotalCost,
             productItemCount,
             productReturnReason);
    }


    public NewReturnsOrderDetail(
            String productId,
            String produckImageUrl,
            @Nullable String productName,
            int productItemCost,
            int productTotalCost,
            int productItemCount,
            @Nullable String productReturnReason) {

        mId = productId;
        mProductName = productName;
        mProductImageUrl = produckImageUrl;
        mProductItemCount = productItemCount;
        mProductItemCost = productItemCost;
        mProductOrderTotalCost = productTotalCost;
        mProductOrderReturnReason = productReturnReason;
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

    public int getProductOrderTotalCost(){ return mProductOrderTotalCost;}

    public int getProductOrderItemCost(){ return mProductItemCost; }

}