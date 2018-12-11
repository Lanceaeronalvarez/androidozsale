package au.com.dealsdirect.ui.controller.account.model;

/**
 * Created by smartwave on 24/05/2018.
 */

public class AccountSubItem {
    long mId;
    String mTitle;

    public AccountSubItem(long mId, String mTitle) {
        this.mId = mId;
        this.mTitle = mTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public long getId() {
        return mId;
    }
}
