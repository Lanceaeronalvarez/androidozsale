package au.com.dealsdirect.ui.controller.vouchers.View;

import android.support.v4.util.Pair;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.GetVouchersResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
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
        final PublishSubject<List<GetUserVoucherResponse.Voucher>> selectSubject
                = PublishSubject.create();
        final PublishSubject<GetVouchersResponse> selectSubject2
                = PublishSubject.create();

        Observable.zip(selectSubject, selectSubject2, Pair::new)
                .subscribe(action->{
                    getMvpView().updateVoucherList(action);
                });

        GetUserVouchersRequest getUserVouchersRequest =
                new GetUserVouchersRequest(getDataManager().getLanguageId());
        getCompositeDisposable().add(getDataManager()
                .getUserVouchersApiCall(getUserVouchersRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetUserVoucherResponse>() {
                    @Override
                    public void accept(@NonNull GetUserVoucherResponse getUserVoucherResponse) throws Exception {
                        selectSubject.onNext(getUserVoucherResponse.getValue().getList());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                }));

        getCompositeDisposable().add(getDataManager()
                .getVouchersApiCall()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetVouchersResponse>() {
                    @Override
                    public void accept(@NonNull GetVouchersResponse getVouchersResponse) throws Exception {
                        selectSubject2.onNext(getVouchersResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                }));
    }
}
