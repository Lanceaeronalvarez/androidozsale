package au.com.dealsdirect.ui.controller.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/6/17.
 */

public class AccountPresenter<V extends AccountMvpView> extends BasePresenter<V> implements
        AccountMvpPresenter<V>, Serializable {

    @Inject
    public AccountPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);

    }

    @Override
    public void onAccountItemClick(int option) {

        if (!isViewAttached() || getMvpView().isChangeInProgress()) {
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
                    getMvpView().showLegalities(BundleKeys.TEMPLATE_KEY_ABOUT_US, option);
                    break;
                case R.string.account_privacy:
                    getMvpView().showLegalities(BundleKeys.TEMPLATE_KEY_PRIVACY, option);
                    break;
                case R.string.account_tnc:
                    getMvpView().showLegalities(BundleKeys.TEMPLATE_KEY_TNC, option);
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

    @Override
    public void loadAccountItems(List<Integer> title, List<Integer> drawable) {
        //Temporary logic for determining multi country, multi language account item availabilities.

        List<Integer> mAccountItems = title;

        List<Integer> mAccountImages = drawable;


        if (!getDataManager().getIsMultiCountry() && mAccountItems.indexOf(R.string.account_country) != -1) {
            mAccountItems.remove(mAccountItems.indexOf(R.string.account_country));
            mAccountImages.remove(mAccountImages.indexOf(R.drawable.bg_account_country));
        }

        if (!getDataManager().getIsMultiLanguage() && mAccountItems.indexOf(R.string.account_language) != -1) {
            mAccountItems.remove(mAccountItems.indexOf(R.string.account_language));
            mAccountImages.remove(mAccountImages.indexOf(R.drawable.bg_account_languages));
        }

        getMvpView().showAccountItems(mAccountItems, mAccountImages);
    }

}
