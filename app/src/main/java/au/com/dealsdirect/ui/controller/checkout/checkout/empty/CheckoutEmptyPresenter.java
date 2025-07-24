package au.com.dealsdirect.ui.controller.checkout.checkout.empty;

import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.productdetails.GetBestSellerResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.SaleItemDetails;
import au.com.dealsdirect.data.priceinfo.PricingInfoLoaderHelper;
import au.com.dealsdirect.data.wishlist.WishlistObject;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpPresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class CheckoutEmptyPresenter<V extends CheckoutEmptyMvpView> extends BasePresenter<V> implements CheckoutEmptyMvpPresenter<V> {


    private final PricingInfoLoaderHelper pricingInfoLoaderHelper = new PricingInfoLoaderHelper(new PricingInfoLoaderHelper.SaleItemProductLoader() {
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
    public CheckoutEmptyPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public CartDetailsMapper getCart() {
        return getDataManager().getCart();
    }

    @Override
    public void loadCart(String postcode, String pickupPoint, boolean willForceLoad) {
        if (!getDataManager().isAuthorized()) {
            getDataManager().saveCart(null);
            getMvpView().showCart();
            return;
        }
        if ((getCart() != null && !getCart().isOld()) && !willForceLoad) {
            if (isViewAttached()) {
                getMvpView().showCart();
            }
            return;
        }

        getCompositeDisposable().add(getDataManager()
                .callGetCurrentOrder(new GetCurrentOrder.RequestValue(postcode, pickupPoint, getDataManager().getLanguageId()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetCurrentOrder.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetCurrentOrder.ResponseValue responseValue) throws Exception {

                        getDataManager().saveCart(new CartDetailsMapper(responseValue));

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().showCart();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().onError(throwable.getMessage());

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                })
        );
    }

    @Override
    public boolean checkIsLoggedIn() {
        return getDataManager().isAuthorized();
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

    @Override
    public void loadRecentlyViewedItems() {
        doApiCallForResponse(getDataManager().callRecentlyViewedItems(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> object) {
                super.onSuccess(object);

                if (!isViewAttached()) {
                    return;
                }
                List<RecentlyViewedItemResponse> response = (List<RecentlyViewedItemResponse>) object;
                getMvpView().showRecentlyViewedItems(response);

            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                // handle load accounts error here
                if (throwable instanceof ANError) {
                    ANError anError = (ANError) throwable;
                    handleApiError(anError);
                }

                if (!isViewAttached()) {
                    return;
                }
                getMvpView().onError(throwable.getMessage());
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
    public void addProductToWishlist(String productId, String seoIdentifier, String masterProductId, CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback) {
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
    public void removeProductFromWishlist(String productId, CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback) {
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
}
