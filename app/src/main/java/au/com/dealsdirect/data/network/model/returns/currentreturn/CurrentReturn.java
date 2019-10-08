package au.com.dealsdirect.data.network.model.returns.currentreturn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

/**
 * dp Created by Admin on 6/29/17.
 */

public class CurrentReturn {


    @NonNull
    private final String mId;

    private final String mProductImageUrl;
    @Nullable
    private final String mProductName;

    @Nullable
    private final String mProductRequestNumber;

    private final boolean mIsProductReturnRequestApproved;

    @Nullable
    private final String mProductReturnRequestDate;

    @Nullable
    private final String mProductReturnStatus;

    @Nullable
    private final String mProductRAN;



    public CurrentReturn(
            @Nullable String productName,
            @Nullable String productImageUrl,
            @Nullable String productRequestNumber,
            @Nullable String productRequestDate,
            @Nullable Boolean isProductReturnRequestApproved,
            @Nullable String productRequestStatus,
            @Nullable String productRAN) {

        this(UUID.randomUUID().toString(),
                productName,
                productImageUrl,
                productRequestNumber,
                productRequestDate,
                isProductReturnRequestApproved,
                productRequestStatus,
                productRAN);
    }


    public CurrentReturn(
            String productId,
            @Nullable String productName,
            @Nullable String productImageUrl,
            @Nullable String productRequestNumber,
            @Nullable String productRequestDate,
            @Nullable Boolean isProductApproved,
            @Nullable String productRequestStatus,
            @Nullable String productRAN) {

        mId = productId;
        mProductName = productName;
        mProductImageUrl = productImageUrl;
        mProductRequestNumber = productRequestNumber;
        mProductReturnRequestDate = productRequestDate;
        mIsProductReturnRequestApproved = isProductApproved;
        mProductReturnStatus = productRequestStatus;
        mProductRAN = productRAN;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getProductName() {return mProductName;}

    @Nullable
    public String getProductImageUrl() {return mProductImageUrl;}

    @Nullable
    public String getProductRequestNumber(){return mProductRequestNumber;}

    @Nullable
    public String getProductRequestDate(){return mProductReturnRequestDate;}

    public boolean getIsProductReturnRequestApproved(){ return mIsProductReturnRequestApproved;}

    @Nullable
    public String getProductReturnStatus() {
        return mProductReturnStatus;
    }

    @Nullable
    public String getProductRAN() {
        return mProductRAN;
    }

}
