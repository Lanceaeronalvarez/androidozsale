package au.com.dealsdirect.ui.controller.klarna;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.checkout.klarna.KlarnaCreateOrderRequest;
import au.com.dealsdirect.data.network.model.checkout.klarna.KlarnaCreateOrderResponse;
import au.com.dealsdirect.data.network.model.checkout.klarna.KlarnaCreateSessionRequest;
import au.com.dealsdirect.data.network.model.checkout.klarna.KlarnaCreateSessionResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class KlarnaPresenter<V extends KlarnaMvpView> extends BasePresenter<V> implements
        KlarnaMvpPresenter<V> {

    private boolean mIsBusy = false;

    @Inject
    public KlarnaPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                           CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void createKlarnaSession() {
        getMvpView().showProgressIndicator();
        KlarnaCreateSessionRequest request = new KlarnaCreateSessionRequest(
                getDataManager().getCountryId(),
                getDataManager().getLanguageId());
        doApiCallForResponse(getDataManager().callCreateKlarnaSession(request),
                new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideProgressIndicator();
                        if (response instanceof KlarnaCreateSessionResponse) {
                            getMvpView().showKlarnaPaymentView((KlarnaCreateSessionResponse) response);
                        } else {
                            getMvpView().hideKlarnaPaymentView();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        if (isViewAttached()) {
                            getMvpView().hideProgressIndicator();
                            getMvpView().hideKlarnaPaymentView();
                        }
                    }
                });
    }

    @Override
    public void createKlarnaOrder(String authorizationToken) {
        getMvpView().showProgressIndicator();
        KlarnaCreateOrderRequest request = new KlarnaCreateOrderRequest(
                getDataManager().getCountryId(),
                getDataManager().getLanguageId(),
                authorizationToken);
        doApiCallForResponse(getDataManager().callCreateKlarnaOrder(request),
                new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideProgressIndicator();
                        if (response instanceof KlarnaCreateOrderResponse) {
                            KlarnaCreateOrderResponse.Value value = ((KlarnaCreateOrderResponse) response).getD().getValue();
                            if (value.getTransactionStatus().equalsIgnoreCase("true")) {
                                getMvpView().showPaymentSuccess(
                                        value.getAddressString(),
                                        value.getOrderInfoResult().getTotal(),
                                        value.getOrderInfoResult().getShipping(),
                                        value.getInvoiceNo(),
                                        value.getOrderInfoResult().getEstimatedDeliveryText());
                            } else {
                                getMvpView().showError(value.getErrorMessage());
                            }
                        } else {
                            getMvpView().showError("Unknown Error");
                        }
                        getMvpView().hideKlarnaPaymentView();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        if (isViewAttached()) {
                            getMvpView().hideProgressIndicator();
                            getMvpView().hideKlarnaPaymentView();
                        }
                    }
                });
    }

    @Override
    public boolean isBusy() {
        return mIsBusy;
    }
}
