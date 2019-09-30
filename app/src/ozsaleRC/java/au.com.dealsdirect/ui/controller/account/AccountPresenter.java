package au.com.dealsdirect.ui.controller.account;

import android.content.Context;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.account.model.AccountItem;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CookieUtils;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/6/17.
 */

public class AccountPresenter<V extends AccountMvpView> extends BasePresenter<V> implements
        AccountMvpPresenter<V> {

    @Inject
    public AccountPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);

    }

    @Override
    public void onAccountItemClick(Context context, String option) {

        if (!isViewAttached() || getMvpView().isChangeInProgress()) {
            return;
        }

        if (!isNeedAuthorization(context, option)) {
            showNoAuthenticationRequiredScreens(context, option);
        } else {
            if (getDataManager().isAuthorized()) {
                showAuthenticationRequiredScreens(context, option);
            } else {
                if(getDataManager().isTablet()) {
                    showAuthenticationRequiredScreens(context,
                        context.getString(R.string.account_details));
                }
                getMvpView().triggerLogin(option);
            }
        }
    }

    private void showAuthenticationRequiredScreens(Context context, String option) {
        if (option.equals(context.getString(R.string.account_details))) {
            getMvpView().showMyDetailsController();
        } else if (option.equals(context.getString(R.string.account_addresses))) {
            getMvpView().showMyAddressesController();
        } else if (option.equals(context.getString(R.string.account_orders))) {
            getMvpView().showMyOrders();
        } else if (option.equals(context.getString(R.string.account_vouchers))) {
            getMvpView().showMyVouchers();
        } else if (option.equals(context.getString(R.string.account_returns))) {
            getMvpView().showMyReturns();
        } else if (option.equals(context.getString(R.string.account_payments))) {
            getMvpView().showMyPaymentsController();
        } else if (option.equals(context.getString(R.string.account_ourpay))) {
            getMvpView().showMyAccountsOurpay();
        } else if (option.equals(context.getString(R.string.account_select))) {
            getMvpView().showMyAccountsSelect();
        } else if (option.equals(context.getString(R.string.account_invite_friend))) {
            getMvpView().showInviteAFriend();
        }
    }

    private void showNoAuthenticationRequiredScreens(Context context, String option) {
        if (option.equals(context.getString(R.string.account_language))) {
            getMvpView().showLanguage();
        } else if (option.equals(context.getString(R.string.account_about_us))) {
            getMvpView().showLegalities(BundleKeys.TEMPLATE_KEY_ABOUT_US, option);
        } else if (option.equals(context.getString(R.string.account_privacy))) {
            getMvpView().showLegalities(BundleKeys.TEMPLATE_KEY_PRIVACY, option);
        } else if (option.equals(context.getString(R.string.account_tnc))) {
            getMvpView().showLegalities(BundleKeys.TEMPLATE_KEY_TNC, option);
        } else if (option.equals(context.getString(R.string.account_contact_us))) {
            getMvpView().showContactUs();
        } else if (option.equals(context.getString(R.string.account_country))) {
            getMvpView().showCountry();
        } else if (option.equals(context.getString(R.string.account_notification))) {
            getMvpView().showNotification();
        } else if (option.equals(context.getString(R.string.account_tutorial))) {
            getMvpView().showTutorial();
        } else if (option.equals(context.getString(R.string.account_logout))) {
            getMvpView().triggerLogout();
        }
    }

    @Override
    public void setMultiCountry(boolean isMultiCountry) {
        getDataManager().setIsMultiCountry(isMultiCountry);
    }

    @Override
    public boolean isMultiCountry() {
        return getDataManager().getIsMultiCountry();
    }

    @Override
    public boolean isMultiLanguage() {
        return getDataManager().getIsMultiLanguage();
    }

    @Override
    public boolean isAuthorized() {
        return getDataManager().isAuthorized();
    }

    @Override
    public boolean shouldShowStrictConsent() {
        return getDataManager().shouldShowStrictConsent();
    }

    @Override
    public boolean isOurpayEnabled() {
        return getDataManager().getIsOurpayDashboardEnabled();
    }

    @Override
    public boolean isGoogleAdsEnabled() {
        return getDataManager().isGoogleAdsEnabled();
    }

    @Override
    public boolean willScreenChange(Context context, String option) {
        return true;
    }

    @Override
    public void loadAccountItems(List<AccountItem> accountItems) {
        getMvpView().showAccountItems(accountItems);
    }

    private boolean isNeedAuthorization(Context context, String option) {
        Log.d("accounts", "option = " + option);
        return option.equals(context.getString(R.string.account_details)) ||
                option.equals(context.getString(R.string.account_addresses)) ||
                option.equals(context.getString(R.string.account_orders)) ||
                option.equals(context.getString(R.string.account_vouchers)) ||
                option.equals(context.getString(R.string.account_returns)) ||
                option.equals(context.getString(R.string.account_payments)) ||
                option.equals(context.getString(R.string.account_ourpay)) ||
                option.equals(context.getString(R.string.account_select)) ||
                option.equals(context.getString(R.string.account_invite_friend));
    }

}
