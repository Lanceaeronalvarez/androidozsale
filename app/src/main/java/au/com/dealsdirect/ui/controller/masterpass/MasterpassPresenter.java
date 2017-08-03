package au.com.dealsdirect.ui.controller.masterpass;
/*
 * Created by CodeineBot on 8/3/17.
 */

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class MasterpassPresenter<V extends MasterpassMvpView> extends BasePresenter<V> implements MasterpassMvpPresenter<V> {

    @Inject
    public MasterpassPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void getMasterpassPayment() {
        HashMap<String, Object> param = new HashMap<>();
        param.put("isMobile", true);

        doApiCallForResponse(getDataManager().callMasterpassPayment(param), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
            }

            @Override
            public void onFailure() {
                super.onFailure();
            }
        });
    }

    @Override
    public void confirmPayment() {

    }
}
