package au.com.dealsdirect.ui.controller.gdpr;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.pref.AppPreferencesHelper;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class StrictConsentPresenter<V extends StrictConsentMvpView> extends BasePresenter<V> implements StrictConsentMvpPresenter<V> {

    @Inject
    public StrictConsentPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public String getConsentContinueText() {
        return getDataManager().getStoredTemplateTexts(AppPreferencesHelper.CONSENT_CONTINUE_TEXT);
    }

    @Override
    public String getConsentFullText() {
        return getDataManager().getStoredTemplateTexts(AppPreferencesHelper.CONSENT_FULL_TEMPLATE_TEXT);
    }
}
