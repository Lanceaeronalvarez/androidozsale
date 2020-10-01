package au.com.dealsdirect.ui.controller.vouchers.Add;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyRequest;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Paul on 6/27/17.
 */

public class AddVouchersPresenter<V extends AddVouchersMvpView> extends BasePresenter<V> implements AddVouchersMvpPresenter<V> {

    @Inject
    public AddVouchersPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void applyVouchers(String postcode, int imageSize, List<String> voucherIds) {
        getMvpView().showLoading();
        ApplyVouchersRequest request = new ApplyVouchersRequest(voucherIds, postcode, imageSize, getDataManager().getLanguageId());
        doApiCallForResponse(getDataManager().callGetApplyVouchers(request), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().onVouchersApplied((ApplyVouchersResponse) response);
            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                getMvpView().onApplyVouchersError();
            }
        });
    }

    @Override
    public void clearVouchers(String postcode, int imageSize) {
        getMvpView().showLoading();
        ClearVouchersRequest request = new ClearVouchersRequest(postcode, imageSize, getDataManager().getLanguageId());
        doApiCallForResponse(getDataManager().callGetClearVouchers(request), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().onVouchersCleared((ClearVouchersResponse) response);
            }
        });
    }

    @Override
    public void addAndApplyVoucherByKey(String postcode, int imageSize, String key) {
        getMvpView().showLoading();
        AddAndApplyVoucherByKeyRequest request = new AddAndApplyVoucherByKeyRequest(key, postcode, imageSize, getDataManager().getLanguageId());
        doApiCallForResponse(getDataManager().callGetAddAndApplyVoucherByKey(request), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().onAddAndAppliedVoucher((AddAndApplyVoucherByKeyResponse) response);
            }
        });
    }

}
