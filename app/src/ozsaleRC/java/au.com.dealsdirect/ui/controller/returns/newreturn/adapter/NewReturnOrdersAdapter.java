package au.com.dealsdirect.ui.controller.returns.newreturn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

public class NewReturnOrdersAdapter extends RecyclerView.Adapter<NewReturnOrderViewHolder> {

    private int lastPosition = -1;
    private final NewReturnOrderUpdateListener mUpateListener;
    Boolean[] mDataChecked;

    public HashMap<String, NewReturnOrderViewHolder> returnViewMap = new HashMap<>();
    public HashMap<Integer, Integer> returnItemsMap = new HashMap<>();
    List<au.com.dealsdirect.data.network.model.returns.newreturn.List> mCurrentReturnList = Collections.emptyList();
    Context mContext;

    public NewReturnOrdersAdapter(
            List<au.com.dealsdirect.data.network.model.returns.newreturn.List> orderList,
            Context context,
            NewReturnOrderUpdateListener updateListener) {

        this.mCurrentReturnList = orderList;
        this.mContext = context;
        this.mUpateListener = updateListener;
        this.mDataChecked = new Boolean[mCurrentReturnList.size()];
        Arrays.fill(this.mDataChecked, false);

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
    public void onBindViewHolder(NewReturnOrderViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (position == mCurrentReturnList.size() - 1) {
            holder.newReturnItemViewDivider.setVisibility(View.GONE);
        }

        if (returnItemsMap.get(position) == null)
            returnItemsMap.put(position, 1);

        Object productNameObject = mCurrentReturnList.get(position).getItem();
        Object productSizeObject = mCurrentReturnList.get(position).getSize();
        Object productItemCountObject = mCurrentReturnList.get(position).getCount();
        Object productPriceObject = PriceUtils.getPriceStringValue(mCurrentReturnList.get(position).getPrice());
        Object productSubTotalObject = PriceUtils.getPriceStringValue(mCurrentReturnList.get(position).getSubtotal());

        Object productId = mCurrentReturnList.get(position).getID();
        int productItemCountInt = Integer.parseInt(productItemCountObject.toString());
        if (productItemCountInt > 0) {


            String productName = "";
            String productSize = "";
            String productItemId = "";
            String productItemCount = "1";
            String productPrice = "";
            String productSubtotal = "";


            if (productId != null) {
                productItemId = productId.toString();
            }
            if (productNameObject != null) {
                productName = productNameObject.toString();
            }
            if (productSizeObject != null) {
                productSize = productSizeObject.toString();
            }
            if (productItemCountObject != null) {

                productItemCount = productItemCountObject.toString();
                holder.productQuantityLayout.setMax(Integer.valueOf(productItemCount));
                holder.productQuantityLayout.setMin(1);
                holder.productQuantityLayout.setQuantity(Integer.valueOf(productItemCount));
                holder.productQuantityLayout.setEditTextToNonEditable();

                holder.newReturnItemCheckBox.setChecked(mDataChecked[position]);
                holder.newReturnItemCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkBoxToggleState(position);
                    }
                });

                holder.productQuantityLayout.setOnQuantityChangeListener(new ProductQuantityLayout.onQuantityChangeListener() {
                    @Override
                    public void onQuantityIncrease(ProductQuantityLayout view, int value) {

                        checkBoxSetState(position, true);
                        String stringIterateValue = holder.productQuantityLayout.getQuantity();
                        int iterateValue = Integer.parseInt(stringIterateValue);
                        returnItemsMap.put(position, iterateValue);

                        mUpateListener.onReturnValueUpdated(
                                holder, position, mCurrentReturnList.get(position).getID(), true, iterateValue);
                        holder.productQuantityLayout.resetLoaders();
                    }

                    @Override
                    public void onQuantityDecrease(ProductQuantityLayout view, int value) {

                        checkBoxSetState(position, true);
                        String stringIterateValue = holder.productQuantityLayout.getQuantity();
                        int iterateValue = Integer.parseInt(stringIterateValue);
                        returnItemsMap.put(position, iterateValue);

                        mUpateListener.onReturnValueUpdated(
                                holder, position, mCurrentReturnList.get(position).getID(),
                                false, value);
                        holder.productQuantityLayout.resetLoaders();

                    }
                });
            }

            if (productPriceObject != null) {
                productPrice = productPriceObject.toString();
            }

            if (productSubtotal != null) {
                productSubtotal = productSubTotalObject.toString();
            }


            if (productSize.isEmpty()) {
                holder.newReturnItemSizeRowContainer.setVisibility(View.GONE);
            }

            holder.newReturnItemPriceTextView.setText(productPrice);
            holder.newReturnItemSubTotalTextView.setText(productSubtotal);
            holder.newReturnItemNameTextView.setText(productName);
            holder.newReturnItemSizeTextView.setText(productSize);
            holder.newReturnItemCountTextView.setText(productItemCount);

            String imageBrandId = mCurrentReturnList.get(position).getBrandID();
            String imageId = mCurrentReturnList.get(position).getImageID();
            String imageFileName = mCurrentReturnList.get(position).getFileName();

            String imageUrl = LegacyStringImageUtils.generateImageUrl(imageBrandId, imageId, imageFileName);

            ImageUtils.loadImageImmediate(mContext, imageUrl, holder.newReturnItemImageView, null);
            mUpateListener.onReturnValueUpdated(
                    holder, position, mCurrentReturnList.get(position).getID(), true, 1);

        } else {
            holder.newReturnItemContainer.setVisibility(View.GONE);
        }

        returnViewMap.put("" + position, holder);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void checkBoxToggleState(int position) {
        if (mDataChecked[position]) {
            mDataChecked[position] = false;
        } else {
            mDataChecked[position] = true;
        }
        notifyDataSetChanged();
    }

    public void checkBoxSetState(int position, boolean boolVal) {
        mDataChecked[position] = boolVal;
        notifyDataSetChanged();
    }

    public HashMap<String, NewReturnOrderViewHolder> getReturnMapView() {
        return returnViewMap;
    }

    @Override
    public int getItemCount() {
        return mCurrentReturnList.size();
    }
}
