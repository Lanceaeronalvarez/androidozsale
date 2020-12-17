package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.CustomPhotoViewAttacher;
import com.github.chrisbanes.photoview.ScalableImageView;
import com.mysale.genie.utility.GenericEvent;
import com.mysale.genie.utility.RxBus;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.LoadImagesListener;
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.SaleDetailsImageListener;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

/**
 * dp Created by Admin on 6/25/17.c
 */

public class SaleItemDetailsImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean mIsTablet;
    private List<String> mData;
    private LoadImagesListener mLoadImagesListener;
    private int mViewType;
    private SaleDetailsImageListener mSaleDetailsListener;
    private ImageUtils.ImageLoadedCallback mImageLoadedCallback = new ImageUtils.ImageLoadedCallback() {
        @Override
        public void onImageResourceReady(Bitmap resource) {
            super.onImageResourceReady(resource);
            mLoadImagesListener.imagesLoaded();
        }
    };
    private Activity mActivity;

    public void replaceData(List<String> data) {
        if (shouldUpdateData(mData, data)) {
            mData = data;
            notifyDataSetChanged();
        }
    }

    private boolean shouldUpdateData(List<String> currentData, List<String> newData) {
        if (currentData.size() == 0) {
            return true;
        }

        if (currentData.size() != newData.size()) {
            return true;
        }

        for (int i = 0; i < currentData.size(); i++) {
            if (!currentData.get(i).equals(newData.get(i))) {
                return true;
            }
        }

        return false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vh_sale_item_image)
        ImageView image;

        Disposable eventBusSubscription;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public abstract class OnPositionChangedListener {
        public abstract void onPositionChanged(int position);
    }

    public SaleItemDetailsImageAdapter(Activity activity,
                                       boolean isTablet,
                                       LoadImagesListener loadImagesListener,
                                       List<String> data,
                                       int viewType,
                                       SaleDetailsImageListener saleDetailsImageListener) {

        this.mActivity = activity;
        this.mIsTablet = isTablet;
        this.mLoadImagesListener = loadImagesListener;
        this.mData = data;
        this.mViewType = viewType;
        this.mSaleDetailsListener = saleDetailsImageListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_details_image_row, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.circle_indicator_image_layout, parent, false);
                break;
        }

        ViewHolder vh = new ViewHolder(view);
        if (vh.image instanceof ScalableImageView) {
            //pass presenter in the future
            ScalableImageView imageView = ((ScalableImageView) vh.image);

            imageView.init();
            imageView.setOnDoubleTapListener(null);
            imageView.setZoomSnapBackMode(CustomPhotoViewAttacher.ZoomSnapBackMode.TO_MINIMUM);
        }

        initializeViewHolder(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        switch (mViewType) {
            case 1:
                if (mData.size() > 0) {
                    String url = mData.get(position);
                    if (position == 0) {
                        ImageUtils.loadImageImmediate(url, vh.image, mImageLoadedCallback);
                    } else {
                        ImageUtils.loadImage(url, vh.image);
                    }
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
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mLoadImagesListener = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        ViewHolder vh = (ViewHolder) holder;
        switch (mViewType) {
            case 1:
                if (!mActivity.isDestroyed()) {
                    ImageUtils.clearImage(vh.image);
                }

                ScalableImageView scalableImageView = (ScalableImageView) vh.image;
                RxBus.instance().unSubscribe(vh.eventBusSubscription);
                scalableImageView.setOnScaleChangeListener(null);
                break;
            default:
                break;
        }
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemViewType(int position) {
        return mViewType;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    private void initializeViewHolder(ViewHolder vh) {
        Context context = vh.image.getContext();
        switch (mViewType) {
            case 1:
                ScalableImageView scalableImageView = (ScalableImageView) vh.image;
                scalableImageView.setZoomable(mSaleDetailsListener.getVerticalOffset() == 0);
                vh.eventBusSubscription = RxBus.instance().subscribe(action -> {
                    if (action instanceof Pair && ((Pair) action).first == GenericEvent.Events.SALE_ITEM_DETAILS_VERTICAL_OFFSET &&
                            !scalableImageView.getAttacher().isScaling()) {
                        scalableImageView.setZoomable((int) ((Pair) action).second == 0);
                    }
                });
                scalableImageView.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> {
                    final float scale = scalableImageView.getScale();
                    final boolean resetZoom = scale <= Math.round(1.00f);
                    mSaleDetailsListener.toggleClipPadding(resetZoom);
                    mSaleDetailsListener.onImageRescale(scale);
                });
                break;
            default:
                break;
        }
    }
}
