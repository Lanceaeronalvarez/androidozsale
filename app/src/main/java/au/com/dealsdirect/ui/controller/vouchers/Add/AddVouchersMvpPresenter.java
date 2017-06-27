package au.com.dealsdirect.ui.controller.vouchers.Add;

import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyRequest;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

/**
 * Created by Paul on 6/27/17.
 */

public interface AddVouchersMvpPresenter<V extends AddVouchersMvpView> extends MvpPresenter<V> {

    void applyVouchers(ApplyVouchersRequest applyVouchersRequest);

    void clearVouchers(ClearVouchersRequest clearVouchersRequest);

    void addAndApplyVoucherByKey(AddAndApplyVoucherByKeyRequest request);
}
