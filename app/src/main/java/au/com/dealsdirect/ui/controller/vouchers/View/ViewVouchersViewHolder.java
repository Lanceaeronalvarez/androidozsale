package au.com.dealsdirect.ui.controller.vouchers.View;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Paul on 6/27/17.
 */

public class ViewVouchersViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.row_layout_vouchers)
    View mVouchersLayout;

    @BindView(R.id.row_text_vouchers_item_cost_value)
    TextView mVouchersItemCostText;

    @BindView(R.id.row_text_vouchers_item_desc)
    TextView mVouchersItemDescText;

    @BindView(R.id.row_text_vouchers_item_expires_on_value)
    TextView mVouchersItemExpiresOnText;

    @BindView(R.id.row_text_label_value)
    TextView mVouchersItemValue;

    @BindView(R.id.row_text_vouchers_item_already_spent)
    TextView mVouchersAlreadySpent;

    public ViewVouchersViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
