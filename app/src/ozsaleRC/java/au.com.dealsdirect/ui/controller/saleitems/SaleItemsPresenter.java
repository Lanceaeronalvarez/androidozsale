package au.com.dealsdirect.ui.controller.saleitems;

import android.graphics.drawable.Drawable;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.data.wishlist.WishlistObject;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;

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
    public void loadWishlist() {
        doApiCallForResponse(getDataManager().callGetWishlist(), new AppApiCallback() {
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
    public void loadSaleItems(GetSaleItemsRequest getSaleItemsRequest) {

        // Cancels any previous loadSaleItems request
        clearPreviousGetSaleItemsRequest();

        Observable dualApiCall = Observable.zip(wrapObservable(getDataManager().callGetSaleItemsRequest(getSaleItemsRequest)),
                wrapObservable(getDataManager().callSortingFacets()),
                new BiFunction<GetSaleItemsResponse, List<SortingResponse>, Pair<GetSaleItemsResponse, List<SortingResponse>>>() {
                    @Override
                    public Pair<GetSaleItemsResponse, List<SortingResponse>> apply(GetSaleItemsResponse t1, List<SortingResponse> t2) throws Exception {
                        return new Pair<>(t1, t2);
                    }
                });

        mPreviousGetSaleItemsRequest = doApiCallForResponse(dualApiCall, new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                clearPreviousGetSaleItemsRequest();
                Pair pair = (Pair) response;
                getMvpView().onLoadSortingFacetsFinished((List<SortingResponse>) pair.second);
                getMvpView().showSaleItems((GetSaleItemsResponse) pair.first, !getSaleItemsRequest.hasFilters());
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
                                   boolean isFreeDelivery) {
        getMvpView().hideKeyboard();
        getMvpView().showProductDetails(
                viewHolder,
                position,
                seoIdentifierId,
                imagePlaceholderDrawable,
                imageUrl,
                skuId,
                saleId,
                isFreeDelivery);
    }

    protected <T> Observable<T> wrapObservable(Observable<T> observable) {
        return observable.subscribeOn(getSchedulerProvider().io());
    }

    @Override
    public void loadSortingFacets() {
        doApiCallForResponse(getDataManager().callSortingFacets(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                getMvpView().onLoadSortingFacetsFinished((List<SortingResponse>) response);
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

}
