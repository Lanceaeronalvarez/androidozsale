package au.com.dealsdirect.ui.controller.account;

import android.content.Context;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.account.model.AccountOption;
import au.com.dealsdirect.utils.BundleKeys;
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
    public void onAccountItemClick(Context context, AccountOption option) {

        if (!isViewAttached() || getMvpView().isChangeInProgress()) {
            return;
        }

        List<AccountAction> actions = new LinkedList<>();

        if (option == null || !option.isNeedsAuthentication() || isAuthorized()) {
            if (option != null) {
                actions.add(accountActions.get(option));
            }
        } else {
            actions.add(() -> getMvpView().triggerLogin(option));
            if (isTablet()) {
                actions.add(() -> getMvpView().showMyDetailsController());
            }
        }

        for (AccountAction action : actions) {
            action.action();
        }
    }

    private HashMap<AccountOption, AccountAction> accountActions = new HashMap<AccountOption, AccountAction>() {{
        put(AccountOption.MYDETAILS, () -> getMvpView().showMyDetailsController());
        put(AccountOption.ADDRESSES, () -> getMvpView().showMyAddressesController());
        put(AccountOption.ORDERS, () -> getMvpView().showMyOrders());
        put(AccountOption.VOUCHERS, () -> getMvpView().showMyVouchers());
        put(AccountOption.RETURNS, () -> getMvpView().showMyReturns());
        put(AccountOption.RETURNSPOLICY, () -> getMvpView().showReturnsPolicy());
        put(AccountOption.CONTACTUS, () -> getMvpView().showContactUs());
        put(AccountOption.PAYMENTS, () -> getMvpView().showMyPaymentsController());
        put(AccountOption.INVITEFRIEND, () -> getMvpView().showInviteAFriend());
        put(AccountOption.LANGUAGE, () -> getMvpView().showLanguage());
        put(AccountOption.ABOUTUS, () -> getMvpView().showLegalities(BundleKeys.TEMPLATE_KEY_ABOUT_US, AccountOption.ABOUTUS));
        put(AccountOption.PRIVACYPOLICY, () -> getMvpView().showLegalities(BundleKeys.TEMPLATE_KEY_PRIVACY, AccountOption.PRIVACYPOLICY));
        put(AccountOption.TERMSANDCONDITIONS, () -> getMvpView().showLegalities(BundleKeys.TEMPLATE_KEY_TNC, AccountOption.TERMSANDCONDITIONS));
        put(AccountOption.COUNTRY, () -> getMvpView().showCountry());
        put(AccountOption.NOTIFICATIONS, () -> getMvpView().showNotification());
        put(AccountOption.TUTORIAL, () -> getMvpView().showTutorial());
        put(AccountOption.LOGOUT, () -> getMvpView().triggerLogout());
        put(AccountOption.INFORMATION, () -> getMvpView().showInformationMenu());
    }};

    private interface AccountAction {
        void action();
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
    public boolean isGoogleAdsEnabled() {
        return getDataManager().isGoogleAdsEnabled();
    }

    @Override
    public boolean willScreenChange(Context context, String option) {
        return true;
    }
}
