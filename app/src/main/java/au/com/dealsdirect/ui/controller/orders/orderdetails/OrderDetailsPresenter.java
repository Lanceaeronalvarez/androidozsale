package au.com.dealsdirect.ui.controller.orders.orderdetails;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.address.ChangeDeliveryAddressRequest;
import au.com.dealsdirect.data.network.model.orders.CancelInvoiceItemRequest;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedRequest;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedSatisfactionResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsPresenter<V extends OrderDetailsMvpView> extends BasePresenter<V> implements OrderDetailsMvpPresenter<V> {

    private static final int SHOW_LOADING_DELAY = 2000;

    private Disposable setOrderReceivedRequestDisposable = null;

    @Inject
    public OrderDetailsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);

    }

    @Override
    public void loadOrderDetails(int orderNumber) {
        getMvpView().showLoading();


        doApiCallForResponse(getDataManager().callGetOrderDetails(orderNumber), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                GetOrdersResponse.Order responseValue = (GetOrdersResponse.Order) response;

                getMvpView().showOrderDetails(responseValue);
            }
        });
    }

    @Override
    public void showTrackingWeb(String link) {
        getMvpView().showOrderTrackingWeb(link);
    }

    @Override
    public void callSetOrderReceived(OrderReceivedRequest receivedRequest) {
        cancelPreviousSetOrderReceivedRequest();
        setOrderReceivedRequestDisposable = doApiCallForResponse(
                getDataManager().callSetOrderReceived(receivedRequest), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        getMvpView().onReceivedSet(receivedRequest.getInvoiceNumber());
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
                        getMvpView().onReceivedSet(receivedRequest.getInvoiceNumber());
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
                getMvpView().orderSatisfactionReceived(receivedRequest.getInvoiceNumber(), response instanceof OrderReceivedSatisfactionResponse && ((OrderReceivedSatisfactionResponse) response).getHasRating());

            }
        });
    }

    @Override
    public void cancelInvoiceItem(CancelInvoiceItemRequest request) {
        getMvpView().showLoadingDelayed(SHOW_LOADING_DELAY);
        doApiCallForResponse(getDataManager().callCancelInvoiceItem(request), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().hideLoading();
                getMvpView().showOrderDetails((GetOrdersResponse.Order) response);
            }
        });
    }

    @Override
    public void changeDeliveryAddress(ChangeDeliveryAddressRequest changeDeliveryAddressRequest) {
        doApiCallForResponse(getDataManager().callChangeDeliveryAddress(changeDeliveryAddressRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().addressChanged();
            }
        });
    }
}
