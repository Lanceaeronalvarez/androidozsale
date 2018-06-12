package au.com.dealsdirect.ui.controller.address.viewaddress;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.address.ApplyAddress;
import au.com.dealsdirect.data.network.model.address.ApplyAddressRequest;
import au.com.dealsdirect.data.network.model.address.ApplyAddressResponse;
import au.com.dealsdirect.data.network.model.address.DeleteUserAddress;
import au.com.dealsdirect.data.network.model.address.GetAddresses;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * Created by smartwave on 20/06/2017.
 */

public class ViewAddressPresenter<V extends ViewAddressMvpView> extends BasePresenter<V> implements ViewAddressMvpPresenter<V> {
    @Inject
    public ViewAddressPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadAddresses() {
        if(getDataManager().isAuthorized()) {
            getMvpView().showLoading();
            doApiCallForResponse(getDataManager().callGetUserAddresses(
                    new GetAddresses.RequestValues(getDataManager().getLanguageId())), new AppApiCallback() {
                @Override
                public void onSuccess(Object response) {
                    super.onSuccess(response);
                    if (((GetAddresses.ResponseValue) response).getD().getResult()) {
                        getMvpView().showAddresses(((GetAddresses.ResponseValue) response));
                    } else {
                        getMvpView().onError(((GetAddresses.ResponseValue) response).getD().getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void applyDeliveryAddress(String deliveryAddressId) {

        ApplyAddressRequest applyAddressRequest = new ApplyAddressRequest();
        applyAddressRequest.countryID = getDataManager().getCountryId();
        applyAddressRequest.languageID = getDataManager().getLanguageId();
        applyAddressRequest.imageSize = "100";
        applyAddressRequest.deliveryAddressID = deliveryAddressId;

        doApiCallForResponse(getDataManager().callApplyDeliveryAddress(applyAddressRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                if (((ApplyAddressResponse) response).d.getResult()) {
//                            GDebug.log("DEBUG", "ApplyDeliveryAddress success");
                    getMvpView().backToCheckout();
                }
            }
        });
    }

    @Override
    public void deleteUserDeliveryAddress(String deliveryAddressId) {
        doApiCallForResponse(getDataManager().callDeleteUserDeliveryAddress(
                new DeleteUserAddress.RequestValues(deliveryAddressId)), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                if (response != null) {

                    Timber.d("remove address", " result = " + ((DeleteUserAddress.ResponseValue) response).d.getResult()
                            + " , " + ((DeleteUserAddress.ResponseValue) response).d.getMessage());

                    if (((DeleteUserAddress.ResponseValue) response).d.getResult()) {
                        Timber.d("remove address", "DeleteUserDeliveryAddress " + "success");
                        getMvpView().onUserDeliveryAddressDeleted(((DeleteUserAddress.ResponseValue) response));
                    }
                }
            }
        });
    }
}
