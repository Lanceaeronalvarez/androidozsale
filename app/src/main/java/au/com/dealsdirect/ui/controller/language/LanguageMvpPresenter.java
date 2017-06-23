package au.com.dealsdirect.ui.controller.language;

import com.mysale.genie.utility.config.model.getserversettings.Language;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by Paul on 6/22/17.
 */

public interface LanguageMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
    void getUserLanguages();

    void onLanguageItemClick(Language language);
}
