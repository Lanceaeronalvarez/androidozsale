package au.com.dealsdirect.ui.controller.returns.returndetails.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/30/17.
 */

public class ReturnDetailsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.viewholder_return_detail_row_container)
    public LinearLayout myReturnDetailItem;
    @BindView(R.id.viewholder_returns_details_size_container)
    public ViewGroup myReturnDetailSizeContainer;
    @BindView(R.id.viewholder_return_detail_image_view)
    public ImageView myReturnsDetailsProductImageView;
    @BindView(R.id.viewholder_return_details_item_name)
    public TextView myReturnsDetailsProductNameValueTextView;
    @BindView(R.id.viewholder_returns_details_item_count_value)
    public TextView myReturnsDetailsProductItemCountValueTextView;
    @BindView(R.id.viewholder_returns_details_size_value)
    public TextView myReturnsDetailsProductItemSizeValueTextView;
    @BindView(R.id.viewholder_returns_order_item_total_cost_value)
    public TextView myReturnsDetailsPriceValueTextView;
    @BindView(R.id.viewholder_returns_details_subtotal_value)
    public TextView myReturnsDetailsSubTotalValueTextView;

    public ReturnDetailsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
