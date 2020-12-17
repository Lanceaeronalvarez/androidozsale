package au.com.dealsdirect.ui.controller.saleitems;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandBubblesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String mCurrentSearchTerm;
    private List<String> mBrandNames;
    private BrandBubbleOnSelectListener mListener;

    public BrandBubblesAdapter(String currentSearchTerm, List<String> brandNames, BrandBubbleOnSelectListener listener) {
        mCurrentSearchTerm = currentSearchTerm;
        mBrandNames = brandNames;
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_brand_bubble, parent, false);
        return new BrandBubbleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final String brandName = mBrandNames.get(position);
        ((BrandBubbleViewHolder) holder).title.setText(brandName);
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onSelect(brandName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBrandNames.size();
    }

    public interface BrandBubbleOnSelectListener {
        void onSelect(String brandName);
    }

    public static class BrandBubbleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.brand_bubble_title)
        TextView title;

        BrandBubbleViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public String getCurrentSearchTerm() {
        return mCurrentSearchTerm == null ? "" : mCurrentSearchTerm;
    }
}
