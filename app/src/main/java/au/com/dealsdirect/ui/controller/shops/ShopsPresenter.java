package au.com.dealsdirect.ui.controller.shops;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.util.Log;

import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class ShopsPresenter<V extends ShopsMvpView> extends BasePresenter<V> implements
        ShopsMvpPresenter<V> {

    @Inject
    public ShopsPresenter(
            DataManager dataManager,
            SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {

        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadShopsBanner(GetBannerRequest request) {
        loadShopsBanner(request, false);
    }

    @Override
    public void loadShopsBanner(GetBannerRequest request, boolean getOnlyFromNetwork) {

        doApiCallForResponse(getDataManager().callGetBanners(request, getOnlyFromNetwork), new AppApiCallback(){
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                getMvpView().showShopBanners((List<GetBannerResponse>) response);
            }

            @Override
            public void onFailure() {
                getMvpView().unBindPaginate();
            }
        });
    }

    @Override
    public void loadCategoryTree() {
        getCompositeDisposable().add(getDataManager()
                .callGetGetCategories()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    Log.d("CategoryPresenter","success load category tree");

                    if (response != null) {

                        getMvpView().storeCategories(response);
                    }

                    getMvpView().hideLoading();

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

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
                    }
                }));
    }

    @Override
    public boolean isAccessAnonymousEnabled() {
        return getDataManager().getAccessAnonymousEnabled();
    }

    @Override
    public boolean isAuthorized() {
        return getDataManager().isAuthorized();
    }

}

