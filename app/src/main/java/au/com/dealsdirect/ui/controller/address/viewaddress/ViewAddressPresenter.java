package au.com.dealsdirect.ui.controller.address.viewaddress;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
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
        GetAddresses.RequestValues requestValue = new GetAddresses.RequestValues(getDataManager().getLanguageId());
        getCompositeDisposable().add(getDataManager()
                .callGetUserAddresses(requestValue)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetAddresses.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetAddresses.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (responseValue.d.getResult()) {
                            getMvpView().showAddresses(responseValue);
                        } else {
                            getMvpView().onError(responseValue.d.getMessage());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
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
                    }
                })
        );


    }

    @Override
    public void applyDeliveryAddress(String deliveryAddressId) {

        ApplyAddressRequest applyAddressRequest = new ApplyAddressRequest();
        applyAddressRequest.countryID = getDataManager().getCountryId();
        applyAddressRequest.languageID = getDataManager().getLanguageId();
        applyAddressRequest.imageSize = "100";
        applyAddressRequest.deliveryAddressID = deliveryAddressId;

        getCompositeDisposable().add(getDataManager()
                .callApplyDeliveryAddress(applyAddressRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<ApplyAddressResponse>() {
                    @Override
                    public void accept(@NonNull ApplyAddressResponse applyAddressResponse) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        if(applyAddressResponse.d.getResult()){
//                            GDebug.log("DEBUG", "ApplyDeliveryAddress success");
//                            mViewMyAddresses.backToCheckoutFragment();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
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
                    }
                })
        );
    }

    @Override
    public void deleteUserDeliveryAddress(String deliveryAddressId) {
        DeleteUserAddress.RequestValues requestValues = new DeleteUserAddress.RequestValues(deliveryAddressId);
        getCompositeDisposable().add(getDataManager()
                .callDeleteUserDeliveryAddress(requestValues)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<DeleteUserAddress.ResponseValue>() {
                    @Override
                    public void accept(@NonNull DeleteUserAddress.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        if (responseValue != null){

                            Timber.d("remove address", " result = "+ responseValue.d.getResult()
                                    + " , "+ responseValue.d.getMessage());
                            if (responseValue.d.getResult()) {

                                Timber.d("remove address", "DeleteUserDeliveryAddress " + "success");
                                getMvpView().onUserDeliveryAddressDeleted(responseValue);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
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
                    }
                })
        );
    }
}
