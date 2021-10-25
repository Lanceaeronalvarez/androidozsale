package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.annotation.SuppressLint;

import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.afterpay.GetAfterpayDataResponse;
import au.com.dealsdirect.data.network.model.checkout.BasketQuantityResponse;
import au.com.dealsdirect.data.network.model.events.DeliveryPriceViewEventRequest;
import au.com.dealsdirect.data.network.model.ourpaydata.OurpayDataRequest;
import au.com.dealsdirect.data.network.model.ourpaydata.OurpayDataResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetPostcodeDefaultResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetPostcodeShippingPriceResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetYouMayAlsoLikeResponse;
import au.com.dealsdirect.data.network.model.promoinfo.PromoInfoResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecommendedItemsResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.SaleItemDetails;
import au.com.dealsdirect.data.wishlist.WishlistObject;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayError;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.CurrencyUtil;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * .Created by smartwave on 08/06/2017.
 */

public class SaleItemDetailsPresenter<V extends SaleItemDetailsMvpView> extends BasePresenter<V> implements SaleItemDetailsMvpPresenter<V> {

    @Inject
    public SaleItemDetailsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                                    CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadProductDetails(String saleId, String seoIdentifierId) {

        // note: sale_id is from a product list opened from banners

        Observable<SaleItemDetails> callGetSaleItemDetailObservable = saleId == null || saleId.isEmpty() ?
                getDataManager().callGetSaleItemDetails(seoIdentifierId) :
                getDataManager().callGetSaleItemDetails(saleId, seoIdentifierId);

        doApiCallForResponse(callGetSaleItemDetailObservable, new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                SaleItemDetails saleItemDetails = (SaleItemDetails) response;

                if (response != null) {
                    getMvpView().showProductDetails(saleItemDetails);
                }

                getMvpView().hideLoading();
            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                getMvpView().hideLoading();

                // handle load accounts error here
                if (throwable instanceof ANError) {
                    ANError anError = (ANError) throwable;
                    handleApiError(anError);
                }
            }
        });
    }

    @Override
    public void loadOurpayData(final SaleItemDetails value) {
        if (!getDataManager().isOurpayEnabled()) {
            return;
        }

        doApiCallForResponse(getDataManager().callGetOurpayData(OurpayDataRequest.init(
                CurrencyUtil.getCurrency(getDataManager().getCountryId()), value.getPrice().getValue())),
                new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);

                        generateOurpay(value, (OurpayDataResponse) response);
                    }
                });
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void loadAfterpayData(Double price) {
        if (!getDataManager().isAfterpayEnabled()) {
            return;
        }

        doApiCallForResponse(getDataManager().callGetAfterpayData(String.format("%.2f", price)),
                new AppApiCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);

                        if (o instanceof GetAfterpayDataResponse) {
                            GetAfterpayDataResponse response = (GetAfterpayDataResponse) o;
                            if (response.getApplicabilityStatus().toLowerCase().contains("ok") &&
                                    response.getPaymentInfo() != null) {
                                getMvpView().showAfterpayDetails(
                                        response.getPaymentInfo().getPaymentsCount(),
                                        response.getPaymentInfo().getPaymentsAmount(),
                                        getDataManager().getCurrencySign());
                            } else {
                                getMvpView().showAfterpayDetails(0, 0, null);
                            }
                        }
                    }
                });
    }

    @Override
    public void loadPromoInfo(String skuId) {
        doApiCallForResponse(getDataManager().callPromoInfo(skuId),
                new AppApiCallback() {
                    @Override
                    public void onSuccess(List<?> o) {
                        super.onSuccess(o);
                        if (o instanceof PromoInfoResponse) {
                            PromoInfoResponse response = (PromoInfoResponse) o;
                            getMvpView().setDynamicDiscount(response.getPercentOffText());
                            getMvpView().setIsAfterpayDetailsVisible(response.getAfterpayEnabled());
                            getMvpView().showFreeShipping(response.getDeliveryType(), response.getDeliveryThreshold());
                            getMvpView().showPostcodeForm(response.getShowPostCode());

                            final Boolean shippingAvailability = response.getShippingAvailability();
                            final Float deliveryPrice = response.getDeliveryPrice();
                            if (shippingAvailability != null && deliveryPrice != null) {
                                GetPostcodeShippingPriceResponse shippingPriceResponse = new GetPostcodeShippingPriceResponse();
                                shippingPriceResponse.setPrice(deliveryPrice);
                                shippingPriceResponse.setShippingAvailability(shippingAvailability);
                                getMvpView().showPreviewShippingPrice(shippingPriceResponse, null, DeliveryPriceViewEventRequest.OPERATION_AUTO);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                    }

                });
    }

    @Override
    public void addToCart(AddToCartRequest requestValues) {
        getMvpView().showLoading();

        doApiCallForResponse(getDataManager()
                .callAddItemToCart(requestValues), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                getMvpView().showAddToCartResponse(new CheckoutDetailsMapper((AddToCartResponse.Response) response));
                getDataManager().setHasActiveCheckoutSession(false);
            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                getMvpView().hideLoading();
                getMvpView().onError(throwable.getMessage());
                getMvpView().showAddToCartResponseFailed();

                // handle load accounts error here
                if (throwable instanceof ANError) {
                    ANError anError = (ANError) throwable;
                    handleApiError(anError);
                }

            }
        });

    }

    @Override
    public boolean isAuthorized() {
        return getDataManager().isAuthorized();
    }

    @Override
    public void generateOurpay(SaleItemDetails value, OurpayDataResponse ourpayDataResponse) {
        Ourpay ourpay = new Ourpay();

        try {
            ourpay.setState(OurpayState.PRECART);

            ourpay.setDescription(ourpayDataResponse.getSummary().getDescription());
            ourpay.setTotalAmount(value.getPrice().getValue());
            ourpay.setCanUse(true);
            ourpay.setBillingPeriod(ourpayDataResponse.getPayment().getBillingPeriod());
            ourpay.setTransactionCount(ourpayDataResponse.getPayment().getTransactionCount());

            ourpay.setMinAmount(ourpayDataResponse.getPayment().getPaymentConditions().getMinAmountThreshold().doubleValue());
            ourpay.setMaxAmount(ourpayDataResponse.getPayment().getPaymentConditions().getMaxAmountThreshold().doubleValue());

            ourpay.setInitialAmount(ourpayDataResponse.getSummary().getFirstTransactionAmount());

            ourpay.setPlannedTransactions(ourpayDataResponse.getPayment().getBillingAgreement().getPlannedTransactions());
            ourpay.setPlannedTransactionText(ourpayDataResponse.getSummary().getPlannedTransactionsText());
            ourpay.setPlannedTransactionAmount(ourpayDataResponse.getSummary().getPlannedTransactionsAmount());
            ourpay.setFirstTransactionText(ourpayDataResponse.getSummary().getFirstTransactionText());
            ourpay.setFirstTransactionAmount(ourpayDataResponse.getSummary().getFirstTransactionAmount());

            if (ourpayDataResponse.getSummary().getDescription() != null) {
                ourpay.setDetails(ourpayDataResponse.getSummary().getDescription());
            }

            if (OurpayStateManager.isPriceOutOfRange(ourpay)) {
                ourpay.setState(ourpay.getState() | OurpayState.ERROR);
                ourpay.setCanUse(false);
                ourpay.setErrorCode(OurpayError.AMOUNT_OUT_OF_RANGE);
            } else {
                if (ourpay.getPlannedTransactions() == null) {
                    ourpay.setState(OurpayState.DISABLED);
                }
            }
        } catch (Exception ex) {
            ourpay.setState(OurpayState.DISABLED);
        }

        getMvpView().showMyPayDetails(value, ourpay);


    }

    @Override
    public void callGetBasketItemsQuantity() {

        doApiCallForResponse(getDataManager().callGetBasketItemsQuantity(), new AppApiCallback() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);

                CartUtil.setValueToCart(((BasketQuantityResponse) o).getItemQuantity());
                getMvpView().onCallGetBasketItemsQuantity();
            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                getMvpView().onError(throwable.getMessage());

                // handle load accounts error here
                if (throwable instanceof ANError) {
                    ANError anError = (ANError) throwable;
                    handleApiError(anError);
                }
            }
        });
    }

    @Override
    public String getPersonalisationErrorText() {
        return getDataManager().getPersonalisationTemplateTexts();
    }

    @Override
    public void getDynamicDiscount(String skuId) {
        doApiCallForResponse(getDataManager().callDynamicDiscount(skuId), new AppApiCallback() {
            @Override
            public void onSuccess(Object o) {
                if (o != null) getMvpView().setDynamicDiscount((String) o);
            }

            @Override
            public void onFailure(Throwable throwable) {
                AppLogger.d(throwable.getMessage());
                getMvpView().setDynamicDiscount(null);

                // handle load accounts error here
                if (throwable instanceof ANError) {
                    ANError anError = (ANError) throwable;
                    handleApiError(anError);
                }
            }
        });
    }

    @Override
    public String getAfterpayLightboxImgUrl() {
        return getDataManager().getAfterpayLightboxImageUrl();
    }

    @Override
    public String getAfterpayTermsLink() {
        return getDataManager().getAfterpayTermsLink();
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
    public void addProductToWishlist(String productId, String seoIdentifier, String masterProductId, WishlistDelayedCallback delayedCallback) {
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
    public void removeProductFromWishlist(String productId, WishlistDelayedCallback delayedCallback) {
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
    public void loadRecommendedItems() {
        doApiCallForResponse(getDataManager().callRecommendedItems(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> object) {
                super.onSuccess(object);

                if (object != null && object.size() != 0) {
                    List<RecommendedItemsResponse> responseList = (List<RecommendedItemsResponse>) object;
                    getMvpView().showRecommendedItems(responseList);
                }

            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                getMvpView().onError(throwable.getMessage());

                // handle load accounts error here
                if (throwable instanceof ANError) {
                    ANError anError = (ANError) throwable;
                    handleApiError(anError);
                }
            }
        });
    }

    @Override
    public void loadYouMayAlsoLike(String skuId) {
        doApiCallForResponse(getDataManager().callYouMayAlsoLike(skuId), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);

                if (response != null && response.size() != 0) {
                    getMvpView().showYouMayAlsoLike((List<GetYouMayAlsoLikeResponse>) response);
                }

            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                getMvpView().hideLoading();

                // handle load accounts error here
                if (throwable instanceof ANError) {
                    ANError anError = (ANError) throwable;
                    handleApiError(anError);
                }
            }
        });
    }

    @Override
    public void loadRecentlyViewedItems() {
        doApiCallForResponse(getDataManager().callRecentlyViewedItems(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> object) {
                super.onSuccess(object);

                List<RecentlyViewedItemResponse> response = (List<RecentlyViewedItemResponse>) object;
                getMvpView().showRecentlyViewedItems(response);

            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                getMvpView().onError(throwable.getMessage());

                // handle load accounts error here
                if (throwable instanceof ANError) {
                    ANError anError = (ANError) throwable;
                    handleApiError(anError);
                }
            }
        });
    }

    @Override
    public void addToRecentlyViewedItems(String productId, String masterSkuId) {
        doApiCallForResponse(getDataManager().callAddToRecentlyViewedItems(
                new RecentlyViewedItemRequest(productId, masterSkuId)), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> object) {
                super.onSuccess(object);
            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                getMvpView().onError(throwable.getMessage());

                // handle load accounts error here
                if (throwable instanceof ANError) {
                    ANError anError = (ANError) throwable;
                    handleApiError(anError);
                }
            }
        });
    }

    @Override
    public void loadDefaultPostcode() {
        final String postcode = getDataManager().getDefaultPostcode();
        if (postcode != null && !postcode.isEmpty()) {
            getMvpView().showDefaultPostcode(postcode);
            return;
        }

        doApiCallForResponse(getDataManager().getPostcodeDefault(), new AppApiCallback() {
            @Override
            public void onSuccess(Object object) {
                super.onSuccess(object);

                if (object instanceof GetPostcodeDefaultResponse) {
                    getMvpView().showDefaultPostcode(((GetPostcodeDefaultResponse) object).getPostcode());
                    setDefaultPostcode(((GetPostcodeDefaultResponse) object).getPostcode());
                } else {
                    getMvpView().showDefaultPostcode(null);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                getMvpView().showDefaultPostcode(null);
            }
        });
    }

    @Override
    public void setDefaultPostcode(String postcode) {
        getDataManager().setDefaultPostcode(postcode);
    }

    @Override
    public void loadPreviewShippingPrice(String postcode, String skuid, float price, int weight, int width, int height, Integer operation) {
        doApiCallForResponse(getDataManager().getPostcodeShippingPrice(postcode, skuid, price, weight, width, height), new AppApiCallback() {
            @Override
            public void onSuccess(Object object) {
                super.onSuccess(object);

                if (object instanceof GetPostcodeShippingPriceResponse) {
                    getMvpView().showPreviewShippingPrice((GetPostcodeShippingPriceResponse) object, postcode, operation);
                } else {
                    getMvpView().showPreviewShippingPrice(null, postcode, operation);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                getMvpView().showPreviewShippingPrice(null, postcode, operation);
            }
        });
    }
}
