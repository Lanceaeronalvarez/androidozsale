package au.com.dealsdirect.ui.controller.vouchers.Add;

import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyRequest;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.AddVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by Paul on 6/27/17.
 */

public interface AddVouchersMvpView extends MvpView {

    void showAddedVoucherItem(AddVoucherByKeyResponse.Response addVoucherResponse);

    void onVouchersApplied(ApplyVouchersResponse applyVouchersResponseBody);

    void onVouchersCleared(ClearVouchersResponse clearVouchersResponse);

    void onApplyVouchersError();

    void onAddAndAppliedVoucher(AddAndApplyVoucherByKeyResponse response);

    boolean isActive();
}
