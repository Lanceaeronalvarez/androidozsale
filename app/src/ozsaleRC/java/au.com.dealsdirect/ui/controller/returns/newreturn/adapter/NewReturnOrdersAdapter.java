package au.com.dealsdirect.ui.controller.returns.newreturn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderList;
import au.com.dealsdirect.ui.controller.returns.newreturn.NewReturnMvpPresenter;
import au.com.dealsdirect.ui.controller.returns.newreturn.viewholder.NewReturnOrderViewHolder;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import au.com.dealsdirect.utils.PriceUtils;

/**
 * dp Created by Admin on 7/25/17.
 */

public class NewReturnOrdersAdapter extends RecyclerView.Adapter<NewReturnOrderViewHolder> {

    private NewReturnMvpPresenter mPresenter;
    Boolean[] mDataChecked;

    public HashMap<String, NewReturnOrderViewHolder> returnViewMap = new HashMap<>();
    List<NewReturnOrderList> mCurrentReturnList = Collections.emptyList();
    Context mContext;
    String mProductId;

    public NewReturnOrdersAdapter(
            List<NewReturnOrderList> orderList,
            Context context,
            NewReturnMvpPresenter mvpPresenter,
            String productId) {

        mCurrentReturnList = orderList;
        mContext = context;
        mPresenter = mvpPresenter;
        mProductId = productId;
        mDataChecked = new Boolean[mCurrentReturnList.size()];
        Arrays.fill(this.mDataChecked, false);
    }

    @Override
    public NewReturnOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.viewholder_new_return_order, parent, false);
        NewReturnOrderViewHolder holder = new NewReturnOrderViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(NewReturnOrderViewHolder holder, int position) {

        String productId = mCurrentReturnList.get(position).getID();
        String productName = mCurrentReturnList.get(position).getItem();
        int productItemCount = mCurrentReturnList.get(position).getCount();
        String productPrice = PriceUtils.getPriceStringValue(mCurrentReturnList.get(position).getPrice());
        String productSubTotal = PriceUtils.getPriceStringValue(mCurrentReturnList.get(position).getSubtotal());

        holder.newReturnItemQuantityText.setText(String.valueOf(productItemCount));
        holder.newReturnItemPriceTextView.setText(productPrice);
        holder.newReturnItemSubTotalTextView.setText(productSubTotal);
        holder.newReturnItemNameTextView.setText(productName);

        holder.productQuantityLayout.setMax(productItemCount);
        holder.productQuantityLayout.setQuantity(1);
        holder.productQuantityLayout.setEditTextToNonEditable();

        if (mProductId != null && !mProductId.isEmpty()) {
            if (productId.equalsIgnoreCase(mProductId)) {
                holder.newReturnItemCheckBox.setChecked(true);
                mDataChecked[position] = true;
                int quantityVal = Integer.valueOf(holder.productQuantityLayout.getQuantity());
                if(quantityVal == 0){
                    holder.productQuantityLayout.setQuantity(1);
                }
                mPresenter.updateReturnValue(productId, position, Integer.valueOf(holder.productQuantityLayout.getQuantity()), true);

            } else {
                holder.newReturnItemCheckBox.setChecked(mDataChecked[position]);
            }
        } else {
            holder.newReturnItemCheckBox.setChecked(mDataChecked[position]);
        }

        holder.newReturnItemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDataChecked[position] = isChecked;
                int quantityVal = Integer.valueOf(holder.productQuantityLayout.getQuantity());
                if(isChecked && quantityVal == 0){
                    holder.productQuantityLayout.setQuantity(1);
                }
                mPresenter.updateReturnValue(productId, position, Integer.valueOf(holder.productQuantityLayout.getQuantity()), isChecked);
            }
        });

        holder.productQuantityLayout.setOnQuantityChangeListener(new ProductQuantityLayout.onQuantityChangeListener() {
            @Override
            public void onQuantityIncrease(ProductQuantityLayout view, int value) {
                mPresenter.updateReturnValue(productId, position, value, mDataChecked[position]);
                holder.productQuantityLayout.resetLoaders();
            }

            @Override
            public void onQuantityDecrease(ProductQuantityLayout view, int value) {
                if(mDataChecked[position] && value == 0){
                    mDataChecked[position] = false;
                    notifyItemChanged(position);
                } else {
                    mPresenter.updateReturnValue(productId, position, value, mDataChecked[position]);
                }
                holder.productQuantityLayout.resetLoaders();
            }
        });

        String imageBrandId = mCurrentReturnList.get(position).getBrandID();
        String imageId = mCurrentReturnList.get(position).getImageID();
        String imageFileName = mCurrentReturnList.get(position).getFileName();

        String imageUrl = LegacyStringImageUtils.generateImageUrl(imageBrandId, imageId, imageFileName);

        ImageUtils.loadImageImmediate(imageUrl, holder.newReturnItemImageView, null);
    }

    @Override
    public int getItemCount() {
        return mCurrentReturnList != null ? mCurrentReturnList.size() : 0;
    }

    public List<NewReturnOrderList> getData() {
        return mCurrentReturnList;
    }
}
