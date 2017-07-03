package au.com.dealsdirect.ui.controller.vouchers.Add;

import android.support.v4.util.Pair;

import com.androidnetworking.error.ANError;

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
        getCompositeDisposable().add(getDataManager().callGetApplyVouchers(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<ApplyVouchersResponse>() {
                    @Override
                    public void accept(@NonNull ApplyVouchersResponse applyVouchersResponse) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().onVouchersApplied(applyVouchersResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().onApplyVouchersError();
                    }
                }));
    }

    @Override
    public void clearVouchers(int imageSize) {
        ClearVouchersRequest request = new ClearVouchersRequest(imageSize, getDataManager().getLanguageId());
        getCompositeDisposable().add(getDataManager().callGetClearVouchers(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<ClearVouchersResponse>() {
                    @Override
                    public void accept(@NonNull ClearVouchersResponse clearVouchersResponse) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().onVouchersCleared(clearVouchersResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
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
                    }
                }));
    }

    @Override
    public void addAndApplyVoucherByKey(int imageSize, String key) {
        AddAndApplyVoucherByKeyRequest request = new AddAndApplyVoucherByKeyRequest(key, imageSize, getDataManager().getLanguageId());
        getCompositeDisposable().add(getDataManager().callGetAddAndApplyVoucherByKey(request)
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
                    }
                }));
    }


}
