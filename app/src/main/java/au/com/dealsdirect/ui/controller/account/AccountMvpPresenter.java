package au.com.dealsdirect.ui.controller.account;

import android.content.Context;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.account.model.AccountOption;

public interface AccountMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void onAccountItemClick(Context context, AccountOption option);

    void setMultiCountry(boolean isMultiCountry);

    boolean isMultiCountry();

    boolean isMultiLanguage();

    boolean isAuthorized();

    boolean shouldShowStrictConsent();

    boolean isGoogleAdsEnabled();

    boolean willScreenChange(Context context, String option);
}
