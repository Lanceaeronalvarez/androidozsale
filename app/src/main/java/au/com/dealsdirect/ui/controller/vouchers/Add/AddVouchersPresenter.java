package au.com.dealsdirect.ui.controller.vouchers.Add;

import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyRequest;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
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
        getCompositeDisposable().add(getDataManager().callGetApplyVouchers(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(applyVouchersResponse -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().onVouchersApplied(applyVouchersResponse);
                }, throwable -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().onApplyVouchersError();
                }));
    }

    @Override
    public void clearVouchers(int imageSize) {
        ClearVouchersRequest request = new ClearVouchersRequest(imageSize, getDataManager().getLanguageId());
        getCompositeDisposable().add(getDataManager().callGetClearVouchers(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(clearVouchersResponse -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().onVouchersCleared(clearVouchersResponse);
                }, throwable -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();
                    getMvpView().onError(throwable.getMessage());

                    // handle load accounts error here
                    if (throwable instanceof ANError) {
                        ANError anError = (ANError) throwable;
                        handleApiError(anError);
                    }
                }));
    }

    @Override
    public void addAndApplyVoucherByKey(int imageSize, String key) {
        AddAndApplyVoucherByKeyRequest request = new AddAndApplyVoucherByKeyRequest(key, imageSize, getDataManager().getLanguageId());
        getCompositeDisposable().add(getDataManager().callGetAddAndApplyVoucherByKey(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(addAndApplyVoucherByKeyResponse -> getMvpView().onAddAndAppliedVoucher(addAndApplyVoucherByKeyResponse), throwable -> {

                }));
    }
}
