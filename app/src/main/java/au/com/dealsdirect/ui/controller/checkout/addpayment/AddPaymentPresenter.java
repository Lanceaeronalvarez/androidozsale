package au.com.dealsdirect.ui.controller.checkout.addpayment;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.AppEventHelper;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 29/06/2017.
 */

public class AddPaymentPresenter<V extends AddPaymentMvpView> extends BasePresenter<V> implements AddPaymentMvpPresenter<V> {
    @Inject
    public AddPaymentPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public boolean isDebug() {
        return getDataManager().isDebugMode();
    }
}
