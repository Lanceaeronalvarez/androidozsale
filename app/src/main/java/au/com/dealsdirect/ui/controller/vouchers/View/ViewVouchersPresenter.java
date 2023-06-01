package au.com.dealsdirect.ui.controller.vouchers.View;

import android.annotation.SuppressLint;

import androidx.core.util.Pair;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.GetVouchersResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;

/**
 * Created by Paul on 6/23/17.
 */

public class ViewVouchersPresenter<V extends ViewVouchersMvpView> extends BasePresenter<V> implements ViewVouchersMvpPresenter<V> {


    @Inject
    public ViewVouchersPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                                 CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadMyVouchers() {
        if (getDataManager().isAuthorized()) {
            GetUserVouchersRequest getUserVouchersRequest =
                    new GetUserVouchersRequest(getDataManager().getLanguageId());

            doApiCallForResponse(getDataManager().callGetUserVouchers(getUserVouchersRequest), new AppApiCallback() {
                @Override
                public void onSuccess(List<?> response) {
                    super.onSuccess(response);

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideNoNetworkLayout();
                    getMvpView().hideLoading();

                    getMvpView().updateVoucherList((List<GetUserVoucherResponse.Response>) response);
                }
            });
        }

    }

    @Override
    public String getVoucherStatusString(GetUserVoucherResponse.Status status) {
        return getDataManager().getVoucherStatusTemplateText(status);
    }

    private <T> Observable<T> wrapObservable(Observable<T> observable) {
        return observable.subscribeOn(getSchedulerProvider().io());
    }
}
