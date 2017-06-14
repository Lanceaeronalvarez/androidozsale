package au.com.dealsdirect.ui.controller.productdetails;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.ScalableImageView;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicItemDetailsResponse;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 09/06/2017.
 */

public class ProductDetailsImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    GetPublicItemDetailsResponse.Value mData;
    Context mContext;
    String mSaleId;
    ProductDetailsController mProductDetailsController;
    int mViewType;

    public void replaceData(GetPublicItemDetailsResponse.Value data) {
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

    public ProductDetailsImageAdapter(
            ProductDetailsController productDetailsController,
            GetPublicItemDetailsResponse.Value data,
            String saleId,
            int viewType) {
        this.mProductDetailsController = productDetailsController;
        this.mData = data;
        this.mSaleId = saleId;
        this.mViewType = viewType;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = null;

        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                                 .inflate(R.layout.product_details_image_row, parent, false);
        } else if (viewType == 2) {
            view = LayoutInflater.from(parent.getContext())
                                 .inflate(R.layout.circle_indicator_image_layout, parent, false);
        }

        ProductDetailsImageAdapter.ViewHolder vh = new ProductDetailsImageAdapter.ViewHolder(view);
        if (vh.image instanceof ScalableImageView) {
            //pass presenter in the future
            ((ScalableImageView) vh.image).init();
        }
        return vh;
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ProductDetailsImageAdapter.ViewHolder vh = (ProductDetailsImageAdapter.ViewHolder) holder;

        switch (mViewType) {
            case 1:
                if (mData.getImages()
                         .size() != 0) {
                    String url =
                            LegacyStringImageUtils.productDetailsImageURLString(mData.getBrandID(),
                                                                                mData.getImages()
                                                                                     .get(position)
                                                                                     .getID(),
                                                                                mData.getImages()
                                                                                     .get(position)
                                                                                     .getPreview());


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
            return mData.getImages()
                        .size();
        } else {
            return 0;
        }
    }

}
