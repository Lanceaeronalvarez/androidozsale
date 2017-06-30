package au.com.dealsdirect.ui.controller.vouchers.Add;

import android.widget.LinearLayout;

/**
 * Created by Paul on 6/30/17.
 */

public interface AddVouchersItemClickListener {
    void onVoucherItemClicked(String voucherId, String voucherState, LinearLayout holder, int position);
}
