package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.CustomPhotoViewAttacher;
import com.github.chrisbanes.photoview.ScalableImageView;
import com.mysale.genie.utility.GenericEvent;
import com.mysale.genie.utility.RxBus;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.SaleDetailsImageListener;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class SaleItemDetailsImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mData;
    private final SaleDetailsImageListener mSaleDetailsListener;
    private final ImageUtils.ImageLoadedCallback mImageLoadedCallback = new ImageUtils.ImageLoadedCallback() {
        @Override
        public void onImageResourceReady(Bitmap resource) {
            super.onImageResourceReady(resource);
            if (mSaleDetailsListener != null) {
                mSaleDetailsListener.imagesLoaded();
            }
        }
    };

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

    public abstract static class OnPositionChangedListener {
        public abstract void onPositionChanged(int position);
    }

    public SaleItemDetailsImageAdapter(List<String> data,
                                       SaleDetailsImageListener saleDetailsImageListener) {
        this.mData = data;
        this.mSaleDetailsListener = saleDetailsImageListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_details_image_row, parent, false);

        final ViewHolder vh = new ViewHolder(view);
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
        if (mData.size() > 0) {
            String url = mData.get(position);
            if (position == 0) {
                ImageUtils.loadImageImmediate(url, vh.image, mImageLoadedCallback);
            } else {
                ImageUtils.loadImage(url, vh.image);
            }
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        ViewHolder vh = (ViewHolder) holder;
        ImageUtils.clearImage(vh.image);

        ScalableImageView scalableImageView = (ScalableImageView) vh.image;
        RxBus.instance().unSubscribe(vh.eventBusSubscription);
        scalableImageView.setOnScaleChangeListener(null);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    private void initializeViewHolder(ViewHolder vh) {
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
    }
}
