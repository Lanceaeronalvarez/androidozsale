package au.com.dealsdirect.ui.controller.returns.returndetails.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;

/**
 * dp Created by Admin on 6/30/17.
 */

public class ReturnDetailsViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout myReturnDetailItem;
    public ViewGroup myReturnDetailSizeContainer;

    public ImageView myReturnsDetailsProductImageView;

    public TextView myReturnsDetailsProductNameValueTextView;
    public TextView myReturnsDetailsProductItemCountValueTextView;
    public TextView myReturnsDetailsProductItemSizeValueTextView;
    public TextView myReturnsDetailsPriceValueTextView;
    public TextView myReturnsDetailsSubTotalValueTextView;

    public ReturnDetailsViewHolder(View itemView) {
        super(itemView);

        myReturnDetailItem = (LinearLayout) itemView.
                findViewById(R.id.viewholder_return_detail_row_container);

        myReturnDetailSizeContainer = (ViewGroup) itemView.
                findViewById(R.id.viewholder_returns_details_size_container);

        myReturnsDetailsProductImageView = (ImageView) itemView.
                findViewById(R.id.viewholder_return_detail_image_view);

        myReturnsDetailsProductNameValueTextView = (TextView) itemView.
                findViewById(R.id.viewholder_return_details_item_name);

        myReturnsDetailsProductItemSizeValueTextView = (TextView) itemView.
                findViewById(R.id.viewholder_returns_details_size_value);

        myReturnsDetailsProductItemCountValueTextView = (TextView) itemView.
                findViewById(R.id.viewholder_returns_details_item_count_value);

        myReturnsDetailsPriceValueTextView = (TextView) itemView.
                findViewById(R.id.viewholder_returns_order_item_total_cost_value);

        myReturnsDetailsSubTotalValueTextView = (TextView) itemView.
                findViewById(R.id.viewholder_returns_details_subtotal_value);


    }
}
