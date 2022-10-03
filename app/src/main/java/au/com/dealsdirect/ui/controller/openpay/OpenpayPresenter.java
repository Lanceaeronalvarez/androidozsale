package au.com.dealsdirect.ui.controller.openpay;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.openpay.CreateOpenpayOrderRequest;
import au.com.dealsdirect.data.network.model.openpay.OpenpayCapturePaymentRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutPresenter;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class OpenpayPresenter<V extends OpenpayMvpView> extends BasePresenter<V> implements
        OpenpayMvpPresenter<V> {

    private boolean mIsBusy = false;

    @Inject
    public OpenpayPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void createOpenpayOrder() {
        CreateOpenpayOrderRequest request = new CreateOpenpayOrderRequest();
        request.setCountryId(getDataManager().getCountryId());
        request.setLanguageId(getDataManager().getLanguageId());
        request.setRedirectUrl(getRedirectUrlPrefix());

        getMvpView().showProgressIndicator();
        mIsBusy = true;

        getCompositeDisposable().add(getDataManager()
                .callCreateOpenpayOrder(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    getMvpView().hideProgressIndicator();
                    mIsBusy = false;

                    if (response == null) {
                        return;
                    }

                    if (!response.d.getResult()) {
                        getMvpView().showError(response.d.getMessage());
                    }

                    final boolean isSuccess = response.d.getValue().isSuccess();
                    final String planId = response.d.getValue().getPlanId();
                    final String orderId = response.d.getValue().getOrderId();
                    final String postUrl = response.d.getValue().getPostUrl();
                    final String error = response.d.getValue().getError();

                    if (isSuccess) {
                        getMvpView().showOpenpayWebView(planId, orderId, postUrl);
                    } else {
                        getMvpView().showError(error);
                    }
                }, throwable -> {

                    getMvpView().hideProgressIndicator();
                    mIsBusy = false;

                    Log.e(CheckoutPresenter.class.toString(), throwable.toString());
                    getMvpView().showError(null);
                })
        );
    }

    @Override
    public void capturePaymentRequest(String planId, String orderId, String status) {
        OpenpayCapturePaymentRequest request = new OpenpayCapturePaymentRequest();
        request.setCountryId(getDataManager().getCountryId());
        request.setLanguageId(getDataManager().getLanguageId());
        request.setData(planId, orderId, status);

        getMvpView().showProgressIndicator();
        mIsBusy = true;

        getCompositeDisposable().add(getDataManager()
                .callOpenpayCapturePayment(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideProgressIndicator();
                    mIsBusy = false;

                    try {
                        JSONObject jsonResponse = response.getJSONObject("d");

                        if (jsonResponse.getBoolean("Result") && jsonResponse.getBoolean("IsAuthenticated")) {

                            boolean isPaid = jsonResponse.getJSONObject("Value").getBoolean("isPaid");

                            if (isPaid) {
                                String invoiceNo = jsonResponse.getJSONObject("Value").getString("invoiceNo");
                                String address = jsonResponse.getJSONObject("Value").getString("AddressString");
                                String delivery = jsonResponse.getJSONObject("Value").getJSONObject("OrderInfoResult").getString("EstimatedDeliveryText");
                                Double price = jsonResponse.getJSONObject("Value").getJSONObject("OrderInfoResult").getDouble("Total");
                                Double shipping = jsonResponse.getJSONObject("Value").getJSONObject("OrderInfoResult").getDouble("Shipping");
                                getMvpView().showPaymentSuccess(address, price, shipping, invoiceNo, delivery);
                            } else {
                                getMvpView().showError(jsonResponse.getString("Message"));
                            }

                        } else {
                            String message = jsonResponse.getString("Message");
                            getMvpView().showError(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, throwable -> {

                    getMvpView().hideProgressIndicator();
                    mIsBusy = false;

                    Log.e(CheckoutPresenter.class.toString(), throwable.toString());
                    getMvpView().showError(null);
                })
        );
    }

    @Override
    public String getCountryIso() {
        return getDataManager().getCountryIso();
    }

    @Override
    public boolean isBusy() {
        return mIsBusy;
    }

    @Override
    public String getRedirectUrlPrefix() {
        String url = Settings.getSelectedCountry().genieRoot;
        if (url.charAt(url.length() - 1) != '/') {
            url += "/";
        }
        url += "checkout.aspx?cid=10";
        return url;
    }
}
