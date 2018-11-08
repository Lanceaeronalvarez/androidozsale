package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.ScalableImageView;
import com.mysale.genie.utility.GenericEvent;
import com.mysale.genie.utility.RxBus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.LoadImagesListener;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpView;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * dp Created by Admin on 6/25/17.c
 */

public class SaleItemDetailsImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean mIsTablet;
    private View mContainerToToggle;
    private List<View> mViewsToToggle = new ArrayList<>();
    private List<String> mData = new LinkedList<>();
    private LoadImagesListener mLoadImagesListener;
    private int mViewType;
    private Drawable mPlaceholder;
    private RequestListener mRequestListener = new RequestListener() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
            mLoadImagesListener.imagesLoaded();
            return false;
        }
    };
    private SaleItemDetailsMvpView mSaleItemDetailsView;

    public void replaceData(List<String> data) {
        if (shouldUpdateData(mData, data)) {
            mData = data;
            notifyDataSetChanged();
        }
    }

    private boolean shouldUpdateData(List<String> currentData, List<String> newData) {
        if(currentData.size() == 0) {
            return true;
        }

        if (currentData.size() != newData.size()) {
            return true;
        }

        for(int i = 0; i < currentData.size(); i++) {
            if(!currentData.get(i).equals(newData.get(i))) {
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

    public SaleItemDetailsImageAdapter(boolean isTablet,
                                       View container,
                                       ArrayList<View> views,
                                       LoadImagesListener loadImagesListener,
                                       List<String> data,
                                       int viewType,
                                       Drawable placeholder, SaleItemDetailsMvpView saleItemDetailsMvpView) {

        this.mIsTablet = isTablet;
        this.mContainerToToggle = container;
        this.mViewsToToggle = views != null ? views : new ArrayList<>();
        this.mLoadImagesListener = loadImagesListener;
        this.mData = data;
        this.mViewType = viewType;
        this.mPlaceholder = placeholder;
        this.mSaleItemDetailsView = saleItemDetailsMvpView;
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
            ((ScalableImageView) vh.image).init();
        }

        initializeViewHolder(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        Context context = vh.image.getContext();
        switch (mViewType) {
            case 1:
                if (mData.size() > 0) {
                    String url = mData.get(position);
                    if (position == 0) {
                        ImageUtils.loadImageWithPlaceholder(url, vh.image, mPlaceholder, mRequestListener);
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
        mSaleItemDetailsView = null;
        mViewsToToggle = null;
        mContainerToToggle = null;
        mLoadImagesListener = null;
        mPlaceholder = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        ViewHolder vh = (ViewHolder) holder;
        switch (mViewType) {
            case 1:
                ImageUtils.clearImage(vh.image);

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
                scalableImageView.setZoomable(mSaleItemDetailsView.getVerticalOffset() == 0);
                vh.eventBusSubscription = RxBus.instance().subscribe(action -> {
                    if (action instanceof Pair && ((Pair) action).first == GenericEvent.Events.SALE_ITEM_DETAILS_VERTICAL_OFFSET &&
                            !scalableImageView.getAttacher().isScaling()) {
                        scalableImageView.setZoomable((int) ((Pair) action).second == 0);
                    }
                });
                scalableImageView.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> {

                    boolean resetZoom = scalableImageView.getScale() <= 1.05f;
                    mSaleItemDetailsView.toggleClipPadding(resetZoom);
                    if (resetZoom) {
                        for (View v : mViewsToToggle) {
                            v.setVisibility(View.VISIBLE);
                        }
                    } else {
                        for (View v : mViewsToToggle) {
                            v.setVisibility(!mIsTablet ||
                                    (mIsTablet && !context.getResources().getBoolean(R.bool.is_item_details_split_enabled)) ? View.GONE : View.INVISIBLE);
                        }
                    }
                });
                break;
            default:
                break;
        }
    }
}
