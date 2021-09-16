package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.searchfilter.SubCategoryItemClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SubCategoryItemsAdapter extends RecyclerView.Adapter<SubCategoryItemsAdapter.SubCategoryItemViewHolder> {


    private GetCategoryTreeResponse mData;
    private SubCategoryItemClickListener mSubCategoryItemClickListener;
    private SubCategoryItemsAdapter mSubCategoryItemsAdapter;
    private Map<String, GetCategoryTreeResponse> mCategoryMap = new HashMap<>();
    private int position;

    public SubCategoryItemsAdapter(
            GetCategoryTreeResponse data,
            SubCategoryItemClickListener subCategoryItemClickListener,
            Map<String, GetCategoryTreeResponse> categoryMap) {

        mData = data;
        mSubCategoryItemClickListener = subCategoryItemClickListener;
        mCategoryMap = categoryMap;
    }

    @Override
    public SubCategoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter_subcategory_item, parent, false);
        SubCategoryItemViewHolder vh = new SubCategoryItemViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(SubCategoryItemViewHolder holder, int position) {
        List<GetCategoryTreeResponse> children = mData.getChildren();

        if (!children.isEmpty()) {

            if (!children.get(position).getName().isEmpty()) {
                holder.titleTextView.setText(children.get(position).getName());
                holder.subCategoryCheck.setVisibility(children.get(position).isSelected() ? View.VISIBLE : View.GONE);
            }

            List<GetCategoryTreeResponse> subCategoryItems = getSubCategoryItems(children.get(position).getKey()).getChildren();

            if (subCategoryItems != null && !subCategoryItems.isEmpty()) {

                mSubCategoryItemsAdapter = new SubCategoryItemsAdapter(children.get(position), mSubCategoryItemClickListener, mCategoryMap);
                holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                holder.recyclerView.setAdapter(mSubCategoryItemsAdapter);

                holder.itemView.setActivated(false);
                holder.recyclerView.setVisibility(View.VISIBLE);
            }

            holder.itemView.setOnClickListener(view -> {
                holder.titleTextView.setEnabled(true);
                this.position = position;
                mSubCategoryItemClickListener.onSubCategoryItemClicked(children.get(position));
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.getChildren().size() : 0;
    }

    public void replaceData(GetCategoryTreeResponse response) {
        mData = response;
        notifyDataSetChanged();
    }

    static class SubCategoryItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_subcategory_title)
        TextView titleTextView;

        @BindView(R.id.viewholder_subcategory_items_recyclerview)
        RecyclerView recyclerView;

        @BindView(R.id.viewholder_subcategory_check)
        ImageView subCategoryCheck;

        public SubCategoryItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private GetCategoryTreeResponse getSubCategoryItems(String categoryKey) {
        return mCategoryMap.get(categoryKey);
    }

    public int getPosition() {
        return position;
    }
}
