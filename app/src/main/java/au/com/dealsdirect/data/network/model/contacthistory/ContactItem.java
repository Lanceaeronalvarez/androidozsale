package au.com.dealsdirect.data.network.model.contacthistory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

/**
 * dp Created by Admin on 1/8/17.
 */
public class ContactItem {

    @NonNull
    private final String mId;

    private final String mContactItemTitle;

    private final String mContactItemDetail;

    private final String mContactTimeStamp;

    private final String mContactItemDate;

    public ContactItem(
            @Nullable String contactItemTitle,
            @NonNull String contactItemDetail,
            @NonNull String contactItemTimeStamp,
            @NonNull String contactItemDate) {

        this(UUID.randomUUID().toString(),
             contactItemTitle,
             contactItemDetail,
             contactItemTimeStamp,
             contactItemDate);
    }


    public ContactItem(
            String contactId,
            @Nullable String contactTitle,
            @NonNull String contactDetail,
            @NonNull String contactTimeStamp,
            @NonNull String contactDate) {

        mId = contactId;
        mContactItemTitle = contactTitle;
        mContactItemDetail = contactDetail;
        mContactTimeStamp = contactTimeStamp;
        mContactItemDate = contactDate;
    }

    @NonNull public String getmId() {
        return mId;
    }

    public String getmContactItemTitle() {
        return mContactItemTitle;
    }

    public String getmContactItemDetail() {
        return mContactItemDetail;
    }

    public String getmContactTimeStamp() {
        return mContactTimeStamp;
    }

    public String getmContactItemDate() {
        return mContactItemDate;
    }
}
