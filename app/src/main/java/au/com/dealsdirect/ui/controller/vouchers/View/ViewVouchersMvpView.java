package au.com.dealsdirect.ui.controller.vouchers.View;

import androidx.core.util.Pair;

import java.util.List;

import au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse;
import au.com.dealsdirect.data.network.model.vouchers.GetVouchersResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by Paul on 6/23/17.
 */

public interface ViewVouchersMvpView extends MvpView {

    void updateVoucherList(Pair<List<GetUserVoucherResponse.Voucher>,GetVouchersResponse> pair);

}
