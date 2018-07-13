package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

/**
 * dp Created by Admin on 6/25/17.c
 */

public class SaleItemDetailsImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean mIsTablet;
    private View mContainerToToggle;
    private List<View> mViewsToToggle = new ArrayList<>();
    private List<String> mData = new LinkedList<>();
    private Context mContext;
    private LoadImagesListener mLoadImagesListener;
    private int mViewType;
    private Drawable mPlaceholder;
    private CompositeDisposable mEventBusSubscriptions = new CompositeDisposable();
    private RequestListener mRequestListener = new RequestListener() {
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
    private SaleItemDetailsMvpView mSaleItemDetailsView;

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
                    scalableImageView.setZoomable(mSaleItemDetailsView.getVerticalOffset() == 0);
                    mEventBusSubscriptions.add(RxBus.instance().subscribe(action -> {
                        if (action instanceof Pair && ((Pair) action).first == GenericEvent.Events.SALE_ITEM_DETAILS_VERTICAL_OFFSET &&
                                !scalableImageView.getAttacher().isScaling()) {
                            scalableImageView.setZoomable((int) ((Pair) action).second == 0);
                        }
                    }));
                    scalableImageView.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> {

                        boolean resetZoom = scalableImageView.getScale() <= 1.05f;
                        if (resetZoom) {
                            for (View v : mViewsToToggle) {
                                v.setVisibility(View.VISIBLE);
                            }

                            if (mContainerToToggle != null && !mIsTablet) {
                                mContainerToToggle.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            }

                        } else {
                            for (View v : mViewsToToggle) {
                                v.setVisibility(!mIsTablet ? View.GONE : View.INVISIBLE);
                            }

                            if (mContainerToToggle != null && !mIsTablet) {
                                mContainerToToggle.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                            }
                        }

                    });
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
        super.onDetachedFromRecyclerView(recyclerView);
        mEventBusSubscriptions.dispose();
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
