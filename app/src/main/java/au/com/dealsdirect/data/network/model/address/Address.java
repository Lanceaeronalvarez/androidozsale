package au.com.dealsdirect.data.network.model.address;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * dp Created by Admin on 11/9/16.
 */
public class Address {


    @NonNull
    private final String mId;

    @Nullable
    private final String mAddress;

    @Nullable
    private final String mSuburb;

    @Nullable
    private final String mState;

    @Nullable
    private final String mPostCode;


    public Address(@Nullable String address, @Nullable String suburb,
                   @Nullable String state, @Nullable String postcode) {

        this(UUID.randomUUID().toString(), address, suburb, state, postcode);
    }


    public Address(@Nullable String id, @Nullable String address, @Nullable String suburb,
                   @NonNull String state, @NonNull String postCode) {
        mId = id;
        mAddress = address;
        mSuburb = suburb;
        mState = state;
        mPostCode = postCode;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getAddress() {
        return mAddress;
    }


    @Nullable
    public String getSuburb() {
        return mSuburb;
    }


    @Nullable
    public String getState() {
        return mState;
    }


    @Nullable
    public String getPostCode() {
        return mPostCode;
    }


    @Nullable
    public String getAddressForList() {
        if (!Strings.isNullOrEmpty(mAddress)) {
            return mAddress;
        } else {
            return mState;
        }
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mAddress) &&
               Strings.isNullOrEmpty(mSuburb) &&
               Strings.isNullOrEmpty(mAddress) &&
               Strings.isNullOrEmpty(mPostCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equal(mId, address.mId) &&
               Objects.equal(mAddress, address.mAddress) &&
               Objects.equal(mState, address.mState)&&
               Objects.equal(mPostCode, address.mPostCode);
    }

//    @Override
//    public int hashCode() {
//        return Objects.hashCode(mId, mTitle, mDescription);
//    }

    public String getNewAddress() {
        return mAddress+", "+mSuburb+", "+mPostCode+", "+mState+", Philippines";
    }

}
