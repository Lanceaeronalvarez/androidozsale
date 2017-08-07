package au.com.dealsdirect.ui.controller.masterpass;
/*
 * Created by CodeineBot on 8/3/17.
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class MasterpassPresenter<V extends MasterpassMvpView> extends BasePresenter<V> implements MasterpassMvpPresenter<V> {

    @Inject
    public MasterpassPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void getMasterpassPayment() {
        HashMap<String, Object> param = new HashMap<>();
        param.put("isMobile", true);

        doApiCallForResponse(getDataManager().callMasterpassPayment(param), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                if (!isViewAttached()) {
                    return;
                }

                getMvpView().hideLoadingDialog();

                try {
                    JSONObject jsonResponse = ((JSONObject) response).getJSONObject("d");

                    if (jsonResponse.getBoolean("Result") && jsonResponse.getBoolean("IsAuthenticated")) {

                        String paymentUrl = jsonResponse.getJSONObject("Value").getString("PaymentURL");
                        try {
                            URL url = new URL(paymentUrl);
                            getMvpView().loadMasterpassUrl(paymentUrl, url.getHost());

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        getMvpView().showError(jsonResponse.getString("Message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void confirmPayment(String oAuthToken, String oAuthVerifier, String checkoutResourceUrl) {

        JSONObject masterPassData = new JSONObject();
        try {
            masterPassData.put("OAuthToken", oAuthToken);
            masterPassData.put("OAuthVerifier", oAuthVerifier);
            masterPassData.put("CheckoutResourceUrl", checkoutResourceUrl);
            masterPassData.put("IsMobile", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, Object> param = new HashMap<>();
        param.put("data", masterPassData);

        doApiCallForResponse(getDataManager().callMasterpassPostTransaction(param), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                if (!isViewAttached()) {
                    return;
                }

                getMvpView().hideLoadingDialog();

                try {
                    JSONObject jsonResponse = ((JSONObject) response).getJSONObject("d");

                    if (jsonResponse.getBoolean("Result") && jsonResponse.getBoolean("IsAuthenticated")) {

                        String invoiceNo = jsonResponse.getJSONObject("Value").getString("invoiceNo");
                        String address = jsonResponse.getJSONObject("Value").getString("AddressString");
                        String delivery = jsonResponse.getJSONObject("Value").getString("EstimatedDeliveryText");
                        String price = String.valueOf(jsonResponse.getJSONObject("Value").getJSONObject("OrderInfoResult").getDouble("Total"));
                        getMvpView().showPaymentSuccess(address, price, invoiceNo, delivery);

                    } else {
                        String message = jsonResponse.getString("Message");
                        getMvpView().showError(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
