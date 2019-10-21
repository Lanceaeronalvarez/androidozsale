package au.com.dealsdirect.ui.controller.vouchers.View;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Paul on 6/27/17.
 */

public class ViewVouchersViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.row_view_vouchers_layout)
    View mVouchersLayout;

    @BindView(R.id.row_view_voucher_cost_text)
    TextView mVouchersItemCostText;

    @BindView(R.id.row_view_voucher_desc_text)
    TextView mVouchersItemDescText;

    @BindView(R.id.row_view_voucher_expiry_text)
    TextView mVouchersItemExpiresOnText;

    @BindView(R.id.row_view_voucher_value_label)
    TextView mVouchersItemValue;

    @BindView(R.id.row_view_voucher_name_text)
    TextView mVoucherName;

    @BindView(R.id.row_view_voucher_status_text)
    TextView mStatusText;

    public ViewVouchersViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
