package au.com.dealsdirect.ui.controller.account;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface AccountMvpPresenter <V extends MvpView> extends MvpPresenter<V> {

    void loadAccountItems();

    void onAccountItemClick(String option);
}
