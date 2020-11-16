package au.com.dealsdirect.ui.controller.contact.selectorder;
/*
 * Created by CodeineBot on 5/15/17.
 */


import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class ContactSelectOrderPresenter<V extends ContactSelectOrderMvpView> extends BasePresenter<V> implements ContactSelectOrderMvpPresenter<V> {

    @Inject
    public ContactSelectOrderPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void loadContactUsOrders() {
        getMvpView().showLoading();

        doApiCallForResponse(getDataManager().callGetContactOrders(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                getMvpView().showContactOrders((List<ContactOrderResponse>) response);
            }
        });
    }

    @Override
    public void selectContactOrder(ContactOrderResponse contactOrderResponse) {
        getMvpView().onContactOrderSelected(contactOrderResponse);
    }
}
