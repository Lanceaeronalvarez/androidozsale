package au.com.dealsdirect.ui.controller.returns.newreturn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.returns.newreturn.listener.NewReturnOrderUpdateListener;
import au.com.dealsdirect.ui.controller.returns.newreturn.viewholder.NewReturnOrderViewHolder;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import au.com.dealsdirect.utils.PriceUtils;

/**
 * dp Created by Admin on 7/25/17.
 */

public class NewReturnOrdersAdapter extends RecyclerView.Adapter<NewReturnOrderViewHolder>{

    private int lastPosition = -1;
    private final NewReturnOrderUpdateListener mUpateListener;

    List<au.com.dealsdirect.data.network.model.returns.newreturn.List> mCurrentReturnList = Collections.emptyList();
    Context mContext;

    public NewReturnOrdersAdapter(
            List<au.com.dealsdirect.data.network.model.returns.newreturn.List> orderList,
            Context context,
            NewReturnOrderUpdateListener updateListener){

        this.mCurrentReturnList = orderList;
        this.mContext = context;
        this.mUpateListener = updateListener;
        //this.mListener = listener;
    }

    @Override
    public NewReturnOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.viewholder_new_return_order, parent,
                        false);
        NewReturnOrderViewHolder holder = new NewReturnOrderViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(NewReturnOrderViewHolder holder, int position) {

        Log.d("NewReturnOrder", "entered position = "+position);

        if (position == mCurrentReturnList.size()-1){
            holder.newReturnItemViewDivider.setVisibility(View.GONE);
        }

        Object productNameObject = mCurrentReturnList.get(position).getItem();
        Object productSizeObject = mCurrentReturnList.get(position).getSize();
        Object productItemCountObject = mCurrentReturnList.get(position).getCount();
        Object productPriceObject = PriceUtils.getPriceStringValue(mCurrentReturnList.get(position).getPrice());

        Object productId = mCurrentReturnList.get(position).getID();
        int productItemCountInt =  Integer.parseInt(productItemCountObject.toString());
        if (productItemCountInt > 0){


            String productName = "";
            String productSize = "";
            String productItemId = "";
            String productItemCount = "1";
            String productPrice = "";


            if (productId!= null){
                productItemId = productId.toString();
            }
            if (productNameObject != null){
                productName = productNameObject.toString();
            }
            if (productSizeObject != null){
                productSize = productSizeObject.toString();
            }
            if (productItemCountObject != null){
                //  productItemCount = productItemCountObject;
                //int productItemCountInt =  (Integer) productItemCountObject;
                productItemCount = productItemCountObject.toString();
                holder.productQuantityLayout.setMax(productItemCountInt);
                holder.productQuantityLayout.setQuantity(Integer.parseInt("1"));
                holder.productQuantityLayout.setEditTextToNonEditable();

//            mUpateListener.onReturnValueUpdated(
//                    holder, position, mCurrentReturnList.get(position).getID(), true, 1);


                holder.productQuantityLayout.setOnQuantityChangeListener(new ProductQuantityLayout.onQuantityChangeListener() {
                    @Override public void onQuantityIncrease(ProductQuantityLayout view, int value) {

                        String stringIterateValue = holder.productQuantityLayout.getQuantity();
                        int iterateValue = Integer.parseInt(stringIterateValue);
                        mUpateListener.onReturnValueUpdated(
                                holder, position, mCurrentReturnList.get(position).getID(), true, iterateValue);
                        holder.productQuantityLayout.resetLoaders();
                    }

                    @Override public void onQuantityDecrease(ProductQuantityLayout view, int value) {

                        String stringIterateValue = holder.productQuantityLayout.getQuantity();
                        int iterateValue = Integer.parseInt(stringIterateValue);

                        mUpateListener.onReturnValueUpdated(
                                holder, position, mCurrentReturnList.get(position).getID(),
                                false, iterateValue);
                        holder.productQuantityLayout.resetLoaders();

                    }
                });
                holder.productQuantityLayout.setOnClickListener(view -> {
//                        GDebug.log("return", "clicked");
                });
            }
            if (productPriceObject != null){
                productPrice = productPriceObject.toString();
            }


            if (productSize.isEmpty()){
                holder.newReturnItemSizeRowContainer.setVisibility(View.GONE);
            }

            holder.newReturnItemPriceTextView.setText(productPrice);
            holder.newReturnItemNameTextView.setText(productName);
            holder.newReturnItemSizeTextView.setText(productSize);
            holder.newReturnItemCountTextView.setText(productItemCount);
            holder.newReturnItemIdTextView.setText(productItemId);

            String imageBrandId = mCurrentReturnList.get(position).getBrandID();
            String imageId = mCurrentReturnList.get(position).getImageID();
            String imageFileName = mCurrentReturnList.get(position).getFileName();

            String imageUrl = LegacyStringImageUtils.generateImageUrl(imageBrandId,imageId,imageFileName);

            ImageUtils.loadImage(mContext,imageUrl,holder.newReturnItemImageView);
            mUpateListener.onReturnValueUpdated(
                    holder, position, mCurrentReturnList.get(position).getID(), true, 1);

//        holder.newReturnItemImageView.setImageDrawable(
//                mContext.getResources()
//                        .getDrawable(R.drawable.girl));
        }else {
            holder.newReturnItemContainer.setVisibility(View.GONE);
        }

        //setAnimation(holder.currentReturnProductItem, position);
    }

    @Override
    public int getItemCount() {
        return mCurrentReturnList.size();
    }
}
