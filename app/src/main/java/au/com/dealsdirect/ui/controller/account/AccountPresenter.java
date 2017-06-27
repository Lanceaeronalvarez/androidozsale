package au.com.dealsdirect.ui.controller.account;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.auth.Auth;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BasePresenter;
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
    public void loadAccountItems() {

        loadAccountItems(new ArrayList<>(
                Arrays.asList(
                        "My Details",
                        "My Addresses",
                        "My Orders",
                        "My Vouchers",
                        "My Returns",
                        "Contact Us",
                        "Language")));
    }

    @Override
    public void onAccountItemClick(String option) {
        if (getDataManager().isAuthorized()) {
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
                    break;
                case "Contact Us":
                    getMvpView().showViewContactUsController();
                    break;
                case "Language":
                    getMvpView().showLanguage();
                    break;
                default:
                    break;

            }
        } else {
            getMvpView().triggerLogin(option);
        }
    }

    public void loadAccountItems(ArrayList<String> items) {

//        if (Auth.isLoggedIn()){
//            items.add("logout");
//
//        }
        getMvpView().showAccountItems(items);
    }
}
