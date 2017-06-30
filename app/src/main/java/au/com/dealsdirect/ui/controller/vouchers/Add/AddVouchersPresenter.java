package au.com.dealsdirect.ui.controller.vouchers.Add;

import android.support.v4.util.Pair;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyRequest;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersResponse;
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
 * Created by Paul on 6/27/17.
 */

public class AddVouchersPresenter<V extends AddVouchersMvpView>  extends BasePresenter<V> implements AddVouchersMvpPresenter<V> {

    @Inject
    public AddVouchersPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void applyVouchers(int imageSize, List<String> voucherIds) {
        ApplyVouchersRequest request = new ApplyVouchersRequest(voucherIds, imageSize, getDataManager().getLanguageId());
        getCompositeDisposable().add(getDataManager().getApplyVouchersApiCall(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<ApplyVouchersResponse>() {
                    @Override
                    public void accept(@NonNull ApplyVouchersResponse applyVouchersResponse) throws Exception {
                        getMvpView().onVouchersApplied(applyVouchersResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        getMvpView().onApplyVouchersError();
                    }
                }));
    }

    @Override
    public void clearVouchers(int imageSize) {
        ClearVouchersRequest request = new ClearVouchersRequest(imageSize, getDataManager().getLanguageId());
        getCompositeDisposable().add(getDataManager().getClearVouchersApiCall(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<ClearVouchersResponse>() {
                    @Override
                    public void accept(@NonNull ClearVouchersResponse clearVouchersResponse) throws Exception {
                        getMvpView().onVouchersCleared(clearVouchersResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                }));
    }

    @Override
    public void addAndApplyVoucherByKey(int imageSize, String key) {
        AddAndApplyVoucherByKeyRequest request = new AddAndApplyVoucherByKeyRequest(key, imageSize, getDataManager().getLanguageId());
        getCompositeDisposable().add(getDataManager().getAddAndApplyVoucherByKeyApiCall(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<AddAndApplyVoucherByKeyResponse>() {
                    @Override
                    public void accept(@NonNull AddAndApplyVoucherByKeyResponse addAndApplyVoucherByKeyResponse) throws Exception {
                        getMvpView().onAddAndAppliedVoucher(addAndApplyVoucherByKeyResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                }));
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
