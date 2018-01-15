package au.com.dealsdirect.ui.controller.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

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

    ArrayList<Integer> mAccountItems = new ArrayList<>(
            Arrays.asList(
                    R.string.account_orders,
                    R.string.account_payments,
                    R.string.account_details,
                    R.string.account_addresses,
                    R.string.account_returns,
                    R.string.account_vouchers,
                    R.string.account_invite_friend,
                    R.string.account_language,
                    R.string.account_country,
                    R.string.account_contact_us,
                    R.string.account_about_us,
                    R.string.account_privacy,
                    R.string.account_tnc,
                    R.string.account_tutorial
                    ));

    ArrayList<Integer> mAccountImages = new ArrayList<>(Arrays.asList(

            R.drawable.bg_account_orders,
            R.drawable.bg_account_payments,
            R.drawable.bg_account_details,
            R.drawable.bg_account_address,
            R.drawable.bg_account_returns,
            R.drawable.bg_account_vouchers,
            R.drawable.bg_account_invite_friend,
            R.drawable.bg_account_languages,
            R.drawable.bg_account_country,
            R.drawable.bg_account_contact_us,
            R.drawable.bg_account_about_us,
            R.drawable.bg_account_privacy_policy,
            R.drawable.bg_account_terms_and_conditions,
            R.drawable.bg_account_tutorial,
            R.drawable.bg_account_logout
    ));


    @Inject
    public AccountPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);

        //Temporary logic for determining multi country, multi language account item availabilities.

        if(!getDataManager().getIsMultiCountry()){
            mAccountItems.remove(mAccountItems.indexOf(R.string.account_country));
            mAccountImages.remove(mAccountImages.indexOf(R.drawable.bg_account_country));
        }

        if(!getDataManager().getIsMultiLanguage()){
            mAccountItems.remove(mAccountItems.indexOf(R.string.account_language));
            mAccountImages.remove(mAccountImages.indexOf(R.drawable.bg_account_languages));
        }
    }

    @Override
    public void onAccountItemClick(int option) {

        if (getMvpView().isChangeInProgress()) {
            return;
        }

        if (getDataManager().isAuthorized() || (option == R.string.account_language ||
                option == R.string.account_privacy ||
                option == R.string.account_tnc ||
                option == R.string.account_about_us ||
                option == R.string.account_country ||
                option == R.string.account_tutorial)) {

            switch (option) {
                case R.string.account_details:
                    getMvpView().showMyDetailsController();
                    break;
                case R.string.account_addresses:
                    getMvpView().showMyAddressesController();
                    break;
                case R.string.account_orders:
                    getMvpView().showMyOrders();
                    break;
                case R.string.account_vouchers:
                    getMvpView().showMyVouchers();
                    break;
                case R.string.account_returns:
                    getMvpView().showMyReturns();
                    break;
                case R.string.account_payments:
                    getMvpView().showMyPaymentsController();
                    break;
                case R.string.account_language:
                    getMvpView().showLanguage();
                    break;
                case R.string.account_about_us:
                    getMvpView().showLegalities("aboutus", option);
                    break;
                case R.string.account_privacy:
                    getMvpView().showLegalities("PrivacyPolicy_Text", option);
                    break;
                case R.string.account_tnc:
                    getMvpView().showLegalities("TermsAndConditions_Text", option);
                    break;
                case R.string.account_contact_us:
                    getMvpView().showContactUs();
                    break;
                case R.string.account_country:
                    getMvpView().showCountry();
                    break;
                case R.string.account_invite_friend:
                    getMvpView().showInviteAFriend();
                    break;
                case R.string.account_tutorial:
                    getMvpView().showTutorial();
                    break;
                case R.string.account_logout:
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
    public void setMultiCountry(boolean isMultiCountry) {
        getDataManager().setIsMultiCountry(isMultiCountry);
    }

    @Override
    public boolean isAuthorized() {
        return getDataManager().isAuthorized();
    }

    public void loadAccountItems() {
        getMvpView().showAccountItems(mAccountItems, mAccountImages);
    }

}
