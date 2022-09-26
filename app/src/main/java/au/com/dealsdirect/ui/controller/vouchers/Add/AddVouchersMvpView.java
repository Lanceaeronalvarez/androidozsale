package au.com.dealsdirect.ui.controller.vouchers.Add;

import androidx.core.util.Pair;
import android.widget.LinearLayout;

import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.RemoveVoucherByKeyResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by Paul on 6/27/17.
 */

public interface AddVouchersMvpView extends MvpView {

    void onVouchersApplied(ApplyVouchersResponse applyVouchersResponseBody);

    void onVouchersCleared(ClearVouchersResponse clearVouchersResponse);

    void onApplyVouchersError();

    void onAddAndAppliedVoucher(AddAndApplyVoucherByKeyResponse response);

    void onRemoveVoucherByKey(RemoveVoucherByKeyResponse response);
}
