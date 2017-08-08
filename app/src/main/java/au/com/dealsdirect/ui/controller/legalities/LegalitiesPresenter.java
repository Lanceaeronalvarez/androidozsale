
package au.com.dealsdirect.ui.controller.legalities;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextRequest;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Paul on 7/14/17.
 */

public class LegalitiesPresenter<V extends LegalitiesMvpView> extends BasePresenter<V> implements LegalitiesMvpPresenter<V> {

    @Inject
    public LegalitiesPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                               CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadText(String key) {
        GetTemplateTextRequest getTemplateTextRequest = new GetTemplateTextRequest();
        getTemplateTextRequest.templateKey = key;
        getTemplateTextRequest.countryId = getDataManager().getCountryId();
        getTemplateTextRequest.languageId = getDataManager().getLanguageId();
        doApiCallForResponse(getDataManager().callGetTemplateText(getTemplateTextRequest), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().displayFetchedText(((GetTemplateTextResponse) response).getResponse().getValue());
            }
        });
    }
}
