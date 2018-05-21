package au.com.dealsdirect.ui.controller.contact.selectorder;
/*
 * Created by CodeineBot on 5/15/17.
 */


import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderList;
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

        getCompositeDisposable()
                .add(getDataManager()
                        .callGetContactOrders()
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(response -> {

                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();
                            List<ContactOrderList> myContactOrder
                                    = response.getContactOrderResponse().getList();

                            getMvpView().showContactOrders(myContactOrder);


                        }, throwable -> {

                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();
                            getMvpView().onError(throwable.getMessage());

                            // handle load accounts error here
                            if (throwable instanceof ANError) {
                                ANError anError = (ANError) throwable;
                                handleApiError(anError);
                            }
                        }));
    }

    @Override
    public void selectContactOrder(ContactOrderList contactOrderList){
        getMvpView().onContactOrderSelected(contactOrderList);
    }
}
