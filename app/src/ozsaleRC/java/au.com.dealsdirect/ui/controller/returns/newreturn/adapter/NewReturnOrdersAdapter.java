package au.com.dealsdirect.ui.controller.returns.newreturn.adapter;

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
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderList;
import au.com.dealsdirect.ui.controller.returns.newreturn.NewReturnMvpPresenter;
import au.com.dealsdirect.ui.controller.returns.newreturn.viewholder.NewReturnOrderViewHolder;
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

    public NewReturnOrdersAdapter(
            List<NewReturnOrderList> orderList,
            Context context,
            NewReturnMvpPresenter mvpPresenter) {

        mCurrentReturnList = orderList;
        mContext = context;
        mPresenter = mvpPresenter;
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

        if (position == mCurrentReturnList.size() - 1) {
            holder.newReturnItemViewDivider.setVisibility(View.GONE);
        }

        String productName = mCurrentReturnList.get(position).getItem();
        int productItemCount = mCurrentReturnList.get(position).getCount();
        String productPrice = PriceUtils.getPriceStringValue(mCurrentReturnList.get(position).getPrice());
        String productSubTotal = PriceUtils.getPriceStringValue(mCurrentReturnList.get(position).getSubtotal());

        holder.newReturnItemCheckBox.setChecked(mDataChecked[position]);
        holder.newReturnItemCheckBox.setOnClickListener(v -> checkBoxToggleState(position));


        holder.newReturnItemPriceTextView.setText(productPrice);
        holder.newReturnItemSubTotalTextView.setText(productSubTotal);
        holder.newReturnItemNameTextView.setText(productName);
        holder.newReturnItemCountTextView.setText(String.valueOf(productItemCount));

        String imageBrandId = mCurrentReturnList.get(position).getBrandID();
        String imageId = mCurrentReturnList.get(position).getImageID();
        String imageFileName = mCurrentReturnList.get(position).getFileName();

        String imageUrl = LegacyStringImageUtils.generateImageUrl(imageBrandId, imageId, imageFileName);

        ImageUtils.loadImageImmediate(mContext, imageUrl, holder.newReturnItemImageView, null);

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

    public Boolean[] getCheckedReturns() {
        return mDataChecked;
    }

    @Override
    public int getItemCount() {
        return mCurrentReturnList.size();
    }

    public List<NewReturnOrderList> getReturnList(){
        return mCurrentReturnList;
    }
}
