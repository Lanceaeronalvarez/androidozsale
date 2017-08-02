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

    private int[] accountImages = new int[]{
            R.drawable.bg_account_details,
            R.drawable.bg_account_address,
            R.drawable.bg_account_orders,
            R.drawable.bg_account_payments,
            R.drawable.bg_account_vouchers,
            R.drawable.bg_account_returns,
            R.drawable.bg_account_languages,
            R.drawable.bg_account_terms_and_conditions,
            R.drawable.bg_account_privacy_policy,
            R.drawable.bg_account_about_us,
            R.drawable.bg_account_privacy_policy
    };

    @Inject
    public AccountPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadAccountItems() {

        loadAccountItems(new ArrayList<>(
                Arrays.asList(
                        "My Details",
                        "My Addresses",
                        "My Orders",
                        "My Payments",
                        "My Vouchers",
                        "My Returns",
                        "Language",
                        "Terms & Conditions",
                        "Privacy Policy",
                        "About Us" )), accountImages);
    }

    @Override
    public void onAccountItemClick(String option) {
        if (getDataManager().isAuthorized() || (option.equalsIgnoreCase("Language") ||
                option.equalsIgnoreCase("Privacy Policy") ||
                option.equalsIgnoreCase("Terms & Conditions") ||
                option.equalsIgnoreCase("About Us"))) {
            switch (option) {
                case "My Details":
                    getMvpView().showMyDetailsController();
                    break;
                case "My Addresses":
                    getMvpView().showMyAddressesController();
                    break;
                case "My Orders":
                    getMvpView().showMyOrders();
                    break;
                case "My Vouchers":
                    getMvpView().showMyVouchers();
                    break;
                case "My Returns":
                    getMvpView().showMyReturns();
                    break;
                case "My Payments":
                    getMvpView().showMyPaymentsController();
                    break;
                case "Language":
                    getMvpView().showLanguage();
                    break;
                case "Logout":
                    getMvpView().triggerLogout();
                    break;
                case "About Us":
                    getMvpView().showLegalities("aboutus", option);
                    break;
                case "Privacy Policy":
                    getMvpView().showLegalities("PrivacyPolicy_Text", option);
                    break;
                case "Terms & Conditions":
                    getMvpView().showLegalities("TermsAndConditions_Text", option);
                    break;
                default:
                    break;
            }
        } else {
            getMvpView().triggerLogin(option);
        }
    }

    public void loadAccountItems(ArrayList<String> items, int[] images) {

        if (getDataManager().isAuthorized()) {
            items.add("Logout");
        }

        getMvpView().showAccountItems(items, images);
    }
}
