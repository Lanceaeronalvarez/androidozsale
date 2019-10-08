package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.content.Context;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpPresenter;
import au.com.dealsdirect.ui.controller.searchfilter.SubCategoryClickListener;
import au.com.dealsdirect.ui.controller.searchfilter.SubCategoryItemClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.SubCategoriesViewHolder> {

    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private SearchFilterMvpPresenter mSearchFilterPresenter;
    private SubCategoriesAdapter mSubCategoryItemsAdapter;
    private Map<String, GetCategoryTreeResponse> mCategoryMap;
    static GetCategoryTreeResponse mPreviousCategory;
    private String mChosenCategoryKey;

    public SubCategoriesAdapter(
            String chosenCategorykey,
            List<GetCategoryTreeResponse> data,
            SearchFilterMvpPresenter searchFilterMvpPresenter,
            Map<String, GetCategoryTreeResponse> categoryMap) {

        mChosenCategoryKey = chosenCategorykey;
        mData = data;
        mCategoryMap = categoryMap;
        mSearchFilterPresenter = searchFilterMvpPresenter;
    }

    @Override
    public SubCategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_subcategory, parent, false);
        return new SubCategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubCategoriesViewHolder holder, int position) {

        GetCategoryTreeResponse categoryItem = mCategoryMap.get(mData.get(position).getKey());

        if (!mData.get(position).getName().isEmpty()) {
            holder.subCategoryTitle.setText(mData.get(position).getName());

//            holder.subCategoryCheck.setVisibility( ? View.VISIBLE : View.GONE);
            if (categoryItem.isSelected()) {
                holder.subCategoryCheck.setVisibility(View.VISIBLE);
                holder.itemView.setSelected(true);
            } else {
                holder.subCategoryCheck.setVisibility(View.GONE);
                holder.itemView.setSelected(false);
            }

//            List<GetCategoryTreeResponse> subCategoryItems = mData.get(position).getChildren();
            List<GetCategoryTreeResponse> subCategoryItems = mCategoryMap.get(mData.get(position).getKey()).getChildren();

            if (subCategoryItems != null && !subCategoryItems.isEmpty()) {
                mSubCategoryItemsAdapter = new SubCategoriesAdapter(mChosenCategoryKey, subCategoryItems, mSearchFilterPresenter, mCategoryMap);
                holder.subCategoryItemsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                holder.subCategoryItemsRecyclerView.setAdapter(mSubCategoryItemsAdapter);

                holder.itemView.setActivated(false);
                holder.subCategoryItemsRecyclerView.setVisibility(View.VISIBLE);
            }

            holder.itemView.setOnClickListener(view -> {
//                if (mPreviousCategory != null && mPreviousCategory != categoryItem) {
//                    mPreviousCategory.setSelected(false);
//                }
                categoryItem.setSelected(!categoryItem.isSelected());

                mSearchFilterPresenter.selectCategory(categoryItem);
//                mPreviousCategory = categoryItem;
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mData != null)
            return mData.size();
        return 0;
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
}
