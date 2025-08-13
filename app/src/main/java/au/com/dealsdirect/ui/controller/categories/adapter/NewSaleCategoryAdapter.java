package au.com.dealsdirect.ui.controller.categories.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.listener.NewSaleCategoryClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewSaleCategoryAdapter extends RecyclerView.Adapter<NewSaleCategoryAdapter.NewSaleCategoryViewHolder> {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_URL = 1;

    private List<GetCategoryTreeResponse> mData;

    private String clearanceUrl;
    private final NewSaleCategoryClickListener mCategoryAdapterClickListener;

    public NewSaleCategoryAdapter(List<GetCategoryTreeResponse> data,
                                  NewSaleCategoryClickListener categoryClickListener) {
        if (data != null) {
            mData = new ArrayList<>(data);
        } else {
            mData = Collections.emptyList();
        }
        mCategoryAdapterClickListener = categoryClickListener;
    }

    @NonNull
    @Override
    public NewSaleCategoryAdapter.NewSaleCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_newsalecategory, parent, false);
        return new NewSaleCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewSaleCategoryAdapter.NewSaleCategoryViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_NORMAL:
                final GetCategoryTreeResponse item = mData.get(position);
                if (item.getName().equals("All") || item.getName().equalsIgnoreCase("Gift Cards")) {
                    holder.categoryContainer.setVisibility(View.GONE);
                }

                final String subCategoryTitle = item.getName();
                holder.title.setText(subCategoryTitle);

                final String textColor = item.getTextColor();
                if (textColor != null && !textColor.isEmpty()) {
                    holder.title.setTextColor(Color.parseColor(textColor));
                } else {
                    holder.resetTitleTextColor();
                }

                holder.imageButton.setVisibility(
                        item.getChildren() != null && !item.getChildren().isEmpty() ?
                                View.VISIBLE : View.GONE);

                holder.itemView.setOnClickListener(view -> {
                    final int pos = holder.getBindingAdapterPosition();
                    mCategoryAdapterClickListener.onCategoryClicked(pos, mData.get(pos), mData.get(pos).getName());
                });

                holder.imageButton.setOnClickListener(view -> {
                    final int pos = holder.getBindingAdapterPosition();
                    mCategoryAdapterClickListener.onCategoryClicked(pos, mData.get(pos), mData.get(pos).getName());
                });
                break;
            case VIEW_TYPE_URL:
                holder.title.setText("Clearance");
                holder.title.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
                holder.imageButton.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(v -> mCategoryAdapterClickListener.onURLClicked(clearanceUrl));
                holder.imageButton.setOnClickListener(v -> mCategoryAdapterClickListener.onURLClicked(clearanceUrl));
                break;
            default:
                break;

        }
    }

    public GetCategoryTreeResponse getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return (mData != null ? mData.size() : 0) +
                (clearanceUrl != null ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (clearanceUrl != null && position >= mData.size()) {
            return VIEW_TYPE_URL;
        }
        return VIEW_TYPE_NORMAL;
    }

    public void replaceData(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        mData = new ArrayList<>(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    public String getClearanceUrl() {
        return clearanceUrl;
    }

    public void setClearanceUrl(String clearanceUrl) {
        this.clearanceUrl = clearanceUrl;
    }

    public static class NewSaleCategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_salecategory_container)
        RelativeLayout categoryContainer;

        @BindView(R.id.viewholder_salecategory_title)
        TextView title;

        @BindView(R.id.viewholder_salecategory_image_button)
        ImageButton imageButton;

        private final int titleDefaultTextColor;

        public NewSaleCategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            titleDefaultTextColor = title.getTextColors().getDefaultColor();
        }

        public void resetTitleTextColor() {
            title.setTextColor(titleDefaultTextColor);
        }
    }

}
