package au.com.dealsdirect.ui.controller.contact.selectorder;
/*
 * Created by CodeineBot on 5/15/17.
 */


import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class ContactSelectOrderPresenter<V extends ContactSelectOrderMvpView> extends BasePresenter<V> implements ContactSelectOrderMvpPresenter<V> {

    @Inject
    public ContactSelectOrderPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void loadContactUsSubjects() {

    }

    @Override
    public void loadContactUsOrders() {

    }
}
