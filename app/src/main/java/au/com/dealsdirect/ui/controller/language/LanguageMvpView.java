package au.com.dealsdirect.ui.controller.language;

import com.mysale.genie.utility.config.model.getserversettings.Language;

import java.util.List;

import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by Paul on 6/22/17.
 */

public interface LanguageMvpView extends MvpView {
    void showLanguages(List<Language> languages);

    void onBackPress();
}
