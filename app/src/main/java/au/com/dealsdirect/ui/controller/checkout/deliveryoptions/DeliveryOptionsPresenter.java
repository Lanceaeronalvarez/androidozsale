package au.com.dealsdirect.ui.controller.checkout.deliveryoptions;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.service.deliveryoptions.DeliveryOptions;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.LoadingDialogType;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class DeliveryOptionsPresenter<V extends DeliveryOptionsMvpView> extends BasePresenter<V> implements DeliveryOptionsMvpPresenter<V> {

    @Inject
    public DeliveryOptionsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public String getStandardTitleText() {
        return getDataManager().getStoredTemplateTexts(DeliveryOptions.KEY_DELIVERYOPTION_STANDARD_TITLE);
    }

    @Override
    public String getExpressTitleText() {
        return getDataManager().getStoredTemplateTexts(DeliveryOptions.KEY_DELIVERYOPTION_EXPRESS_TITLE);
    }

    @Override
    public String getExpressDescriptionText() {
        return getDataManager().getStoredTemplateTexts(DeliveryOptions.KEY_DELIVERYOPTION_EXPRESS_DESCRIPTION);
    }

    @Override
    public void setDeliveryOption(SetDeliveryOption.OptionParameters setDeliveryOptionParameters) {
        SetDeliveryOption setDeliveryOption = new SetDeliveryOption();
        setDeliveryOption.setCountryId(getDataManager().getCountryId());
        setDeliveryOption.setLanguageId(getDataManager().getLanguageId());
        setDeliveryOption.setOptionParameters(setDeliveryOptionParameters);
        setDeliveryOption.setImageSize(AppConstants.IMAGE_SIZE);
        setDeliveryOption.setDeliveryDetails(new SetDeliveryOption.DeliveryDetails(null, null));
        setDeliveryOption.setPostcode(null);

        getMvpView().showLoading(LoadingDialogType.DEFAULT);
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
}
