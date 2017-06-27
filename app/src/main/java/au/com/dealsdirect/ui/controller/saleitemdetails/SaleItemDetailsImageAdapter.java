package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.ScalableImageView;

import java.util.LinkedList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SaleItemDetailsImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    List<String> mData = new LinkedList<>();
    Context mContext;
    String mSaleId;
    SaleItemDetailsController mSaleItemDetailsController;
    int mViewType;

    public void replaceData(List<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.productImage)
        ImageView image;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public SaleItemDetailsImageAdapter(
            SaleItemDetailsController saleItemDetailsController,
            List<String> data,
            String saleId,
            int viewType) {
        this.mSaleItemDetailsController = saleItemDetailsController;
        this.mData = data;
        this.mSaleId = saleId;
        this.mViewType = viewType;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = null;

        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_details_image_row, parent, false);
        } else if (viewType == 2) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.circle_indicator_image_layout, parent, false);
        }

        SaleItemDetailsImageAdapter.ViewHolder vh = new SaleItemDetailsImageAdapter.ViewHolder(view);
        if (vh.image instanceof ScalableImageView) {
            //pass presenter in the future
            ((ScalableImageView) vh.image).init();
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SaleItemDetailsImageAdapter.ViewHolder vh = (SaleItemDetailsImageAdapter.ViewHolder) holder;

        switch (mViewType) {
            case 1:
                if (mData.size() != 0) {
                    String url = mData.get(position);

                    ImageUtils.loadImage(mContext, url, vh.image);
//                    if (position == 0) {
//                        vh.image.setTransitionName(mData.getID());
//                    } else {
//                        vh.image.setTransitionName(mData.getID() + position);
//                    }
                }
                break;
            case 2:
                if (position != 0) {
                    vh.itemView.setAlpha(0.40f);
                }

                vh.image.setImageDrawable(ContextCompat.getDrawable(vh.image.getContext(),
                        R.drawable.circle_indicator_active));
                break;
            default:
                break;
        }


    }

    @Override public int getItemViewType(int position) {
        return mViewType;
    }

    @Override public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }
}
