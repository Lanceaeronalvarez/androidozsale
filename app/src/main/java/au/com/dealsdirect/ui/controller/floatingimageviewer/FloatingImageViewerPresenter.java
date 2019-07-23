package au.com.dealsdirect.ui.controller.floatingimageviewer;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class FloatingImageViewerPresenter<V extends FloatingImageViewerMvpView> extends BasePresenter<V> implements
        FloatingImageViewerMvpPresenter<V> {

    @Inject
    public FloatingImageViewerPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
