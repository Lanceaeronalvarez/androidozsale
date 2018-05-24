package au.com.dealsdirect.ui.controller.account;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.account.model.AccountItem;
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

        if (getMvpView().isChangeInProgress()) {
            return;
        }

        if (getDataManager().isAuthorized()) {
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
            }

        } else {

            if (option.equals(context.getString(R.string.account_language))) {
                getMvpView().showLanguage();
            } else if (option.equals(context.getString(R.string.account_about_us))) {
                getMvpView().showLegalities("aboutus", option);
            } else if (option.equals(context.getString(R.string.account_privacy))) {
                getMvpView().showLegalities("PrivacyPolicy_Text", option);
            } else if (option.equals(context.getString(R.string.account_tnc))) {
                getMvpView().showLegalities("TermsAndConditions_Text", option);
            } else if (option.equals(context.getString(R.string.account_contact_us))) {
                getMvpView().showContactUs();
            } else if (option.equals(context.getString(R.string.account_country))) {
                getMvpView().showCountry();
            } else if (option.equals(context.getString(R.string.account_invite_friend))) {
                getMvpView().showInviteAFriend();
            } else if (option.equals(context.getString(R.string.account_tutorial))) {
                getMvpView().showTutorial();
            } else if (option.equals(context.getString(R.string.account_logout))) {
                getMvpView().triggerLogout();
            } else {
                getMvpView().triggerLogin(option);
            }
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
    public void loadAccountItems(List<AccountItem> accountItems) {
        getMvpView().showAccountItems(accountItems);
    }


}
