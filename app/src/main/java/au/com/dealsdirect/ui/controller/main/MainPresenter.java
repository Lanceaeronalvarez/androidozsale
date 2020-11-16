package au.com.dealsdirect.ui.controller.main;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/6/17.
 */

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V> implements
        MainMvpPresenter<V> {

    @Inject
    public MainPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
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
                    getMvpView().showBasketItemCount();
                },throwable -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();

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

    @Override
    public boolean isInitialLaunch() {
        return getDataManager().getIsInitialLaunch();
    }

    @Override
    public boolean hasWishlistBeenAccessed() {
        return getDataManager().hasWishlistBeenAccessed();
    }

    @Override
    public void setHasWishlistBeenAccessed(boolean isAccessed) {
        getDataManager().setHasWishlistBeenAccessed(isAccessed);
        getDataManager().updateWishlistCount();
    }

    @Override
    public void setInitialLaunchFalse() {
        getDataManager().setIsInitialLaunch(false);
    }
}
