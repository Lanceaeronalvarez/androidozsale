package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import au.com.dealsdirect.ui.base.MvpPresenter;

/**
 * dp Created by Admin on 6/21/17.
 */

public interface ViewContactHistoryMvpPresenter<V extends ViewContactHistoryMvpView>
        extends MvpPresenter<V> {

    void loadContactHistory(int contactId);
}
