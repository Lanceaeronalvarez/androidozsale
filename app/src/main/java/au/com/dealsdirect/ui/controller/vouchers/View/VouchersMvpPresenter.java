package au.com.dealsdirect.ui.controller.vouchers.View;

import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyRequest;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.controller.vouchers.View.VouchersMvpView;

/**
 * Created by Paul on 6/23/17.
 */

public interface VouchersMvpPresenter<V extends VouchersMvpView> extends MvpPresenter<V> {

    void loadMyVouchers();
}
