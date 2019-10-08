package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpPresenter;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpPresenter;
import au.com.dealsdirect.ui.controller.searchfilter.SubCategoryItemClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SubCategoryItemsAdapter extends RecyclerView.Adapter<SubCategoryItemsAdapter.SubCategoryItemViewHolder> {


    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private SubCategoryItemsAdapter mSubCategoryItemsAdapter;
    private SearchFilterMvpPresenter mSearchFilterPresenter;
    private int lastPosition = -1;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private String mChosenCategoryKey;

    public SubCategoryItemsAdapter(
            String chosenCategoryKey,
            List<GetCategoryTreeResponse> data,
            SearchFilterMvpPresenter searchFilterMvpPresenter,
            Map<String, List<GetCategoryTreeResponse>> categoryMap) {

        mChosenCategoryKey = chosenCategoryKey;
        mData = data;
        mSearchFilterPresenter = searchFilterMvpPresenter;
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
        if (!mData.isEmpty()) {

            if (!mData.get(position).getName().isEmpty()) {

                holder.titleTextView.setText(mData.get(position).getName());

                if (mData.get(position).getKey().equals(mChosenCategoryKey)) {
                    holder.subCategoryCheck.setVisibility(View.VISIBLE);
                    holder.itemView.setSelected(true);
                } else {
                    holder.subCategoryCheck.setVisibility(View.GONE);
                    holder.itemView.setSelected(false);
                }
            }

            List<GetCategoryTreeResponse> subCategoryItems = mCategoryMap.get(mData.get(position).getKey());

            if (subCategoryItems != null && !subCategoryItems.isEmpty()) {
                mSubCategoryItemsAdapter = new SubCategoryItemsAdapter(mChosenCategoryKey, subCategoryItems, mSearchFilterPresenter, mCategoryMap);
                holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                holder.recyclerView.setAdapter(mSubCategoryItemsAdapter);

                holder.itemView.setActivated(false);
                holder.recyclerView.setVisibility(View.VISIBLE);
            }

            holder.itemView.setOnClickListener(view -> {
                holder.titleTextView.setEnabled(true);
                mSearchFilterPresenter.selectCategory(mData.get(position));
            });
        }
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

    public void addItem(GetCategoryTreeResponse getCategoryTreeResponse) {
        mData.add(getCategoryTreeResponse);
        notifyDataSetChanged();
    }

}
