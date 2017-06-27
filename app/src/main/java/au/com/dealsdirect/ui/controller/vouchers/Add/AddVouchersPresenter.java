package au.com.dealsdirect.ui.controller.vouchers.Add;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyRequest;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Paul on 6/27/17.
 */

public class AddVouchersPresenter<V extends AddVouchersMvpView>  extends BasePresenter<V> implements AddVouchersMvpPresenter<V> {

    @Inject
    public AddVouchersPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void applyVouchers(ApplyVouchersRequest applyVouchersRequest) {
        getCompositeDisposable().add(getDataManager().getApplyVouchersApiCall(applyVouchersRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<ApplyVouchersResponse>() {
                    @Override
                    public void accept(@NonNull ApplyVouchersResponse applyVouchersResponse) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                }));
    }

    @Override
    public void clearVouchers(ClearVouchersRequest request) {
        getCompositeDisposable().add(getDataManager().getClearVouchersApiCall(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<ClearVouchersResponse>() {
                    @Override
                    public void accept(@NonNull ClearVouchersResponse clearVouchersResponse) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                }));
    }

    @Override
    public void addAndApplyVoucherByKey(AddAndApplyVoucherByKeyRequest request) {
        getCompositeDisposable().add(getDataManager().getAddAndApplyVoucherByKeyApiCall(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<AddAndApplyVoucherByKeyResponse>() {
                    @Override
                    public void accept(@NonNull AddAndApplyVoucherByKeyResponse addAndApplyVoucherByKeyResponse) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                }));
    }
}
