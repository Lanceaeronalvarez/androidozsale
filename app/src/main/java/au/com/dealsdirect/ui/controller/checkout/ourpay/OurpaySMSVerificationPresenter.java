package au.com.dealsdirect.ui.controller.checkout.ourpay;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.util.Log;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.ourpayverificationcodeconfirm.VerificationCodeConfirmRequest;
import au.com.dealsdirect.data.network.model.ourpayverificationnormalizephone.VerificationNormalizePhoneRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class OurpaySMSVerificationPresenter<V extends OurpaySMSVerificationMvpView> extends BasePresenter<V> implements OurpaySMSVerificationMvpPresenter<V> {

    @Inject
    public OurpaySMSVerificationPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void callNormalizePhone(String phone) {

        VerificationNormalizePhoneRequest verificationNormalizePhoneRequest = new VerificationNormalizePhoneRequest();
        verificationNormalizePhoneRequest.setCountryID(getDataManager().getCountryId());
        verificationNormalizePhoneRequest.setLanguageID(getDataManager().getLanguageId());
        verificationNormalizePhoneRequest.setPhone(phone);

        getMvpView().loadExtension();
        getCompositeDisposable().add(getDataManager()
                .callNormalizePhone(verificationNormalizePhoneRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {
                    Log.d("smsverify", "normalize response = "+responseValue.getVerificationNormalizePhoneResponse().getMessage() + " , "+
                            responseValue.getVerificationNormalizePhoneResponse().getIsAuthenticated()+" , "+
                            responseValue.getVerificationNormalizePhoneResponse().getResult()+ " , "+
                            responseValue.getVerificationNormalizePhoneResponse().getValue().getErrorMessage()+" ,  "+
                            responseValue.getVerificationNormalizePhoneResponse().getValue().getCountryCode()+ " , "+
                            responseValue.getVerificationNormalizePhoneResponse().getValue().getPhone());
                    getMvpView().callNormalizePhoneResponse(responseValue);
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
                }));
    }

    @Override
    public void callVerificationCodeSend(String phone, String countryCode) {

        VerificationNormalizePhoneRequest verificationNormalizePhoneRequest = new VerificationNormalizePhoneRequest();
        verificationNormalizePhoneRequest.setCountryID(countryCode);
        verificationNormalizePhoneRequest.setLanguageID(getDataManager().getLanguageId());
        verificationNormalizePhoneRequest.setPhone(phone);

        getCompositeDisposable().add(getDataManager()
                .callVerificationCodeSend(verificationNormalizePhoneRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {
                    getMvpView().callVerificationCodeSendResponse(responseValue);

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
                }));
    }

    @Override
    public void callVerificationCodeConfirm(String phone, String countryCode, String code) {

        VerificationCodeConfirmRequest verificationCodeConfirmRequest = new VerificationCodeConfirmRequest();
        verificationCodeConfirmRequest.setCountryID(getDataManager().getCountryId());
        verificationCodeConfirmRequest.setLanguageID(getDataManager().getLanguageId());
        verificationCodeConfirmRequest.setPhone(String.format("%s%s", countryCode, phone));
        verificationCodeConfirmRequest.setCode(code);
        Log.d("verification", "values = "+countryCode+ " , "+phone+", "+getDataManager().getCountryId()+" , "+getDataManager().getLanguageId()+ " ,"+code);

        getCompositeDisposable().add(getDataManager()
                .callVerificationCodeConfirm(verificationCodeConfirmRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {
                    Log.d("smsverify", "verify response = "+responseValue.getVerificationCodeConfirmResponse().getMessage() + " , "+
                    responseValue.getVerificationCodeConfirmResponse().getIsAuthenticated()+" , "+
                    responseValue.getVerificationCodeConfirmResponse().getResult()+ " , ");

                    getMvpView().callVerificationCodeConfirmResponse(responseValue);

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        Log.d("smsverify", "error response = "+throwable.getMessage());

                        getMvpView().hideLoading();
                        getMvpView().onError(throwable.getMessage());

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                }));
    }
}
