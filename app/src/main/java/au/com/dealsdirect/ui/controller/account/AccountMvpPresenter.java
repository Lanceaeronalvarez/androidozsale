package au.com.dealsdirect.ui.controller.account;

import java.util.ArrayList;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface AccountMvpPresenter <V extends MvpView> extends MvpPresenter<V> {

    void loadAccountItems(ArrayList<String> items, int[] images);

    void onAccountItemClick(String option);

    boolean isAuthorized();
}
