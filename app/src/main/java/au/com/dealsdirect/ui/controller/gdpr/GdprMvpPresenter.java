package au.com.dealsdirect.ui.controller.gdpr;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 12/07/2018.
 */

//GDPR related base presenter
//To create GdprMvpView in the future
//Currently template texts calls only
public interface GdprMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    String getGdprTemplateTexts(String key);

    boolean getGdprIsChecked(String key);

}
