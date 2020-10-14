package au.com.dealsdirect.ui.controller.checkout.deliveryoptions;

import android.util.Log;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.GetDeliveryServicePackageDetails;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by smartwave on 30/05/2018.
 */

public class DeliveryOptionsPresenter<V extends DeliveryOptionsMvpView> extends BasePresenter<V> implements DeliveryOptionsMvpPresenter<V> {

    @Inject
    public DeliveryOptionsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void getDeliveryServicePackageDetails() {
        doApiCallForResponse(getDataManager().
                callGetDeliveryServicePackageDetails(new GetDeliveryServicePackageDetails.RequestValue(getDataManager().getCountryId(), getDataManager().getLanguageId())),
                new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                GetDeliveryServicePackageDetails.ResponseValue responseValue = (GetDeliveryServicePackageDetails.ResponseValue) response;
                if (responseValue.getD().isAuthenticated() && responseValue.getD().getResult()) {
                    getMvpView().onDeliveryServicePackageDetailsLoaded(responseValue.getD().getValue());
                }
            }
        });
    }

    @Override
    public void setDeliveryOption(SetDeliveryOption.OptionParameters setDeliveryOptionParameters) {
        SetDeliveryOption setDeliveryOption = new SetDeliveryOption();
        setDeliveryOption.setCountryId(getDataManager().getCountryId());
        setDeliveryOption.setLanguageId(getDataManager().getLanguageId());
        setDeliveryOption.setOptionParameters(setDeliveryOptionParameters);
        setDeliveryOption.setImageSize(AppConstants.IMAGE_SIZE);
        setDeliveryOption.setPostcode(null);

        getMvpView().showLoading();
        doApiCallForResponse(getDataManager().callSetDeliveryOption(setDeliveryOption), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                GetCurrentOrder.ResponseValue responseValue = (GetCurrentOrder.ResponseValue) response;
                if (responseValue.getD().isAuthenticated() && responseValue.getD().getResult()) {
                    getMvpView().onSetDeliveryOption(responseValue);
                }
            }
        });
    }

    @Override
    public void onTermsAndConditionsClicked() {
        getMvpView().showTermsAndConditionsController();
    }

}
