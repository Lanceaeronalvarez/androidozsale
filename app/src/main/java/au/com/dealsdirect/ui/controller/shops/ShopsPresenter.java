package au.com.dealsdirect.ui.controller.shops;
/*
 * Created by CodeineBot on 5/15/17.
 */


import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.banner.GetTopBrandsResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

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
        if (response != null && isViewAttached()) {
            getMvpView().showShopBanners(response, request.getCategory(), true);
        }

        mPreviousLoadShopsBannerRequest = doApiCallForResponse(
                getDataManager().callGetBanners(request, getOnlyFromNetwork), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        GetBannerResponse getBannerResponse = (GetBannerResponse) response;
                        getDataManager().setCachedResponse(request, getBannerResponse);

                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().showShopBanners(getBannerResponse, request.getCategory(), false);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().unBindPaginate();
                    }
                });
    }

    @Override
    public void loadSlidingBanners(GetBannerRequest request) {
        getDataManager().pruneCachedResponse(request);
        GetBannerResponse response = getDataManager().getCachedResponse(request, GetBannerResponse.class);
        if (response != null && isViewAttached()) {
            getMvpView().showSlidingBanners(response);
        }

        doApiCallForResponse(
                getDataManager().callGetBanners(request, false), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        GetBannerResponse getBannerResponse = (GetBannerResponse) response;
                        getDataManager().setCachedResponse(request, getBannerResponse);

                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().showSlidingBanners(getBannerResponse);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().showSlidingBanners(null);
                    }
                });
    }

    @Override
    public void loadSponsoredBanners(GetBannerRequest request) {
        getDataManager().pruneCachedResponse(request);
        GetBannerResponse response = getDataManager().getCachedResponse(request, GetBannerResponse.class);
        if (response != null && isViewAttached()) {
            getMvpView().showSlidingBanners(response);
        }

        doApiCallForResponse(
                getDataManager().callGetBanners(request, false), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        GetBannerResponse getBannerResponse = (GetBannerResponse) response;
                        getDataManager().setCachedResponse(request, getBannerResponse);

                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().showSponsoredBanners(getBannerResponse);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().showSponsoredBanners(null);
                    }
                });
    }

    @Override
    public void loadCategoryBanners(GetBannerRequest request) {
        doApiCallForResponse(
                getDataManager().callGetBanners2(request, false), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().showCategoryBanners((GetBannerResponse) response);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().showCategoryBanners(null);
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
        doApiCallForResponse(getDataManager().callGetCategories(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                if (response != null && isViewAttached()) {
                    getMvpView().storeCategories((List<GetCategoryTreeResponse>) response);
                }
            }
        });
    }

    @Override
    public void loadTopBrands() {
        doApiCallForResponse(getDataManager().callGetTopBrands(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                if (response != null && isViewAttached()) {
                    getMvpView().showTopBrands((List<GetTopBrandsResponse>) response);
                }
            }
        });
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
    public boolean isGoogleAdsEnabled() {
        return getDataManager().isGoogleAdsEnabled();
    }

    @Override
    public int getBannerColumnCount() {
        return isTablet() ? getDataManager().getMobileTabletBannerColumns() : getDataManager().getMobilePhoneBannerColumns();
    }

    @Override
    public boolean getPrefersOldShopBannerDimensions() {
        return isTablet() || getDataManager().getPrefersOldShopBannersDimensions();
    }

    @Override
    public void setPrefersOldShopBannerDimensions(boolean doesPrefer) {
        getDataManager().setPrefersOldShopBannerDimensions(doesPrefer);
    }
}

