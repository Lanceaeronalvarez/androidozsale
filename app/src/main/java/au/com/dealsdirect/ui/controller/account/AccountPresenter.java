package au.com.dealsdirect.ui.controller.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/6/17.
 */

public class AccountPresenter<V extends AccountMvpView> extends BasePresenter<V> implements
        AccountMvpPresenter<V>,Serializable {

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
                        "My Payments",
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
                default:
                    break;
            }
        } else {
            getMvpView().triggerLogin(option);
        }
    }

    public void loadAccountItems(ArrayList<String> items) {

        if (getDataManager().isAuthorized()){
            items.add("Logout");
        }

        getMvpView().showAccountItems(items);
    }
}
