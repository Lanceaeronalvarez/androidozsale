package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class SaleItemDetailsImagePageIndicatorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mData;

    private int activePosition = -1;

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

    public SaleItemDetailsImagePageIndicatorAdapter() {
        this.mData = new ArrayList<>();
    }

    public SaleItemDetailsImagePageIndicatorAdapter(List<String> data) {
        this.mData = data;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.circle_indicator_image_layout, parent, false);

        final ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        if (activePosition != position) {
            vh.image.setImageResource(R.drawable.circle_indicator_inactive);
        } else {
            vh.image.setImageResource(R.drawable.circle_indicator_active);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
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

    public int getActivePosition() {
        return activePosition;
    }

    public void setActivePosition(int activePosition) {
        this.activePosition = activePosition;
    }
}
