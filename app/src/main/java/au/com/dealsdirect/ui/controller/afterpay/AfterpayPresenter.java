package au.com.dealsdirect.ui.controller.afterpay;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.afterpay.AfterPayCreatePaymentRequest;
import au.com.dealsdirect.data.network.model.afterpay.CreateAfterpayOrderRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutPresenter;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class AfterpayPresenter<V extends AfterpayMvpView> extends BasePresenter<V> implements
        AfterpayMvpPresenter<V> {

    private boolean mIsBusy = false;

    @Inject
    public AfterpayPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                             CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void createAfterpayOrder() {
        if (!isViewAttached()) {
            return;
        }
        CreateAfterpayOrderRequest request = new CreateAfterpayOrderRequest();
        request.setCountryId(getDataManager().getCountryId());
        request.setLanguageId(getDataManager().getLanguageId());
        request.setRedirectUrl(getRedirectUrlPrefix());

        getMvpView().showProgressIndicator();
        mIsBusy = true;

        getCompositeDisposable().add(getDataManager()
                .createAfterpayOrder(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    mIsBusy = false;

                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideProgressIndicator();

                    if (response.d.getValue().isSuccess()) {
                        getMvpView().showAfterpayWebView(response.d.getValue().getToken());
                    } else {
                        getMvpView().showError(response.d.getValue().getError());
                    }
                }, throwable -> {
                    mIsBusy = false;

                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideProgressIndicator();

                    Log.e(CheckoutPresenter.class.toString(), throwable.toString());
                    getMvpView().showError(null);
                })
        );
    }

    @Override
    public void payWithAfterpay(String token) {
        if (!isViewAttached()) {
            return;
        }
        AfterPayCreatePaymentRequest request = new AfterPayCreatePaymentRequest();
        request.setCountryId(getDataManager().getCountryId());
        request.setLanguageId(getDataManager().getLanguageId());
        request.setToken(token);

        getMvpView().showProgressIndicator();
        mIsBusy = true;

        getCompositeDisposable().add(getDataManager()
                .callAfterPayCreatePayment(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    mIsBusy = false;

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideProgressIndicator();

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
                    }

                }, throwable -> {
                    mIsBusy = false;

                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideProgressIndicator();

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
    public String getAfterpayScriptUri() {
        return getDataManager().getAfterpayScriptUri();
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
