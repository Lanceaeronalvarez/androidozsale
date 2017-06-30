package au.com.dealsdirect.ui.controller.vouchers.Add;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Paul on 6/30/17.
 */

public class AddVouchersViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.row_layout_add_vouchers)
    public LinearLayout mVoucherItemLayout;

    @BindView(R.id.row_add_vouchers_text_item_cost_value)
    public TextView mVoucherItemCostText;
    @BindView(R.id.row_add_vouchers_text_item_expires_on_value)
    public TextView mVoucherItemExpiresOnText;
    @BindView(R.id.row_add_vouchers_text_item_desc)
    public TextView mVoucherItemDescText;

    public AddVouchersViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
