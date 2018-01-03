package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
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


    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private SubCategoryItemClickListener mCategoryAdapterClickListener;
    private SubCategoryItemsAdapter mSubCategoryItemsAdapter;
    private int lastPosition = -1;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private String mChosenCategoryKey;

    public SubCategoryItemsAdapter(
            String chosenCategoryKey,
            List<GetCategoryTreeResponse> data,
            SubCategoryItemClickListener subCategoryItemClickListener,
            Map<String, List<GetCategoryTreeResponse>> categoryMap) {

        mChosenCategoryKey = chosenCategoryKey;
        mData = data;
        mCategoryAdapterClickListener = subCategoryItemClickListener;
        mCategoryMap = categoryMap;
    }

    @Override
    public SubCategoryItemsAdapter.SubCategoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter_subcategory_item, parent, false);
        SubCategoryItemsAdapter.SubCategoryItemViewHolder vh
                = new SubCategoryItemsAdapter.SubCategoryItemViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(SubCategoryItemsAdapter.SubCategoryItemViewHolder holder, int position) {
//        setAnimation(holder.itemView, position);

        Context context = holder.itemView.getContext();

        if (!mData.isEmpty()) {

            if (!mData.get(position).getName().isEmpty()) {

                holder.titleTextView.setText(mData.get(position).getName());

                if (mData.get(position).getKey().equals(mChosenCategoryKey)) {
                    holder.titleTextView.setTextColor(context.getResources().getColor(R.color.category_text_active));
                }
            }

            List<GetCategoryTreeResponse> subCategoryItems = getSubCategoryItems(mData.get(position).getKey());

            if (subCategoryItems != null && !subCategoryItems.isEmpty()) {
                mSubCategoryItemsAdapter = new SubCategoryItemsAdapter(mChosenCategoryKey, subCategoryItems, mCategoryAdapterClickListener, mCategoryMap);
                holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                holder.recyclerView.setAdapter(mSubCategoryItemsAdapter);

                holder.itemView.setActivated(false);
                holder.recyclerView.setVisibility(View.VISIBLE);
            }

            holder.container.setOnClickListener(view -> {
                holder.titleTextView.setEnabled(true);
                mCategoryAdapterClickListener.onSubCategoryItemClicked(
                        mData.get(position).getId(),
                        mData.get(position).getName(),
                        mData.get(position).getKey());
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null)
            return mData.size();
        return 0;
    }

    @Override
    public void onViewDetachedFromWindow(SubCategoryItemsAdapter.SubCategoryItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
//        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_to_top);
//        holder.itemView.startAnimation(animation);
    }

    public void replaceData(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        mData = new ArrayList<>(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    static class SubCategoryItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_subcategory_container)
        RelativeLayout container;

        @BindView(R.id.viewholder_subcategory_title)
        TextView titleTextView;

        @BindView(R.id.viewholder_subcategory_items_recyclerview)
        RecyclerView recyclerView;

        public SubCategoryItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.slide_to_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private List<GetCategoryTreeResponse> getSubCategoryItems(String categoryKey) {
        return mCategoryMap.get(categoryKey);
    }

    public void addItem(GetCategoryTreeResponse getCategoryTreeResponse) {
        mData.add(getCategoryTreeResponse);
        notifyDataSetChanged();
    }

}
