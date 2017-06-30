package au.com.dealsdirect.ui.main;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.content.Context;
import android.content.SharedPreferences;

import com.androidnetworking.error.ANError;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.google.gson.Gson;
import com.mysale.genie.utility.Prefs;
import com.mysale.genie.utility.RxBus;
import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;
import com.mysale.genie.utility.config.model.getappsettingssection.Android;
import com.mysale.genie.utility.config.model.getappsettingssection.Payload;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentMethod;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.GetPaymentToken;
import au.com.dealsdirect.data.network.model.login.LoginEmail;
import au.com.dealsdirect.data.network.model.login.LoginTicket;
import au.com.dealsdirect.data.network.model.login.Logout;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V> implements MainMvpPresenter<V> {

    @Inject
    public MainPresenter(DataManager dataManager,
                         SchedulerProvider schedulerProvider,
                         CompositeDisposable compositeDisposable) {

        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void initServerSettings(Context context, String countryId) {
        getCompositeDisposable().add(getDataManager()
                .callGetServerSettings(context, countryId)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetServerSettings.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetServerSettings.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        com.mysale.genie.utility.config.model.getserversettings.Value value = responseValue.d.getValue();
                        if (value != null) {
                            getDataManager().setCountryId(responseValue.getCountryId());
                            getDataManager().setLanguageId(responseValue.getLanguages().get(0).getID());
                            Gson gson = new Gson();
                            getDataManager().setLanguages(gson.toJson(responseValue.getLanguages()));
                            getDataManager().setSiteName(responseValue.getSiteFullname());
                            getDataManager().setCurrency(responseValue.getCurrency());
                            getDataManager().setCurrencySign(responseValue.getCurrencySign());
                            getDataManager().setFollowUsFbLink(responseValue.getFollowUsFacebookLink());
                            getDataManager().setFollowUsTwitterLink(responseValue.getFollowUsTwitterLink());
                            getDataManager().setImageServerUrl(responseValue.getImageServerUrl());

                            SharedPreferences test = Prefs.getPreferences();

                            //if auth is logged in, appsettings call, elsee publicapp settings
                            callGetPublicAppSettings(context, countryId);

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
                }));
    }

    private void doAppSettingsApiCall(Context context, String countryId) {
        getCompositeDisposable().add(getDataManager()
                .callGetPublicAppSettings(context, countryId)
                .subscribeOn(getSchedulerProvider().io())
                .subscribeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetAppSettings.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetAppSettings.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        com.mysale.genie.utility.config.model.getappsettings.Value value = responseValue.d.getValue();
                        if (value != null) {
                            getDataManager().setIsPaypalEnabled(value.getPayments().getPayPal().getEnabled());
                            getDataManager().setIsMasterpassEnabled(value.getPayments().getMasterPass().getEnabled());
                            getDataManager().setIsAmexEnabled(value.getPayments().getAmExpress().getEnabled());
                            getDataManager().setIsKountEnabled(value.getPayments().getKount().getEnabled());
                            getDataManager().setKountMerchantId(value.getPayments().getKount().getMerchantID());
                            getDataManager().setSearchMaxPrice(value.getSearch().getMaxPrice());
                            getDataManager().setAccessAnonymousEnabled(value.getAccess().getAnonymousEnabled());
                        }
                        RxBus.instance().post("FinishSplashActivity");
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
                }));
    }

    public void callGetPublicAppSettings(Context context, String countryId) {
        doAppSettingsApiCall(context, countryId);
    }

    private void callGetAppSettings(Context context, String countryId) {
        doAppSettingsApiCall(context, countryId);
    }

    @Override
    public void callGetAppSettingsSection(Context context, String countryId) {
        getCompositeDisposable().add(getDataManager()
                .callGetAppSettingsSection(context, countryId)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetAppSettingsSection.ResponseValue>() {
                               @Override
                               public void accept(@NonNull GetAppSettingsSection.ResponseValue responseValue) throws Exception {
                                   if (!isViewAttached()) {
                                       return;
                                   }

                                   GetAppSettingsSection.ResponseValue.Value value = responseValue.d.getValue();
                                   if (value != null) {

                                       String version = value.getMobileApp().getVersionRules();
                                       version = version.replace("/", "");

                                       try {
                                           JSONObject jsonVersion = new JSONObject(version);

                                           ArrayList<Android> androidArrayList = new ArrayList<>();

                                           for (int i = 0; i < jsonVersion.getJSONObject("VersionRules").getJSONArray("Android").length(); i++) {
                                               JSONObject jsonAndroid = jsonVersion.getJSONObject("VersionRules").getJSONArray("Android").getJSONObject(i);
                                               Android android = new Android();
                                               android.setAction(jsonAndroid.getString("action"));
                                               android.setEvent(jsonAndroid.getString("event"));
                                               android.setVersionNo(jsonAndroid.getString("versionNo"));

                                               Payload payload = new Payload();

                                               try {
                                                   JSONObject jsonPayload = jsonAndroid.getJSONObject("payload");
                                                   payload.setMessage(jsonPayload.getString("title"));
                                                   payload.setTitle(jsonPayload.getString("message"));

                                                   try {
                                                       payload.setUrl(jsonPayload.getString("url"));
                                                   } catch (Exception e) {
                                                       e.printStackTrace();
                                                       payload.setUrl(null);
                                                   }

                                               } catch (Exception e) {
                                                   e.printStackTrace();
                                                   payload = null;
                                               }

                                               android.setPayload(payload);

                                               androidArrayList.add(android);
                                           }

                                           if (!androidArrayList.isEmpty()) {
//                                               GVersion.checkVersion(context, androidArrayList);
                                           }
                                       } catch (JSONException e) {
                                           e.printStackTrace();
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
                           }

                ));
    }

    @Override
    public void fetchBTAuthorization(FetchTokenHandler fetchTokenHandler) {
        if (!getDataManager().isAuthorized()) return;

        getMvpView().showLoading();

        getCompositeDisposable().add(getDataManager()
                .callGetPaymentToken(new GetPaymentToken.RequestValue(getDataManager().getLanguageId(), getDataManager().getCountryId()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetPaymentToken.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetPaymentToken.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (responseValue.isResult() && responseValue.isAuthenticated()) {
                            getMvpView().onAuthorizationFetched(responseValue.getPaymentToken(), responseValue.getPaymentType());

                            if (fetchTokenHandler != null)
                                fetchTokenHandler.onSuccess();
                        } else {
                            if (fetchTokenHandler != null)
                                fetchTokenHandler.onFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (fetchTokenHandler != null)
                            fetchTokenHandler.onFailure();

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
    public void callCreatePaymentTransaction(BraintreeFragment braintreeFragment, String paymentType, String paymentNonce, String paymentToken) {
        getMvpView().showLoading();

        BraintreeResponseListener<String> handler = new BraintreeResponseListener<String>() {
            @Override
            public void onResponse(String deviceData) {
                String languageId = getDataManager().getLanguageId();
                String countryId = getDataManager().getCountryId();
                CreatePaymentTransaction.RequestValue.Request requestValue =
                        new CreatePaymentTransaction.RequestValue.Request(paymentType, paymentNonce, paymentToken, deviceData);
                getCompositeDisposable().add(getDataManager()
                                .callCreatePaymentTransaction(new CreatePaymentTransaction.RequestValue(requestValue, countryId, languageId))
                                .subscribeOn(getSchedulerProvider().io())
                                .observeOn(getSchedulerProvider().ui())
                                .subscribe(new Consumer<CreatePaymentTransaction.ResponseValue>() {
                                    @Override
                                    public void accept(@NonNull CreatePaymentTransaction.ResponseValue responseValue) throws Exception {
                                        if (!isViewAttached()) {
                                            return;
                                        }

                                        getMvpView().hideLoading();

                                        if (responseValue.getD().getResult()) {
                                            getMvpView().createPaymentTransactionSuccess(responseValue);
                                        } else {
                                            getMvpView().onError(responseValue.getD().getMessage());
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
        };

        if (getDataManager().isKountEnabled()) {
            DataCollector.collectDeviceData(braintreeFragment, getDataManager().getKountMerchantId(), handler);
        } else {
            DataCollector.collectDeviceData(braintreeFragment, handler);
        }
    }

    @Override
    public void createPaymentMethod(BraintreeFragment braintreeFragment, String paymentNonce, String paymentType) {

        BraintreeResponseListener<String> handler = new BraintreeResponseListener<String>() {
            @Override
            public void onResponse(String deviceData) {
                String languageId = getDataManager().getLanguageId();
                String countryId = getDataManager().getCountryId();
                SchedulerProvider test = getSchedulerProvider();
                CreatePaymentMethod.RequestValue.Request requestValue = new CreatePaymentMethod.RequestValue.Request(paymentType, paymentNonce, deviceData);
                getCompositeDisposable().add(getDataManager()
                                .callCreatePaymentMethod(new CreatePaymentMethod.RequestValue(requestValue, countryId, languageId))
                                .subscribeOn(getSchedulerProvider().io())
                                .observeOn(getSchedulerProvider().ui())
                                .subscribe(new Consumer<CreatePaymentMethod.ResponseValue>() {
                                    @Override
                                    public void accept(@NonNull CreatePaymentMethod.ResponseValue responseValue) throws Exception {
                                        if (!isViewAttached()) {
                                            return;
                                        }

                                        getMvpView().performResetWithAuthFetch();

                                        if ((responseValue.getResult() && responseValue.getIsAuthenticated())) {
                                            getMvpView().createPaymentMethodSuccess(responseValue.getD().getValue().getLastPaymentMethod());
                                        } else {
                                            getMvpView().onError(responseValue.getMessage());
                                        }
//

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
        };

        if (getDataManager().isKountEnabled()) {
            DataCollector.collectDeviceData(braintreeFragment, getDataManager().getKountMerchantId(), handler);
        } else {
            DataCollector.collectDeviceData(braintreeFragment, handler);
        }
    }

    @Override
    public void callLoginTicket() {
        String loginTicket = getDataManager().getLoginTicket();
        if (!loginTicket.isEmpty()) {
            getCompositeDisposable().add(getDataManager()
                            .callLoginTicket(new LoginTicket.RequestValue(loginTicket, getDataManager().getCountryId()))
                            .subscribeOn(getSchedulerProvider().io())
                            .observeOn(getSchedulerProvider().ui())
                            .subscribe(new Consumer<LoginEmail.ResponseValue>() {
                                @Override
                                public void accept(@NonNull LoginEmail.ResponseValue responseValue) throws Exception {
                                    if (!isViewAttached()) {
                                        return;
                                    }

                                    if (responseValue.isSuccess()) {
                                        getDataManager().acknowledgeAuth(responseValue.getTicket());
                                    } else {
                                        //On login ticket fail, call logout and go back to shop
//                                RxBus.instance().post("shop_now");
                                        callLogout();
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

    @Override
    public void callLogout() {

//        GCartUtil.setValueToCart(0);
//        RxBus.instance().post("update_cart_items_immediate");
//        RxBus.instance().post(Auth.EVENT_PRE_LOGOUT);
        getCompositeDisposable().add(getDataManager()
                        .callLogout(new Logout.RequestValue())
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(new Consumer<Logout.ResponseValue>() {
                            @Override
                            public void accept(@NonNull Logout.ResponseValue responseValue) throws Exception {
                                if (!isViewAttached()) {
                                    return;
                                }

                                getDataManager().revokeAuth();
//                RxBus.instance().post(Auth.EVENT_LOGOUT);
//                RxBus.instance().post(GVersion.EVENT_LOGOUT);
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
