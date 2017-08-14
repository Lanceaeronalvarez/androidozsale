package au.com.dealsdirect.ui.controller.home;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/6/17.
 */

public class HomePresenter<V extends HomeMvpView> extends BasePresenter<V> implements
        HomeMvpPresenter<V> {

    @Inject
    public HomePresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                         CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void callGetBasketItemsQuantity() {
        getCompositeDisposable().add(getDataManager()
                .callGetBasketItemsQuantity()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(basketQuantityResponse -> {
                    if(!isViewAttached()){
                        return;
                    }
                    CartUtil.setValueToCart(basketQuantityResponse.getItemQuantity());
                    getMvpView().updateBasketItemCount();
                },throwable -> {
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
                })
        );
    }

    @Override
    public boolean isAuthorized() {
        return getDataManager().isAuthorized();
    }
}
