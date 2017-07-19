package au.com.dealsdirect.ui.controller.main;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface MainMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void loadCategoryTree();
}
