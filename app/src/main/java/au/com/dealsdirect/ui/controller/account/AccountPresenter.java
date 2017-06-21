package au.com.dealsdirect.ui.controller.account;

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

    public void loadAccountItems(ArrayList<String> items) {

//        if (Auth.isLoggedIn()){
//            items.add("logout");
//
//        }
        getMvpView().showAccountItems(items);
    }
}
