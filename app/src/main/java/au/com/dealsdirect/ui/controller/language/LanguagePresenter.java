package au.com.dealsdirect.ui.controller.language;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mysale.genie.utility.config.model.getserversettings.Language;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
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
        String languagesJsonString = getDataManager().getLanguages();
        List<Language> languages = new Gson().fromJson(languagesJsonString, new TypeToken<ArrayList<Language>>(){}.getType());
        getMvpView().showLanguages(languages, getDataManager().getLanguageId());
        getMvpView().hideLoading();
    }

    @Override
    public void onLanguageItemClick(Language language) {
        getMvpView().showLanguageLanguageDialog(language.getName());
        getDataManager().setLanguageId(language.getID());
        getMvpView().onBackPress();
    }
}
