package au.com.dealsdirect.ui.controller.saleitemdetails;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayError;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.AppEventHelper;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
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
    public void loadSaleItemDetails(String seoIdentifierId) {
        getMvpView().hideLoading();

        getCompositeDisposable().add(getDataManager()
                .callGetSaleItemDetails(seoIdentifierId)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    if (response != null)
                        getMvpView().showSaleDetails(response);

                    getMvpView().hideLoading();

                    AppEventHelper.viewedContent(response.getSkuId(), response.getName(),
                            response.getPrice().getValue(), getDataManager().getCountryId());

                }, throwable -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();
//                    getMvpView().onError(throwable.getMessage());

                    // handle load accounts error here
                    if (throwable instanceof ANError) {
                        ANError anError = (ANError) throwable;
                        handleApiError(anError);
                    }
                }));

    }

    @Override
    public void addToCart(AddToCartRequest requestValues) {
        getMvpView().showLoading();

        getCompositeDisposable().add(getDataManager()
                .callAddItemToCart(requestValues)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();

                    getMvpView().showAddToCartResponse(true);

                    AppEventHelper.addedToCart(requestValues.getSkuId(), requestValues.getItemName(),
                            requestValues.getPrice(), getDataManager().getCountryId());


                }, throwable -> {

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
                }));

    }

    @Override
    public boolean isAuthorized() {
        return getDataManager().isAuthorized();
    }

    @Override
    public void generateOurpay(GetSaleItemDetailsResponse value) {
        Ourpay ourpay = new Ourpay();

        try {
            ourpay.setState(OurpayState.PRECART);

            ourpay.setUserAmount(value.getPrice().getValue());
            ourpay.setCanUse(true);
            ourpay.setBillingPeriod(value.getPaymentPlan().getBillingPeriod());
            ourpay.setTransactionCount(value.getPaymentPlan().getTransactionCount());

            ourpay.setMinAmount(value.getPaymentConditions().minAmountThreshold);
            ourpay.setMaxAmount(value.getPaymentConditions().maxAmountThreshold);

            ourpay.setAmount(value.getMyPayAmount());

            ourpay.setPlannedTransactions(value.getBillingAgreement().getPlannedTransactions());

            if (OurpayStateManager.isPriceOutOfRange(ourpay)){
                ourpay.setState(ourpay.getState() | OurpayState.ERROR);
                ourpay.setCanUse(false);
                ourpay.setErrorCode(OurpayError.AMOUNT_OUT_OF_RANGE);
            }else{
                if (ourpay.getPlannedTransactions() == null){
                    ourpay.setState(OurpayState.DISABLED);
                }
            }
        }catch (Exception ex){
            ourpay.setState(OurpayState.DISABLED);
        }

        getMvpView().showMyPayDetails(value, ourpay);


    }

    @Override
    public void callGetBasketItemsQuantity() {
        getCompositeDisposable().add(getDataManager()
                .callGetBasketItemsQuantity()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(basketQuantityResponse -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    CartUtil.setValueToCart(basketQuantityResponse.getItemQuantity());
                    getMvpView().onCallGetBasketItemsQuantity();
                }, throwable -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().onError(throwable.getMessage());

                    // handle load accounts error here
                    if (throwable instanceof ANError) {
                        ANError anError = (ANError) throwable;
                        handleApiError(anError);
                    }
                })
        );
    }
}
