package au.com.dealsdirect.ui.controller.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/6/17.
 */

public class AccountPresenter<V extends AccountMvpView> extends BasePresenter<V> implements
        AccountMvpPresenter<V>, Serializable {

    ArrayList<String> mAccountItems = new ArrayList<>(
            Arrays.asList(AccountItems.DETAILS,
                    AccountItems.ADDRESSES,
                    AccountItems.ORDERS,
                    AccountItems.PAYMENTS,
                    AccountItems.VOUCHERS,
                    AccountItems.RETURNS,
                    AccountItems.LANGUAGE,
                    AccountItems.COUNTRY,
                    AccountItems.TNC,
                    AccountItems.PRIVACY_POLICY,
                    AccountItems.ABOUT_US));

    ArrayList<Integer> mAccountImages = new ArrayList<>(Arrays.asList(
            R.drawable.bg_account_details,
            R.drawable.bg_account_address,
            R.drawable.bg_account_orders,
            R.drawable.bg_account_payments,
            R.drawable.bg_account_vouchers,
            R.drawable.bg_account_returns,
            R.drawable.bg_account_languages,
            R.drawable.bg_account_country,
            R.drawable.bg_account_terms_and_conditions,
            R.drawable.bg_account_privacy_policy,
            R.drawable.bg_account_about_us
    ));


    @Inject
    public AccountPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);

        if(!getDataManager().getIsMultiCountry()){
            mAccountItems.remove(AccountItems.COUNTRY);
            mAccountImages.remove(mAccountImages.indexOf(R.drawable.bg_account_country));
        }

        if(!getDataManager().getIsMultiLanguage()){
            mAccountItems.remove(AccountItems.LANGUAGE);
            mAccountImages.remove(mAccountImages.indexOf(R.drawable.bg_account_languages));
        }
    }

    @Override
    public void onAccountItemClick(String option) {

        if (getMvpView().isChangeInProgress()) {
            return;
        }

        if (getDataManager().isAuthorized() || (option.equals(AccountItems.LANGUAGE) ||
                option.equals(AccountItems.PRIVACY_POLICY) ||
                option.equalsIgnoreCase(AccountItems.TNC) ||
                option.equalsIgnoreCase(AccountItems.ABOUT_US) ||
                option.equalsIgnoreCase(AccountItems.TUTORIAL))) {

            switch (option) {
                case AccountItems.DETAILS:
                    getMvpView().showMyDetailsController();
                    break;
                case AccountItems.ADDRESSES:
                    getMvpView().showMyAddressesController();
                    break;
                case AccountItems.ORDERS:
                    getMvpView().showMyOrders();
                    break;
                case AccountItems.VOUCHERS:
                    getMvpView().showMyVouchers();
                    break;
                case AccountItems.RETURNS:
                    getMvpView().showMyReturns();
                    break;
                case AccountItems.PAYMENTS:
                    getMvpView().showMyPaymentsController();
                    break;
                case AccountItems.LANGUAGE:
                    getMvpView().showLanguage();
                    break;
                case AccountItems.ABOUT_US:
                    getMvpView().showLegalities("aboutus", option);
                    break;
                case AccountItems.PRIVACY_POLICY:
                    getMvpView().showLegalities("PrivacyPolicy_Text", option);
                    break;
                case AccountItems.TNC:
                    getMvpView().showLegalities("TermsAndConditions_Text", option);
                    break;
                case AccountItems.CONTACT_US:
                    getMvpView().showContactUs();
                    break;
                case AccountItems.INVITE_FRIEND:
                    getMvpView().showInviteAFriend();
                    break;
                case AccountItems.TUTORIAL:
                    getMvpView().showTutorial();
                    break;
                case AccountItems.LOGOUT:
                    getMvpView().triggerLogout();
                    break;
                default:
                    break;
            }
        } else {
            getMvpView().triggerLogin(option);
        }
    }

    @Override
    public boolean isAuthorized() {
        return getDataManager().isAuthorized();
    }

    public void loadAccountItems() {
        getMvpView().showAccountItems(mAccountItems, mAccountImages);
    }

}
