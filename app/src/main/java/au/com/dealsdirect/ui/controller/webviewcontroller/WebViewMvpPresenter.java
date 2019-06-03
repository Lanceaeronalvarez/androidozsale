package au.com.dealsdirect.ui.controller.webviewcontroller;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

public interface WebViewMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
    void loadFromUrl(String url);
}
