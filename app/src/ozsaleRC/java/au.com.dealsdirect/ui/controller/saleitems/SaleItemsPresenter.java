package au.com.dealsdirect.ui.controller.saleitems;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.cachedresponses.ListOfSortingResponses;
import au.com.dealsdirect.data.cachedresponses.ParamaterizedCachableRequest;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.banner.GetSaleBannerDetailsResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.data.wishlist.WishlistObject;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsPresenter<V extends SaleItemsMvpView> extends BasePresenter<V>
        implements SaleItemsMvpPresenter<V> {

    private static final String SEARCH_QUERY_TAG = "search_query";
    private Disposable mPreviousGetSaleItemsRequest = null;

    String mSaleId = "";

    @Inject
    public SaleItemsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                              CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadWishlistAll() {
        doApiCallForResponse(getDataManager().callGetWishlistAll(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> list) {
                getMvpView().showWishlist((List<GetSaleItemsResponse.Products>) list);
            }
        });
    }

    @Override
    public void loadWishlistPaginated(int limit, int offset) {
        doApiCallForResponse(getDataManager().callGetWishlistPaginated(limit, offset), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> list) {
                getMvpView().showWishlist((List<GetSaleItemsResponse.Products>) list);
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
    public void addToWishlist(String productId, String seoIdentifier, WishlistDelayedCallback delayedCallback) {
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
                                delayedCallback.performDelayedAction(productId, true);
                            }
                        }
                    });
        });
        getMvpView().updateWishlistWithAddition(productId);
    }

    @Override
    public void removeFromWishlist(String productId, WishlistDelayedCallback delayedCallback) {
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
                                        delayedCallback.performDelayedAction(productId, false);
                                    }
                                }
                            });
                });
        getMvpView().updateWishlistWithRemoval(productId);
    }

    @Override
    public void loadSaleBannerDetails(String saleId) {
        ParamaterizedCachableRequest request = new ParamaterizedCachableRequest("loadSaleBannerDetails", saleId);
        getDataManager().pruneCachedResponse(request);
        GetSaleBannerDetailsResponse getSaleBannerDetailsResponse = getDataManager().getCachedResponse(request, GetSaleBannerDetailsResponse.class);
        if (getSaleBannerDetailsResponse != null) {
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
        getDataManager().pruneCachedResponse(getSaleItemsRequest);
        GetSaleItemsResponse saleItemsResponse = getDataManager().getCachedResponse(getSaleItemsRequest, GetSaleItemsResponse.class);
        if (saleItemsResponse != null) {
            getMvpView().showSaleItems(saleItemsResponse, !getSaleItemsRequest.hasFilters(), true);
        }

        ParamaterizedCachableRequest loadSortingFacetsRequest = new ParamaterizedCachableRequest("loadSortingFacets");
        getDataManager().pruneCachedResponse(loadSortingFacetsRequest);
        ListOfSortingResponses listOfSortingResponses = getDataManager().getCachedResponse(loadSortingFacetsRequest, ListOfSortingResponses.class);
        if (listOfSortingResponses != null) {
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
                Pair pair = (Pair) response;
                getMvpView().onLoadSortingFacetsFinished((List<SortingResponse>) pair.second);
                getMvpView().showSaleItems((GetSaleItemsResponse) pair.first, !getSaleItemsRequest.hasFilters(), false);
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

    @Override
    public void loadProductDetails(RecyclerView.ViewHolder viewHolder,
                                   int position,
                                   String seoIdentifierId,
                                   Drawable imagePlaceholderDrawable,
                                   String imageUrl,
                                   String skuId,
                                   String saleId,
                                   boolean isFreeDelivery,
                                   boolean isSoldOut) {
        getMvpView().hideKeyboard();
        getMvpView().showProductDetails(
                viewHolder,
                position,
                seoIdentifierId,
                imagePlaceholderDrawable,
                imageUrl,
                skuId,
                saleId,
                isFreeDelivery,
                isSoldOut);
    }

    protected <T> Observable<T> wrapObservable(Observable<T> observable) {
        return observable.subscribeOn(getSchedulerProvider().io());
    }

    @Override
    public void loadSortingFacets() {
        ParamaterizedCachableRequest request = new ParamaterizedCachableRequest("loadSortingFacets");
        getDataManager().pruneCachedResponse(request);
        ListOfSortingResponses listOfSortingResponses = getDataManager().getCachedResponse(request, ListOfSortingResponses.class);
        if (listOfSortingResponses != null) {
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
        doApiCallForResponse(getDataManager().callGetCategories(),
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
                if (getMvpView() != null) {
                    getMvpView().storeBrandNames(output);
                }
            });
        });
    }
}
