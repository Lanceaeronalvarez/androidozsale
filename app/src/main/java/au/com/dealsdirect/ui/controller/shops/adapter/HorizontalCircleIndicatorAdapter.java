package au.com.dealsdirect.ui.controller.shops.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HorizontalCircleIndicatorAdapter extends RecyclerView.Adapter<HorizontalCircleIndicatorAdapter.ViewHolder> {
    private int mItemCount = 0;
    private int mSelectedPosition = -1;
    private int mPreviousSelectedPosition = -1;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.circle_indicator_image_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position != mSelectedPosition) {
            holder.circleIndicatorImage.setImageResource(R.drawable.circle_indicator_inactive);
        } else {
            holder.circleIndicatorImage.setImageResource(R.drawable.circle_indicator_active);
        }
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }

    public void setItemCount(int count) {
        mItemCount = count;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.vh_sale_item_image)
        ImageView circleIndicatorImage;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        mSelectedPosition = mItemCount > 0 ? selectedPosition % mItemCount : -1;
        if (mPreviousSelectedPosition != mSelectedPosition) {
            notifyDataSetChanged();
        }
        mPreviousSelectedPosition = mSelectedPosition;
    }
}
