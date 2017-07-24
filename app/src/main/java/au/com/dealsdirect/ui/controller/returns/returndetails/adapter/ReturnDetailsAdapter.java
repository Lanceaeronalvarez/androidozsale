package au.com.dealsdirect.ui.controller.returns.returndetails.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.returndetails.Item;
import au.com.dealsdirect.ui.controller.returns.returndetails.viewholder.ReturnDetailsViewHolder;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.PriceUtils;

/**
 * dp Created by Admin on 6/30/17.
 */

public class ReturnDetailsAdapter extends RecyclerView.Adapter<ReturnDetailsViewHolder> {

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

    @Override public ReturnDetailsViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_return_details, parent,
                        false);

        ReturnDetailsViewHolder holder = new ReturnDetailsViewHolder(v);
        return holder;
    }


    @Override public void onBindViewHolder(ReturnDetailsViewHolder holder, int position) {
        String itemCost = PriceUtils.getPriceStringValue(mReturnDetailsList.get(position).getPrice());
        String itemSubTotal = PriceUtils.getPriceStringValue(mReturnDetailsList.get(position).getSubTotal());

        String itemSize = mReturnDetailsList.get(position).getSize();
        String brandId = mReturnDetailsList.get(position).getBrandID();
        String imageId = mReturnDetailsList.get(position).getImageID();
        String fileName = mReturnDetailsList.get(position).getFile();
        int itemReturnCount = mReturnDetailsList.get(position).getCount();

//
        String imageUrl = ImageUtils.generateImageUrl(brandId,imageId,fileName);
        ImageUtils.loadImage(holder.myReturnsDetailsProductImageView.getContext(),imageUrl,holder.myReturnsDetailsProductImageView
        );


        holder.myReturnsDetailsProductNameValueTextView.setText(mReturnDetailsList.get(position).getItem());
        holder.myReturnsDetailsPriceValueTextView.setText(itemCost);
        holder.myReturnsDetailsSubTotalValueTextView.setText(itemSubTotal);
        holder.myReturnsDetailsProductItemCountValueTextView.setText(itemReturnCount+" ");

        if (itemSize.isEmpty()){
            holder.myReturnDetailSizeContainer.setVisibility(View.GONE);
        }else{
            holder.myReturnsDetailsProductItemSizeValueTextView.setText(itemSize);
        }


        //setAnimation(holder.currentReturnProductItem, position);

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
}
