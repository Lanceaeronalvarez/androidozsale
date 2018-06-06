package au.com.dealsdirect.ui.controller.login;

import com.visa.checkout.VisaPaymentSummary;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.login.LoginVisa;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpPresenter;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpView;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 12/02/2018.
 */

public class PasswordVerificationPresenter<V extends PasswordVerificationMvpView> extends BasePresenter<V> implements PasswordVerificationMvpPresenter<V> {

    @Inject
    public PasswordVerificationPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
