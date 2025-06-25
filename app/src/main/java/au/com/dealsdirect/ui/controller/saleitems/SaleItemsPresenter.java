package au.com.dealsdirect.ui.controller.saleitems;

import android.os.Handler;
import android.os.Looper;

import androidx.core.util.Pair;

import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.cachedresponses.ListOfSortingResponses;
import au.com.dealsdirect.data.cachedresponses.ParamaterizedCachableRequest;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.banner.GetLeaderboardBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetSaleBannerDetailsResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeRequest;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.SaleItemDetails;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.data.priceinfo.PricingInfoLoaderHelper;
import au.com.dealsdirect.data.wishlist.WishlistObject;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.leaderboardbanner.LeaderboardPresenterHelper;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class SaleItemsPresenter<V extends SaleItemsMvpView> extends BasePresenter<V>
        implements SaleItemsMvpPresenter<V> {

    private Disposable mPreviousGetSaleItemsRequest = null;

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
    public SaleItemsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                              CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadWishlistPaginated(int limit, int offset) {
        doApiCallForResponse(getDataManager().callGetWishlistPaginated(limit, offset), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> list) {
                getMvpView().showWishlist((List<SaleItemProduct>) list, offset);
            }
        });
    }

    @Override
    public boolean isProductInWishlist(String productId) {
        return getDataManager().isProductInWishlist(productId);
    }

    @Override
    public int wishlistCount() {
        return getDataManager().getWishlist().size();
    }

    @Override
    public void addToWishlist(String productId, String productName, String seoIdentifier, Double price, WishlistDelayedCallback delayedCallback) {
        getDataManager().addToWishlist(new WishlistObject() {
            private String mProductId = productId;
            private String mSeoId = seoIdentifier;

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
                return null;
            }

            @Override
            public void setMasterProductId(String masterProductId) {

            }
        }, () -> {
            doApiCallForResponse(
                    getDataManager().callAddToWishlist(productId, seoIdentifier),
                    new AppApiCallback() {
                        @Override
                        public void onSuccess(Object response) {
                            super.onSuccess(response);
                            if (delayedCallback != null) {
                                delayedCallback.performDelayedAction(productId, productName, price, true);
                            }
                        }
                    });
        });
        if (isViewAttached()) {
            getMvpView().updateWishlistWithAddition(productId);
        }
    }

    @Override
    public void removeFromWishlist(String productId, String productName, Double price, WishlistDelayedCallback delayedCallback) {
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
                                        delayedCallback.performDelayedAction(productId, productName, price, false);
                                    }
                                }
                            });
                });
        if (isViewAttached()) {
            getMvpView().updateWishlistWithRemoval(productId);
        }
    }

    @Override
    public void loadSaleBannerDetails(String saleId) {
        ParamaterizedCachableRequest request = new ParamaterizedCachableRequest("loadSaleBannerDetails", saleId);
        getDataManager().pruneCachedResponse(request);
        GetSaleBannerDetailsResponse getSaleBannerDetailsResponse = getDataManager().getCachedResponse(request, GetSaleBannerDetailsResponse.class);
        if (getSaleBannerDetailsResponse != null && isViewAttached()) {
            getMvpView().showSaleBannerDetails(getSaleBannerDetailsResponse);
        }

        doApiCallForResponse(getDataManager().callGetSaleBannerDetails(saleId), new AppApiCallback() {
            @Override
            public void onSuccess(Object o) {
                if (o instanceof GetSaleBannerDetailsResponse) {
                    getMvpView().showSaleBannerDetails((GetSaleBannerDetailsResponse) o);
                    getDataManager().setCachedResponse(request, (GetSaleBannerDetailsResponse) o);
                }
            }
        });
    }

    @Override
    public void loadSaleItems(GetSaleItemsRequest getSaleItemsRequest) {
        final int pageNumber = getSaleItemsRequest.getPageNumber();
        getDataManager().pruneCachedResponse(getSaleItemsRequest);
        GetSaleItemsResponse saleItemsResponse = getDataManager().getCachedResponse(getSaleItemsRequest, GetSaleItemsResponse.class);
        if (saleItemsResponse != null && isViewAttached()) {
            getMvpView().showSaleItems(saleItemsResponse, pageNumber, !getSaleItemsRequest.hasFilters(), true);
        }

        ParamaterizedCachableRequest loadSortingFacetsRequest = new ParamaterizedCachableRequest("loadSortingFacets");
        getDataManager().pruneCachedResponse(loadSortingFacetsRequest);
        ListOfSortingResponses listOfSortingResponses = getDataManager().getCachedResponse(loadSortingFacetsRequest, ListOfSortingResponses.class);
        if (listOfSortingResponses != null && isViewAttached()) {
            getMvpView().onLoadSortingFacetsFinished(listOfSortingResponses.getResponses());
        }

        // Cancels any previous loadSaleItems request
        clearPreviousGetSaleItemsRequest();

        Observable dualApiCall = Observable.zip(wrapObservable(getDataManager().callGetSaleItemsRequest(getSaleItemsRequest)),
                wrapObservable(getDataManager().callSortingFacets()),
                Pair::new);

        mPreviousGetSaleItemsRequest = doApiCallForResponse(dualApiCall, new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                clearPreviousGetSaleItemsRequest();
                final Pair pair = (Pair) response;
                getMvpView().onLoadSortingFacetsFinished((List<SortingResponse>) pair.second);
                getMvpView().showSaleItems((GetSaleItemsResponse) pair.first, pageNumber, !getSaleItemsRequest.hasFilters(), false);
                getDataManager().setCachedResponse(getSaleItemsRequest, (GetSaleItemsResponse) pair.first);
                getDataManager().setCachedResponse(loadSortingFacetsRequest, new ListOfSortingResponses((List<SortingResponse>) pair.second));
            }

            @Override
            public void onFailure(Throwable t) {
                clearPreviousGetSaleItemsRequest();
            }
        });
    }

    private void clearPreviousGetSaleItemsRequest() {
        if (mPreviousGetSaleItemsRequest != null) {
            getCompositeDisposable().remove(mPreviousGetSaleItemsRequest);
            mPreviousGetSaleItemsRequest = null;
        }
    }

    protected <T> Observable<T> wrapObservable(Observable<T> observable) {
        return observable.subscribeOn(getSchedulerProvider().io());
    }

    @Override
    public void loadSortingFacets() {
        ParamaterizedCachableRequest request = new ParamaterizedCachableRequest("loadSortingFacets");
        getDataManager().pruneCachedResponse(request);
        ListOfSortingResponses listOfSortingResponses = getDataManager().getCachedResponse(request, ListOfSortingResponses.class);
        if (listOfSortingResponses != null && isViewAttached()) {
            getMvpView().onLoadSortingFacetsFinished((List<SortingResponse>) listOfSortingResponses.getResponses());
        }

        doApiCallForResponse(getDataManager().callSortingFacets(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                getMvpView().onLoadSortingFacetsFinished((List<SortingResponse>) response);
                getDataManager().setCachedResponse(request, new ListOfSortingResponses((List<SortingResponse>) response));
            }
        });
    }

    @Override
    public boolean isSortingEnabled() {
        return getDataManager().getIsSortingEnabled();
    }

    @Override
    public boolean isGoogleAdsEnabled() {
        return getDataManager().isGoogleAdsEnabled();
    }

    @Override
    public int getColumnCount() {
        return getDataManager().getLastColumnSelected();
    }

    @Override
    public void setColumnCount(int columnCount) {
        getDataManager().setLastColumnSelected(columnCount);
    }

    public void setTimeStamp(String date) {
        getDataManager().setLastTimeStamp(date);
    }

    @Override
    public String getTimeStamp() {
        return getDataManager().getLastTimeStamp();
    }

    @Override
    public void loadBrandBubbles() {
        doApiCallForResponse(getDataManager().callGetCategories(new GetCategoryTreeRequest()),
                new AppApiCallback() {
                    @Override
                    public void onSuccess(List<?> list) {
                        processBrandNamesInAnotherThread((List<GetCategoryTreeResponse>) list);
                    }
                });
    }

    private void processBrandNamesInAnotherThread(List<GetCategoryTreeResponse> response) {
        if (response == null || response.isEmpty()) {
            return;
        }

        final Handler handler = new Handler();
        handler.post(() -> {
            BrandNames output = new BrandNames(response);
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                if (isViewAttached()) {
                    getMvpView().storeBrandNames(output);
                }
            });
        });
    }

    @Override
    public long getSupplierOriginaPriceInfoTimeAgreed() {
        return getDataManager().getSupplierOriginalPriceInfoSaleListTimeAgreed();
    }

    @Override
    public void setSupplierOriginaPriceInfoTimeAgreed(long timestamp) {
        getDataManager().setIsSupplierOriginalPriceInfoSaleListTimeAgreed(timestamp);
    }

    @Override
    public int getPriceBlockMode() {
        return getDataManager().getProductPagePriceBlockMode();
    }

    @Override
    public int getHoursLeftToDisplayTimer() {
        return getDataManager().getHoursLeftToDisplayTimer();
    }

    @Override
    public void loadLeaderboardBanner() {
        LeaderboardPresenterHelper.loadLeaderboardBanner(
                GetLeaderboardBannerRequest.newInstanceForProductList(
                        isTablet() ? GetLeaderboardBannerRequest.DESKTOP_BROWSER :
                                GetLeaderboardBannerRequest.MOBILE_BROWSER),
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
    public void getPricingInfoText(String seoIdentifier, String saleId) {
        pricingInfoLoaderHelper.getPricingInfo(seoIdentifier, saleId, (rrpText, totalPercentOff, originalPrice, combinedPricingInfoText) -> {
            if (!isViewAttached()) {
                return;
            }

            getMvpView().showPricingInfoText(rrpText, totalPercentOff, originalPrice, combinedPricingInfoText);
        });
    }

    @Override
    public String getShippingTemplateText() {
        return getDataManager().getShippingHover();
    }

    @Override
    public String getShippingTitleText() {
        return getDataManager().getShippingTitle();
    }
}
