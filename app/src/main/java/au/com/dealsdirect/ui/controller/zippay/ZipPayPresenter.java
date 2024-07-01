package au.com.dealsdirect.ui.controller.zippay;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.zippay.ZipCreateChargeRequest;
import au.com.dealsdirect.data.network.model.zippay.ZipCreateCheckoutRequest;
import au.com.dealsdirect.data.network.model.zippay.ZipCreateCheckoutResponse;
import au.com.dealsdirect.data.network.model.zippay.ZipPayConfirmNzOrderRequest;
import au.com.dealsdirect.data.network.model.zippay.ZipPayCreateNzOrderRequest;
import au.com.dealsdirect.data.network.model.zippay.ZipPayCreateNzOrderResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class ZipPayPresenter<V extends ZipPayMvpView> extends BasePresenter<V> implements
        ZipPayMvpPresenter<V> {

    private boolean mIsBusy = false;

    @Inject
    public ZipPayPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                           CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void createOrder(String redirectUrl) {
        final String countryId = getDataManager().getCountryId();
        if (countryId.equalsIgnoreCase("AS")) {
            createAUOrder(redirectUrl);
        } else {
            createNZOrder(redirectUrl);
        }
    }

    private void createAUOrder(String redirectUrl) {
        mIsBusy = true;
        ZipCreateCheckoutRequest request = new ZipCreateCheckoutRequest(
                getDataManager().getCountryId(),
                getDataManager().getLanguageId(),
                redirectUrl
        );

        getMvpView().showProgressIndicator();
        doApiCallForResponse(getDataManager().callCreateZipCheckout(request), new AppApiCallback() {

            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                mIsBusy = false;
                final ZipCreateCheckoutResponse response = (ZipCreateCheckoutResponse) o;
                if (isViewAttached()) {
                    if (response == null || response.getUri() == null) {
                        getMvpView().showError("Error");
                    } else {
                        getMvpView().receivedAUOrderRequest(response.getUri());
                    }
                    getMvpView().hideProgressIndicator();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                mIsBusy = false;
                if (isViewAttached()) {
                    getMvpView().showError("Error");
                    getMvpView().hideProgressIndicator();
                }

            }
        });
    }


    private void createNZOrder(String redirectUrl) {
        mIsBusy = true;
        ZipPayCreateNzOrderRequest request = new ZipPayCreateNzOrderRequest(
                getDataManager().getCountryId(),
                getDataManager().getLanguageId(),
                redirectUrl
        );

        getMvpView().showProgressIndicator();
        doApiCallForResponse(getDataManager().callCreateZipPayNzOrder(request), new AppApiCallback() {

            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                mIsBusy = false;
                final ZipPayCreateNzOrderResponse response = (ZipPayCreateNzOrderResponse) o;
                if (isViewAttached()) {
                    if (response == null ||
                            response.getRedirectUrl() == null ||
                            response.getOrderId() == null ||
                            response.getToken() == null) {
                        getMvpView().showError("Error");
                    } else {
                        getMvpView().receivedNZOrderRequest(response.getRedirectUrl());
                    }
                    getMvpView().hideProgressIndicator();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                mIsBusy = false;
                if (isViewAttached()) {
                    getMvpView().showError("Error");
                    getMvpView().hideProgressIndicator();
                }

            }
        });
    }

    @Override
    public void createAUCharge(String checkoutId, String zipOrderId, String paymentStatus) {
        mIsBusy = true;
        ZipCreateChargeRequest request = new ZipCreateChargeRequest(
                getDataManager().getCountryId(),
                getDataManager().getLanguageId(),
                checkoutId, zipOrderId, paymentStatus
        );

        getMvpView().showProgressIndicator();
        doApiCallForResponse(getDataManager().callCreateZipCharge(request), new AppApiCallback() {

            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                mIsBusy = false;
                if (!isViewAttached()) {
                    return;
                }
                getMvpView().hideProgressIndicator();
                handlePaymentResponse(o);
            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                mIsBusy = false;
                if (isViewAttached()) {
                    getMvpView().showError("Error");
                    getMvpView().hideProgressIndicator();
                }

            }
        });
    }

    @Override
    public void confirmNZOrder(String paymentStatus, String zipOrderId, String token) {
        mIsBusy = true;
        ZipPayConfirmNzOrderRequest request = new ZipPayConfirmNzOrderRequest(
                getDataManager().getCountryId(),
                getDataManager().getLanguageId(),
                paymentStatus,
                zipOrderId,
                token
        );

        getMvpView().showProgressIndicator();
        doApiCallForResponse(getDataManager().callConfirmZipPayNzOrder(request), new AppApiCallback() {

            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                mIsBusy = false;
                if (!isViewAttached()) {
                    return;
                }
                getMvpView().hideProgressIndicator();
                handlePaymentResponse(o);
            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                mIsBusy = false;
                if (isViewAttached()) {
                    getMvpView().showError("Error");
                    getMvpView().hideProgressIndicator();
                }

            }
        });
    }

    private void handlePaymentResponse(Object o) {
        try {
            JSONObject jsonResponse = ((JSONObject) o).getJSONObject("d");

            if (jsonResponse.getBoolean("Result") && jsonResponse.getBoolean("IsAuthenticated")) {

                String invoiceNo = jsonResponse.getJSONObject("Value").getString("invoiceNo");
                String address = jsonResponse.getJSONObject("Value").getString("AddressString");
                String delivery = jsonResponse.getJSONObject("Value").getJSONObject("OrderInfoResult").getString("EstimatedDeliveryText");
                Double price = jsonResponse.getJSONObject("Value").getJSONObject("OrderInfoResult").getDouble("Total");
                Double shipping = jsonResponse.getJSONObject("Value").getJSONObject("OrderInfoResult").getDouble("Shipping");
                getMvpView().showPaymentSuccess(address, price, shipping, invoiceNo, delivery);

            } else {
                String message = jsonResponse.getString("Message");
                getMvpView().showError(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isBusy() {
        return mIsBusy;
    }
}
