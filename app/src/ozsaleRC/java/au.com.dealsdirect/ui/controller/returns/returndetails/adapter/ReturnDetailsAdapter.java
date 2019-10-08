package au.com.dealsdirect.ui.controller.returns.returndetails.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.returndetails.Item;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/30/17.
 */

public class ReturnDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int lastPosition = -1;

    List<Item> mReturnDetailsList = Collections.emptyList();
    double mSubTotal;
    Context mContext;

    public ReturnDetailsAdapter(
            List<Item> orderList,
            double subtotal,
            Context context){

        this.mSubTotal = subtotal;
        this.mReturnDetailsList = orderList;
        this.mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_return_details, parent, false);
        return new ReturnDetailsViewHolder(v);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String itemCost = PriceUtils.getPriceStringValue(mReturnDetailsList.get(position).getPrice());
        String itemSubTotal = PriceUtils.getPriceStringValue(mReturnDetailsList.get(position).getSubTotal());

        Log.d("itemiterator", "position = "+position );
        String itemSize = mReturnDetailsList.get(position).getSize();
        String brandId = mReturnDetailsList.get(position).getBrandID();
        String imageId = mReturnDetailsList.get(position).getImageID();
        String fileName = mReturnDetailsList.get(position).getFile();
        int itemReturnCount = mReturnDetailsList.get(position).getCount();
        Log.d("itemiterator", "position = "+position+ "itemssize  = "+itemSize +" , brandid = "+brandId+" , imageid = "+imageId );

        String imageUrl = LegacyStringImageUtils.generateImageUrl(brandId,imageId,fileName);
        ImageUtils.loadImage(imageUrl, ((ReturnDetailsViewHolder) holder).myReturnsDetailsProductImageView);


        ((ReturnDetailsViewHolder) holder).myReturnsDetailsProductNameValueTextView.setText(mReturnDetailsList.get(position).getItem());
        ((ReturnDetailsViewHolder) holder).myReturnsDetailsPriceValueTextView.setText(itemCost);
        ((ReturnDetailsViewHolder) holder).myReturnsDetailsSubTotalValueTextView.setText(itemSubTotal);
        ((ReturnDetailsViewHolder) holder).myReturnsDetailsProductItemCountValueTextView.setText(itemReturnCount+" ");

        if (itemSize.isEmpty()){
            ((ReturnDetailsViewHolder) holder).myReturnDetailSizeContainer.setVisibility(View.GONE);
        }else{
            ((ReturnDetailsViewHolder) holder).myReturnsDetailsProductItemSizeValueTextView.setText(itemSize);
        }

    }


    @Override public int getItemCount() {
        if (mReturnDetailsList == null){
            return 0;
        }
        return mReturnDetailsList.size();
    }

    @Override public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    class ReturnDetailsViewHolder extends RecyclerView.ViewHolder {
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

        ReturnDetailsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
