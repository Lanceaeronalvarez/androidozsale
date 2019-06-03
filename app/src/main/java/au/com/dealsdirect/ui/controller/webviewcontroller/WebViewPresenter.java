package au.com.dealsdirect.ui.controller.webviewcontroller;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class WebViewPresenter<V extends WebViewMvpView> extends BasePresenter<V> implements WebViewMvpPresenter<V> {

    @Inject
    public WebViewPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadFromUrl(String url) {
        getMvpView().showWebpage(url);
    }
}