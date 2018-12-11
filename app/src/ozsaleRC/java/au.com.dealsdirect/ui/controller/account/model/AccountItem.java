package au.com.dealsdirect.ui.controller.account.model;

import java.util.List;

/**
 * Created by smartwave on 24/05/2018.
 */

public class AccountItem {
    long mId;
    String mTitle;
    List<AccountSubItem> mSubItems;

    public AccountItem(long mId, String mTitle, List<AccountSubItem> mSubItems) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mSubItems = mSubItems;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public List<AccountSubItem> getSubItems() {
        return mSubItems;
    }

    public void setSubItems(List<AccountSubItem> mSubItems) {
        this.mSubItems = mSubItems;
    }
    public long getId() {
        return mId;
    }

}
