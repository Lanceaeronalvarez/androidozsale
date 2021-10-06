package au.com.dealsdirect.ui.controller.account;

import android.content.Context;

import java.util.List;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.account.model.AccountItem;
import au.com.dealsdirect.ui.controller.account.model.AccountOption;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface AccountMvpPresenter <V extends MvpView> extends MvpPresenter<V> {

    void loadAccountItems(List<AccountItem> accountItems);

    void onAccountItemClick(Context context, AccountOption option);

    void setMultiCountry(boolean isMultiCountry);

    boolean isMultiCountry();

    boolean isMultiLanguage();

    boolean isAuthorized();

    boolean shouldShowStrictConsent();

    boolean isOurpayEnabled();

    boolean isGoogleAdsEnabled();

    boolean willScreenChange(Context context, String option);
}
