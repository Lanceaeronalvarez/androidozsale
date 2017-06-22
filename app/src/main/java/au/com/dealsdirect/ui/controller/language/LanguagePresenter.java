package au.com.dealsdirect.ui.controller.language;

import android.util.Log;

import com.androidnetworking.error.ANError;
import com.mysale.genie.utility.config.model.getserversettings.Language;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.language.GetUserLanguageRequest;
import au.com.dealsdirect.data.network.model.language.GetUserLanguageResponse;
import au.com.dealsdirect.data.network.model.language.SetUserLanguageRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Paul on 6/22/17.
 */

public class LanguagePresenter<V extends LanguageMvpView> extends BasePresenter<V> implements LanguageMvpPresenter<V> {

    @Inject
    public LanguagePresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                             CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void getUserLanguages() {
        GetUserLanguageRequest getUserLanguageRequest = new GetUserLanguageRequest(getDataManager().getCountryId());
        getCompositeDisposable().add(getDataManager()
                .getUserLanguagesApiCall(getUserLanguageRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetUserLanguageResponse>() {
                    @Override
                    public void accept(@NonNull GetUserLanguageResponse response) throws Exception {
                        getMvpView().showLanguages(response.d.languages);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

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
    public void onLanguageItemClick(String language) {
        SetUserLanguageRequest setUserLanguageRequest =
                new SetUserLanguageRequest(getDataManager().getCountryId(), language);
        getCompositeDisposable().add(getDataManager().doSetUserLanguageApiCall(
                setUserLanguageRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<SetUserLanguageRequest>() {
                    @Override
                    public void accept(@NonNull SetUserLanguageRequest setUserLanguageRequest) throws Exception {
                        getMvpView().onBackPress();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                }));
    }
}
