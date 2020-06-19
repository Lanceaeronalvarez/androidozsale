package au.com.dealsdirect.ui.controller.shops;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.util.Log;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ShopsPresenter<V extends ShopsMvpView> extends BasePresenter<V> implements
        ShopsMvpPresenter<V> {

    private Disposable mPreviousLoadShopsBannerRequest = null;

    @Inject
    public ShopsPresenter(
            DataManager dataManager,
            SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {

        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadShopsBanner(GetBannerRequest request) {
        loadShopsBanner(request, true);
    }

    @Override
    public void loadShopsBanner(GetBannerRequest request, boolean getOnlyFromNetwork) {
        cancelPreviousLoadShopsBannerRequest();

        getDataManager().pruneCachedResponse(request);
        GetBannerResponse response = getDataManager().getCachedResponse(request, GetBannerResponse.class);
        if (response != null) {
            getMvpView().showShopBanners(response, request.getCategory(), true);
        }

        mPreviousLoadShopsBannerRequest = doApiCallForResponse(
                getDataManager().callGetBanners(request, getOnlyFromNetwork), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        GetBannerResponse getBannerResponse = (GetBannerResponse) response;
                        getMvpView().showShopBanners(getBannerResponse, request.getCategory(), false);
                        getDataManager().setCachedResponse(request, getBannerResponse);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        getMvpView().unBindPaginate();
                    }
                });
    }

    @Override
    public void loadSlidingBanners(GetBannerRequest request) {
        getDataManager().pruneCachedResponse(request);
        GetBannerResponse response = getDataManager().getCachedResponse(request, GetBannerResponse.class);
        if (response != null) {
            getMvpView().showSlidingBanners(response);
        }

        doApiCallForResponse(
                getDataManager().callGetBanners(request, false), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        GetBannerResponse getBannerResponse = (GetBannerResponse) response;
                        getMvpView().showSlidingBanners(getBannerResponse);
                        getDataManager().setCachedResponse(request, getBannerResponse);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        getMvpView().showSlidingBanners(null);
                    }
                });
    }

    @Override
    public void loadSponsoredBanners(GetBannerRequest request) {
        getDataManager().pruneCachedResponse(request);
        GetBannerResponse response = getDataManager().getCachedResponse(request, GetBannerResponse.class);
        if (response != null) {
            getMvpView().showSlidingBanners(response);
        }

        doApiCallForResponse(
                getDataManager().callGetBanners(request, false), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        GetBannerResponse getBannerResponse = (GetBannerResponse) response;
                        getMvpView().showSponsoredBanners(getBannerResponse);
                        getDataManager().setCachedResponse(request, getBannerResponse);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        getMvpView().showSponsoredBanners(null);
                    }
                });
    }


    private void cancelPreviousLoadShopsBannerRequest() {
        if (mPreviousLoadShopsBannerRequest != null) {
            getCompositeDisposable().delete(mPreviousLoadShopsBannerRequest);
            mPreviousLoadShopsBannerRequest = null;
        }
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

                    Log.d("CategoryPresenter", "success load category tree");

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
    public void cancelRequest() {
        cancel();
    }

    @Override
    public boolean isAuthorized() {
        return getDataManager().isAuthorized();
    }

    @Override
    public void selectBanner(String saleId, String bannerTitle, String bannerId, int position, String imageUrl, String endDate, boolean isAvailable) {

        if (!isViewAttached() || getMvpView().isChangeInProgress()) {
            return;
        }
        getMvpView().onBannerClicked(saleId, bannerTitle, bannerId, position, imageUrl, endDate, isAvailable);
    }

    @Override
    public boolean isGoogleAdsEnabled() {
        return getDataManager().isGoogleAdsEnabled();
    }

}

