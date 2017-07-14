package au.com.dealsdirect.ui.controller.legalities;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by Paul on 7/14/17.
 */

public interface LegalitiesMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void loadText(String key);

}
