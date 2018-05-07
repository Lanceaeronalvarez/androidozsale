package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.searchfilter.SubCategoryClickListener;
import au.com.dealsdirect.ui.controller.searchfilter.SubCategoryItemClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.SubCategoriesViewHolder> {

    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private SubCategoryClickListener mSubCategoryClickListener;
    private SubCategoryItemClickListener mSubCategoryItemClickListener;
    private SubCategoryItemsAdapter mSubCategoryItemsAdapter;
    private Map<String, GetCategoryTreeResponse> mCategoryMap = new HashMap<>();
    private int selectedPosition;

    public SubCategoriesAdapter(
            List<GetCategoryTreeResponse> data,
            SubCategoryClickListener subCategoryClickListener,
            SubCategoryItemClickListener subCategoryItemClickListener,
            Map<String, GetCategoryTreeResponse> categoryMap) {

        mData = data;
        mSubCategoryClickListener = subCategoryClickListener;
        mSubCategoryItemClickListener = subCategoryItemClickListener;
        mCategoryMap = categoryMap;
    }

    @Override
    public SubCategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_subcategory, parent, false);
        return new SubCategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubCategoriesViewHolder holder, int position) {

        if (!mData.isEmpty()) {
            if (!mData.get(position).getName().equals("empty")) {
                if (!mData.get(position).getName().isEmpty()) {

                    holder.subCategoryTitle.setText(mData.get(position).getName());

                    if (mData.get(position).getSelected()) {
                        holder.subCategoryCheck.setVisibility(View.VISIBLE);
                    } else {
                        holder.subCategoryCheck.setVisibility(View.GONE);
                    }
                }

                List<GetCategoryTreeResponse> subCategoryItems = getSubCategoryItems(mData.get(position).getKey()).getChildren();

                mSubCategoryItemsAdapter = new SubCategoryItemsAdapter(mData.get(position), mSubCategoryItemClickListener, mCategoryMap);
                holder.subCategoryItemsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                holder.subCategoryItemsRecyclerView.setAdapter(mSubCategoryItemsAdapter);

                if (subCategoryItems != null && !subCategoryItems.isEmpty()) {
                    holder.itemView.setActivated(false);
                    holder.subCategoryItemsRecyclerView.setVisibility(View.VISIBLE);
                }
                holder.itemView.setOnClickListener(view -> {
                    selectedPosition = position;
                    mData.get(position).setSelected(!mData.get(position).getSelected());
                    mSubCategoryClickListener.onSubCategoryClicked(mData, mData.get(position).getKey());
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void replaceData(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        mData = new ArrayList<>(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    static class SubCategoriesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_subcategory_title)
        TextView subCategoryTitle;

        @BindView(R.id.viewholder_subcategory_items_recyclerview)
        RecyclerView subCategoryItemsRecyclerView;

        @BindView(R.id.viewholder_subcategory_check)
        ImageView subCategoryCheck;

        SubCategoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private GetCategoryTreeResponse getSubCategoryItems(String categoryKey) {
        return mCategoryMap.get(categoryKey);
    }
}
