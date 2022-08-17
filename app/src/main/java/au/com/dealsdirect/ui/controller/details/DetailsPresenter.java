package au.com.dealsdirect.ui.controller.details;

import com.androidnetworking.error.ANError;

import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.gdpr.savereceivesales.SaveReceiveSalesRequest;
import au.com.dealsdirect.data.network.model.gdpr.savereceivesales.SaveReceiveSalesResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetEmailSubscriptionTemplatesResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.data.network.model.userdetails.UpdateUserEmailSubscriptionRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Paul on 6/20/17.
 */

public class DetailsPresenter<V extends DetailsMvpView> extends BasePresenter<V> implements
        DetailsMvpPresenter<V> {

    @Inject
    public DetailsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadUser(SetUserDetailsRequest setUserDetailsRequest) {
        if (getDataManager().isAuthorized()) {
            doApiCallForResponse(getDataManager().getLoadUserDetailsApiCall(setUserDetailsRequest), new AppApiCallback() {
                @Override
                public void onSuccess(Object response) {
                    super.onSuccess(response);
                    getMvpView().loadDetails((GetUserDetailsResponse) response);
                }
            });
        } else {
            getMvpView().hideLoading();
        }
    }

    @Override
    public void sendUserDetails(SetUserDetailsRequest userDetailsRequest) {

        userDetailsRequest.setLanguageID(getDataManager().getLanguageId());
        doApiCallForResponse(getDataManager().getSaveUserDetailsApiCall(userDetailsRequest), new AppApiCallback() {
            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                if (!getMvpView().isViewAttached()) {
                    return;
                }
                if (t instanceof ANError) {
                    try {
                        JSONObject jsonObject = new JSONObject(((ANError) t).getErrorBody());
                        getMvpView().onError(jsonObject.getString("detail"));
                    } catch (Exception ignored) {
                    }
                }
            }

            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                getMvpView().saveUserDetailsSuccess();

            }
        });
    }

    @Override
    public void accountDeletion(String userDetailsId) {
        doApiCallForResponse(getDataManager().callAccountDeletion(userDetailsId), new AppApiCallback() {
            @Override
            public void onSuccess() {
                super.onSuccess();
            }

            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
            }

            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
            }
        });
    }

    @Override
    public void saveReceiveSales(boolean receiveInvitations) {
        SaveReceiveSalesRequest request = new SaveReceiveSalesRequest(getDataManager().getCountryId(), getDataManager().getLanguageId(), receiveInvitations);
        getMvpView().showLoading();
        doApiCallForResponse(getDataManager().callSaveReceiveSales(request), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        getMvpView().onSaveReceiveSales((SaveReceiveSalesResponse) response);
                    }
                }
        );
    }

    @Override
    public void updateEmailSubscriptionPreference(UpdateUserEmailSubscriptionRequest request) {
        doApiCallForResponse(getDataManager().callUpdateUserEmailSubscription(request), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        getMvpView().onUpdateEmailSubscriptionPreference();
                    }

                    @Override
                    public void onSuccess() {
                        super.onSuccess();
                        getMvpView().onUpdateEmailSubscriptionPreference();
                    }

                    @Override
                    public void onSuccess(List<?> response) {
                        super.onSuccess(response);
                        getMvpView().onUpdateEmailSubscriptionPreference();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                    }
                }
        );
    }

    @Override
    public void getEmailSubscriptionTemplates() {
        doApiCallForResponse(getDataManager().getEmailSubscriptionTemplates(), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (response instanceof GetEmailSubscriptionTemplatesResponse) {
                            getMvpView().onGetEmailSubscriptionTemplates((GetEmailSubscriptionTemplatesResponse) response);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        getMvpView().onGetEmailSubscriptionTemplates(null);
                    }
                }
        );
    }

    @Override
    public String getGdprTemplateTexts(String key) {
        return getDataManager().getConsentTemplateTexts(key);
    }

    @Override
    public boolean getGdprIsChecked(String key) {
        return getDataManager().getAppSettingsConsentIsChecked(key);
    }


}
