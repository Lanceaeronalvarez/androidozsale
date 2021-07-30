package au.com.dealsdirect.ui.controller.information;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class InformationMenuPresenter<V extends InformationMenuMvpView> extends BasePresenter<V> implements InformationMenuMvpPresenter<V> {

    @Inject
    public InformationMenuPresenter(DataManager dataManager,
                                    SchedulerProvider schedulerProvider,
                                    CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
