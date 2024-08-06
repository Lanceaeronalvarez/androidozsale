package au.com.dealsdirect.ui.controller.orders.orders;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.address.ChangeDeliveryAddressRequest;
import au.com.dealsdirect.data.network.model.error.ErrorResponse;
import au.com.dealsdirect.data.network.model.orders.CancelInvoiceItemRequest;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedRequest;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedSatisfactionResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersPresenter<V extends OrdersMvpView> extends BasePresenter<V> implements OrdersMvpPresenter<V> {

    private static final int SHOW_LOADING_DELAY = 2000;

    private Disposable setOrderReceivedRequestDisposable = null;

    @Inject
    public OrdersPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadOrders() {
        doApiCallForResponse(getDataManager()
                .callGetOrders(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                getMvpView().showOrders((List<GetOrdersResponse.Order>) response);
            }
        });
    }

    @Override
    public void loadOrders(String datetime, int months) {
        doApiCallForResponse(getDataManager()
                .callGetOrdersHistory(datetime, months), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().showOrders((GetOrdersResponse) response);
            }
        });
    }

    @Override
    public void callSetOrderReceived(OrderReceivedRequest receivedRequest) {
        cancelPreviousSetOrderReceivedRequest();
        setOrderReceivedRequestDisposable = doApiCallForResponse(
                getDataManager().callSetOrderReceived(receivedRequest), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                    }
                });
    }

    @Override
    public void callSetOrderNotReceived(OrderReceivedRequest receivedRequest) {
        cancelPreviousSetOrderReceivedRequest();
        setOrderReceivedRequestDisposable = doApiCallForResponse(
                getDataManager().callSetOrderNotReceived(receivedRequest), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                    }
                });
    }

    private void cancelPreviousSetOrderReceivedRequest() {
        if (setOrderReceivedRequestDisposable != null) {
            getCompositeDisposable().delete(setOrderReceivedRequestDisposable);
            setOrderReceivedRequestDisposable = null;
        }
    }

    @Override
    public void callGetOrderReceivedSatisfaction(OrderReceivedRequest receivedRequest) {
        doApiCallForResponse(getDataManager().callGetOrderReceivedSatisfaction(receivedRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().orderSatisfactionReceived(
                        receivedRequest,
                        response instanceof OrderReceivedSatisfactionResponse && ((OrderReceivedSatisfactionResponse) response).getHasRating());
            }
        });
    }

    @Override
    public void cancelInvoice(CancelInvoiceItemRequest request) {
        getMvpView().showLoadingDelayed(SHOW_LOADING_DELAY);
        doApiCallForResponse(getDataManager().callCancelInvoice(request), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().hideLoading();
                getMvpView().showOrder((GetOrdersResponse.Order) response);
            }
        });
    }

    @Override
    public void changeDeliveryAddress(ChangeDeliveryAddressRequest changeDeliveryAddressRequest) {
        doApiCallForResponse(getDataManager().callChangeDeliveryAddress(changeDeliveryAddressRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().addressChanged((String) response);
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (!isViewAttached()) {
                    return;
                }

                getMvpView().hideLoading();
                getMvpView().onError(throwable.getMessage());

                // handle load accounts error here
                if (throwable instanceof ANError) {
                    ANError anError = (ANError) throwable;
                    handleApiError(anError);

                    JsonParser parser = new JsonParser();
                    JsonElement mJson =  parser.parse(anError.getErrorBody());
                    Gson gson = new Gson();
                    ErrorResponse errorResponse = gson.fromJson(String.valueOf(mJson), ErrorResponse.class);
                    getMvpView().addressChangeError(errorResponse.getDetail());
                }
            }
        });
    }
}
