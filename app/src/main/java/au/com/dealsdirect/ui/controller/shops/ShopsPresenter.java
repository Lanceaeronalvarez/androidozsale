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
import au.com.dealsdirect.data.network.model.banner.GetLeaderboardBannerRequest;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeRequest;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.notification.GetNotificationsRequest;
import au.com.dealsdirect.data.network.model.notification.GetNotificationsResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetBestSellerResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.SaleItemDetails;
import au.com.dealsdirect.data.priceinfo.PricingInfoLoaderHelper;
import au.com.dealsdirect.data.wishlist.WishlistObject;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.leaderboardbanner.LeaderboardPresenterHelper;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ShopsPresenter<V extends ShopsMvpView> extends BasePresenter<V> implements
        ShopsMvpPresenter<V> {

    private Disposable mPreviousLoadShopsBannerRequest = null;

    private PricingInfoLoaderHelper pricingInfoLoaderHelper = new PricingInfoLoaderHelper(new PricingInfoLoaderHelper.SaleItemProductLoader() {
        @Override
        public void load(String seoIdentifier, String saleId, PricingInfoLoaderHelper.SaleItemProductReceiver receiver) {
            doApiCallForResponse(getDataManager().callGetSaleItemDetails(seoIdentifier), new AppApiCallback() {
                @Override
                public void onSuccess(Object response) {
                    super.onSuccess(response);
                    SaleItemDetails saleItemDetails = (SaleItemDetails) response;

                    if (response != null) {
                        receiver.receive(saleItemDetails);
                    }
                }
            });
        }
    }, getDataManager());

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
    public void loadTrendingBrands(GetBannerRequest request) {
        doApiCallForResponse(
                getDataManager().callGetBanners2(request, false), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().showTrendingBrands((GetBannerResponse) response);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().showTrendingBrands(null);
                    }
                });
    }

    @Override
    public void loadBestSellers(String category) {
        doApiCallForResponse(
                getDataManager().callBestSellers(category), new AppApiCallback() {
                    @Override
                    public void onSuccess(List<?> response) {
                        super.onSuccess(response);
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().showBestSellers((List<GetBestSellerResponse>) response);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().showBestSellers(null);
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
        doApiCallForResponse(getDataManager().callGetCategories(new GetCategoryTreeRequest()), new AppApiCallback() {
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

    @Override
    public void loadLeaderboardBanner(String categoryId) {
        LeaderboardPresenterHelper.loadLeaderboardBanner(
                GetLeaderboardBannerRequest.newInstanceForShopPage(
                        isTablet() ? GetLeaderboardBannerRequest.DESKTOP_BROWSER :
                                GetLeaderboardBannerRequest.MOBILE_BROWSER,
                        categoryId),
                getDataManager(),
                getCompositeDisposable(),
                getSchedulerProvider(),
                new LeaderboardPresenterHelper.LeaderboardHelperListener() {
                    @Override
                    public void receiveResponse(GetBannerResponse response) {
                        getMvpView().showLeaderboardBanner(response);
                    }

                    @Override
                    public void receiveError(Throwable throwable) {
                        getMvpView().showLeaderboardBanner(null);
                    }

                });
    }

    @Override
    public int wishlistCount() {
        return getDataManager().getWishlist().size();
    }

    @Override
    public boolean isProductInWishlist(String productId) {
        return getDataManager().isProductInWishlist(productId);
    }

    @Override
    public void addProductToWishlist(String productId, String seoIdentifier, String masterProductId, ShopsMvpPresenter.WishlistDelayedCallback delayedCallback) {
        getDataManager().addToWishlist(new WishlistObject() {
            private String mProductId = productId;
            private String mSeoId = seoIdentifier;
            private String mMasterProductId = masterProductId;

            @Override
            public String getProductId() {
                return mProductId;
            }

            @Override
            public void setProductId(String id) {
                mProductId = id;
            }

            @Override
            public String getSeoId() {
                return mSeoId;
            }

            @Override
            public void setSeoId(String id) {
                mSeoId = id;
            }

            @Override
            public String getMasterProductId() {
                return mMasterProductId;
            }

            @Override
            public void setMasterProductId(String masterPId) {
                mMasterProductId = masterPId;
            }
        }, () -> {
            doApiCallForResponse(
                    getDataManager().callAddToWishlist(productId, seoIdentifier),
                    new AppApiCallback() {
                        @Override
                        public void onSuccess(Object response) {
                            super.onSuccess(response);
                            if (delayedCallback != null) {
                                delayedCallback.performDelayedAction();
                            }
                        }
                    });
        });
    }

    @Override
    public void removeProductFromWishlist(String productId, ShopsMvpPresenter.WishlistDelayedCallback delayedCallback) {
        getDataManager().removeFromWishlist(
                productId,
                () -> {
                    doApiCallForResponse(
                            getDataManager().callRemoveFromWishlist(productId),
                            new AppApiCallback() {
                                @Override
                                public void onSuccess(Object response) {
                                    super.onSuccess(response);
                                    if (delayedCallback != null) {
                                        delayedCallback.performDelayedAction();
                                    }
                                }
                            });
                });
    }

    @Override
    public void getPricingInfoText(String seoIdentifier) {
        pricingInfoLoaderHelper.getPricingInfo(seoIdentifier, null, (rrpText, totalPercentOff, originalPrice, combinedPricingInfoText) -> {
            if (!isViewAttached()) {
                return;
            }

            getMvpView().showPricingInfoText(rrpText, totalPercentOff, originalPrice, combinedPricingInfoText);
        });
    }

    @Override
    public void getNotifications() {
        GetNotificationsRequest request = new GetNotificationsRequest();
        String loginTicket = getDataManager().getLoginTicket();
        String[] split = loginTicket.split("\\.");
        if (split.length > 0) {
            request.setUserId(split[0]);
        } else {
            request.setUserId("");
        }
        doApiCallForResponse(getDataManager().getNotifications(request), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                if (response != null && isViewAttached()) {
                    getMvpView().showNotifications((List<GetNotificationsResponse>) response);
                }
            }
        });
    }
}

