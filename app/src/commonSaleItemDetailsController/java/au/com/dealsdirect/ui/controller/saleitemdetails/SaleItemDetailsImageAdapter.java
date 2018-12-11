package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.ScalableImageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.LoadImagesListener;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SaleItemDetailsImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<View> mViewsToToggle = new ArrayList<>();
    List<String> mData = new LinkedList<>();
    Context mContext;
    String mSaleId;
    LoadImagesListener mLoadImagesListener;
    int mViewType;
    Drawable mPlaceholder;
    RequestListener mRequestListener = new RequestListener() {
        @Override
        public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
            mLoadImagesListener.imagesLoaded();
            return false;
        }
    };

    public void replaceData(List<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vh_sale_item_image)
        ImageView image;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public SaleItemDetailsImageAdapter(
            ArrayList<View> views,
            LoadImagesListener loadImagesListener,
            List<String> data,
            String saleId,
            int viewType,
            Drawable placeholder) {

        this.mViewsToToggle = views != null ? views : new ArrayList<>();
        this.mLoadImagesListener = loadImagesListener;
        this.mData = data;
        this.mSaleId = saleId;
        this.mViewType = viewType;
        this.mPlaceholder = placeholder;
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

        ViewHolder vh = new ViewHolder(view);
        if (vh.image instanceof ScalableImageView) {
            //pass presenter in the future
            ((ScalableImageView) vh.image).init();
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        vh.image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_account_details));
        switch (mViewType) {
            case 1:
                if (mData.size() != 0) {
                    String url = mData.get(position);
                    if (position == 0) {
                        ImageUtils.loadImageWithPlaceholder(mContext, url, vh.image, mPlaceholder, mRequestListener);
                    } else {
                        ImageUtils.clearImage(vh.image);
                        ImageUtils.loadImage(mContext, url, vh.image);
                    }

                    ScalableImageView scalableImageView = (ScalableImageView) vh.image;
                    scalableImageView.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> {
                        for (View v : mViewsToToggle) {
                            try{
                                ((MainActivity)mContext).getMainController().getHomeController().hideBottomNav();
                            }catch (Exception e){
                                Log.d(SaleItemDetailsImageAdapter.class.getName(),e.getMessage());
                            }
                            v.setVisibility(View.INVISIBLE);
                        }

                        if (scalableImageView.getScale() <= 1.05f) {
                            for (View v : mViewsToToggle) {
                                try{
                                    ((MainActivity)mContext).getMainController().getHomeController().showBottomNav();
                                }catch (Exception e){
                                    Log.d(SaleItemDetailsImageAdapter.class.getName(),e.getMessage());
                                }
                                v.setVisibility(View.VISIBLE);
                            }
                        }
                    });

//                    if (position == 0) {
//                        vh.image.setTransitionName(mData.getID());
//                    } else {
//                        vh.image.setTransitionName(mData.getID() + position);
//                    }
                }
                break;
            case 2:
                if (position != 0) {
                    vh.image.setImageResource(R.drawable.circle_indicator_inactive);
                } else {
                    vh.image.setImageResource(R.drawable.circle_indicator_active);
                }
                break;
            default:
                break;
        }


    }

    @Override
    public int getItemViewType(int position) {
        return mViewType;
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }

}
