package au.com.dealsdirect.ui.controller.vouchers.View;

import android.support.v4.util.Pair;

import com.androidnetworking.error.ANError;

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
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Paul on 6/23/17.
 */

public class ViewVouchersPresenter<V extends ViewVouchersMvpView> extends BasePresenter<V> implements ViewVouchersMvpPresenter<V> {


    @Inject
    public ViewVouchersPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                                 CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadMyVouchers() {
        if (getDataManager().isAuthorized()) {
            GetUserVouchersRequest getUserVouchersRequest =
                    new GetUserVouchersRequest(getDataManager().getLanguageId());

            Observable.zip(wrapObservable(getDataManager().callGetUserVouchers(getUserVouchersRequest)),
                    wrapObservable(getDataManager().callGetVouchers(getUserVouchersRequest)),
                    new BiFunction<GetUserVoucherResponse, GetVouchersResponse, Pair<List<GetUserVoucherResponse.Voucher>, GetVouchersResponse>>() {
                        @Override
                        public Pair<List<GetUserVoucherResponse.Voucher>, GetVouchersResponse> apply(@NonNull GetUserVoucherResponse getUserVoucherResponse, @NonNull GetVouchersResponse getVouchersResponse) throws Exception {
                            return new Pair<>(getUserVoucherResponse.d.getList(), getVouchersResponse);
                        }
                    })
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(action -> {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideNoNetworkLayout();
                        getMvpView().hideLoading();

                        getMvpView().updateVoucherList(action);
                    }, throwable -> {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (throwable.getCause() instanceof SocketTimeoutException || throwable.getCause() instanceof UnknownHostException) {
                            getMvpView().showNoNetworkLayout();
                        }

                        getMvpView().onError(throwable.getMessage());

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
