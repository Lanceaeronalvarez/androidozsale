package au.com.dealsdirect.ui.controller.lpay;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.lpay.ConfirmLPayTransactionRequest;
import au.com.dealsdirect.data.network.model.lpay.CreateLPayOrderRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutPresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class LPayPresenter<V extends LPayMvpView> extends BasePresenter<V> implements
        LPayMvpPresenter<V> {

    private boolean mIsBusy = false;

    @Inject
    public LPayPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                         CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void createLPayOrder(String redirectUri) {
        getMvpView().showProgressIndicator();
        mIsBusy = true;

        CreateLPayOrderRequest request = new CreateLPayOrderRequest(redirectUri);
        request.setCountryId(getDataManager().getCountryId());
        request.setLanguageId(getDataManager().getLanguageId());

        getCompositeDisposable().add(getDataManager()
                .callCreateLPayOrder(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    mIsBusy = false;

                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideProgressIndicator();

                    if (response.getD().getResult()) {
                        getMvpView().showLPayWebView(response.getD().getValue().getPaymentUrl());
                    } else {
                        getMvpView().hideLPayWebView();
                        getMvpView().showError(response.getD().getMessage());
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
    public void confirmLPayTransaction(String token, String signature, String reference) {
        getMvpView().showProgressIndicator();
        mIsBusy = true;

        ConfirmLPayTransactionRequest request = new ConfirmLPayTransactionRequest(token, signature, reference);
        request.setCountryId(getDataManager().getCountryId());
        request.setLanguageId(getDataManager().getLanguageId());

        getCompositeDisposable().add(getDataManager()
                .callConfirmLPayTransaction(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    mIsBusy = false;

                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideProgressIndicator();

                    //TODO: unify payment success response objects

                    try {
                        JSONObject jsonResponse = ((JSONObject) response).getJSONObject("d");

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
                        getMvpView().showError("Unexpected error occurred");
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
    public boolean isBusy() {
        return mIsBusy;
    }
}
