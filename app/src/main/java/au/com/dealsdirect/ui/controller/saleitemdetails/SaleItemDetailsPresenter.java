package au.com.dealsdirect.ui.controller.saleitemdetails;

import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.ApiCallback;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.checkout.BasketQuantityResponse;
import au.com.dealsdirect.data.network.model.ourpaydata.OurpayDataRequest;
import au.com.dealsdirect.data.network.model.ourpaydata.OurpayDataResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayError;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.AppEventHelper;
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
    public void loadSaleItemDetails(String saleId, String seoIdentifierId) {

        Observable<GetSaleItemDetailsResponse> callGetSaleItemDetailObservable = saleId == null || saleId.isEmpty() ?
                getDataManager().callGetSaleItemDetails(seoIdentifierId) :
                getDataManager().callGetSaleItemDetails(saleId, seoIdentifierId);

        doApiCallForResponse(callGetSaleItemDetailObservable, new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                GetSaleItemDetailsResponse getSaleItemDetailsResponse = (GetSaleItemDetailsResponse) response;

                if (response != null) {
                    getMvpView().showSaleDetails(getSaleItemDetailsResponse);
                }

                getMvpView().hideLoading();

                AppEventHelper.viewedContent(getSaleItemDetailsResponse.getSkuId(), getSaleItemDetailsResponse.getName(),
                        getSaleItemDetailsResponse.getPrice().getValue(), getDataManager().getCountryId());
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
    public void loadOurpayData(final GetSaleItemDetailsResponse value) {
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

    @Override
    public void addToCart(AddToCartRequest requestValues) {
        getMvpView().showLoading();

        doApiCallForResponse(getDataManager()
                .callAddItemToCart(requestValues), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                getMvpView().showAddToCartResponse(((AddToCartResponse.Response) response).getValue());

                AppEventHelper.addedToCart(requestValues.getSkuId(), requestValues.getItemName(),
                        requestValues.getPrice(), getDataManager().getCountryId());
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
    public void generateOurpay(GetSaleItemDetailsResponse value, OurpayDataResponse ourpayDataResponse) {
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
                if(o != null) getMvpView().setDynamicDiscount((String) o);
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
}
