package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class SaleItemDetailsImagePageIndicatorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int numberOfPages = 0;

    private int activePosition = -1;

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
        notifyDataSetChanged();
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
        this.numberOfPages = 0;
    }

    public SaleItemDetailsImagePageIndicatorAdapter(int numberOfPages) {
        this.numberOfPages = numberOfPages;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        if (activePosition != position) {
            vh.image.setImageResource(R.drawable.circle_indicator_inactive);
        } else {
            vh.image.setImageResource(R.drawable.circle_indicator_active);
        }
    }

    @Override
    public int getItemCount() {
        return numberOfPages;
    }

    public int getActivePosition() {
        return activePosition;
    }

    public void setActivePosition(int activePosition) {
        final int oldPosition = this.activePosition;
        this.activePosition = activePosition;
        if (oldPosition >= 0 && oldPosition < getItemCount()) {
            notifyItemChanged(oldPosition);
        }
        if (activePosition >= 0 && activePosition < getItemCount()) {
            notifyItemChanged(activePosition);
        }
    }
}
