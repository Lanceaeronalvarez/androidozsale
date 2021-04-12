package au.com.dealsdirect.ui.controller.returns.returndetails.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturn;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/30/17.
 */

public class ReturnDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<CurrentReturn.Item> mReturnDetailsList;

    public ReturnDetailsAdapter(
            List<CurrentReturn.Item> orderList) {
        this.mReturnDetailsList = orderList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_return_details, parent, false);
        return new ReturnDetailsViewHolder(v);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ReturnDetailsViewHolder viewHolder = (ReturnDetailsViewHolder) holder;
        final CurrentReturn.Item item = mReturnDetailsList.get(position);
        viewHolder.myReturnsDetailsPriceValueTextView.setText(PriceUtils.getPriceStringValue(item.getPrice()));
        viewHolder.myReturnsDetailsSubTotalValueTextView.setText(PriceUtils.getPriceStringValue(item.getPriceTotal()));
        viewHolder.myReturnsDetailsProductItemCountValueTextView.setText(Integer.toString(item.getQuantity()));
        viewHolder.myReturnsDetailsProductItemSizeValueTextView.setText(item.getSize());
        viewHolder.myReturnsDetailsTitleTextView.setText(item.getName());

        ImageUtils.loadImage(item.getImageUrl(), viewHolder.myReturnsDetailsProductImageView);
    }


    @Override
    public int getItemCount() {
        if (mReturnDetailsList == null) {
            return 0;
        }
        return mReturnDetailsList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class ReturnDetailsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_return_detail_row_container)
        public LinearLayout myReturnDetailItem;
        @BindView(R.id.viewholder_returns_details_size_container)
        public ViewGroup myReturnDetailSizeContainer;
        @BindView(R.id.viewholder_return_detail_image_view)
        public ImageView myReturnsDetailsProductImageView;
        @BindView(R.id.viewholder_return_details_item_title)
        public TextView myReturnsDetailsTitleTextView;
        @BindView(R.id.viewholder_return_details_item_subtitle)
        public TextView myReturnsDetailsSubtitleTextView;
        @BindView(R.id.viewholder_returns_details_item_count_value)
        public TextView myReturnsDetailsProductItemCountValueTextView;
        @BindView(R.id.viewholder_returns_details_size_value)
        public TextView myReturnsDetailsProductItemSizeValueTextView;
        @BindView(R.id.viewholder_returns_order_item_total_cost_value)
        public TextView myReturnsDetailsPriceValueTextView;
        @BindView(R.id.viewholder_returns_details_subtotal_value)
        public TextView myReturnsDetailsSubTotalValueTextView;

        ReturnDetailsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
