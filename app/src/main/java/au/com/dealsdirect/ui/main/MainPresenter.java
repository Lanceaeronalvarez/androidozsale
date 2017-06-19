package au.com.dealsdirect.ui.main;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.content.Context;
import android.content.SharedPreferences;

import com.androidnetworking.error.ANError;
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
                .callGetServerSettings(context,countryId)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetServerSettings.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetServerSettings.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        com.mysale.genie.utility.config.model.getserversettings.Value value = responseValue.d.getValue();
                        if(value != null) {
                            getDataManager().setCountryId(responseValue.getCountryId());
                            getDataManager().setLanguageId(responseValue.getLanguages().get(0).getID());
                            getDataManager().setLanguages(responseValue.getLanguages());
                            getDataManager().setSiteName(responseValue.getSiteFullname());
                            getDataManager().setCurrency(responseValue.getCurrency());
                            getDataManager().setCurrencySign(responseValue.getCurrencySign());
                            getDataManager().setFollowUsFbLink(responseValue.getFollowUsFacebookLink());
                            getDataManager().setFollowUsTwitterLink(responseValue.getFollowUsTwitterLink());
                            getDataManager().setImageServerUrl(responseValue.getImageServerUrl());

                            SharedPreferences test = Prefs.getPreferences();

                            //if auth is logged in, appsettings call, elsee publicapp settings
                            callGetPublicAppSettings(context,countryId);

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

    private void doAppSettingsApiCall(Context context, String countryId){
        getCompositeDisposable().add(getDataManager()
                .callGetPublicAppSettings(context,countryId)
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
        doAppSettingsApiCall(context,countryId);
    }

    private void callGetAppSettings(Context context, String countryId) {
        doAppSettingsApiCall(context,countryId);
    }

    @Override
    public void callGetAppSettingsSection(Context context, String countryId) {
        getCompositeDisposable().add(getDataManager()
                .callGetAppSettingsSection(context,countryId)
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
}
