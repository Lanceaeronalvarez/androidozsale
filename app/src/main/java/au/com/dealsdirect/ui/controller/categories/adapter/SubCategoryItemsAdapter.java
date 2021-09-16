package au.com.dealsdirect.ui.controller.categories.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SubCategoryItemsAdapter extends RecyclerView.Adapter<SubCategoryItemsAdapter.SubCategoryItemViewHolder> {


    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private SubCategoryItemClickListener mCategoryAdapterClickListener;
    private int lastPosition = -1;
    private boolean mAnimateInsert = true;
    private Context mContext;
    private GetCategoryTreeResponse.LinkOptions mMainLinkOption;
    private SubCategoryItemViewHolder mLastSelectedViewHolder = null;
    private List<SearchChipModel> mChipFilter = new ArrayList<>();

    public SubCategoryItemsAdapter(
            Context context,
            List<GetCategoryTreeResponse> data,
            SubCategoryItemClickListener subCategoryItemClickListener,
            boolean animateInsert,
            GetCategoryTreeResponse.LinkOptions mainLinkOption) {

        mContext = context;
        mData = data;
        mCategoryAdapterClickListener = subCategoryItemClickListener;
        mAnimateInsert = animateInsert;
        mMainLinkOption = mainLinkOption;
    }

    @Override
    public SubCategoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        if (mContext.getResources().getBoolean(R.bool.should_use_old_category_layout)) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_subcategory_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_salesubcategory_item, parent, false);
        }

        return new SubCategoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubCategoryItemViewHolder holder, int position) {

        MainActivity activity = ((MainActivity) holder.itemView.getContext());
        setAnimation(holder.itemView, position);

        if (!mData.isEmpty()) {

            holder.itemView.setActivated(false);
            holder.subCategoryTitle.setText(mData.get(position).getName());


            holder.itemView.setOnClickListener(view -> {

                String categoryItemName = "";
                String categoryItemKey = "";
                String categoryItemId = "";

                if (mData.get(position).getLinkOptions() != null) {
                    categoryItemId = mData.get(position).getLinkOptions().getCategory().getId();
                    categoryItemName = mData.get(position).getLinkOptions().getCategory().getName();
                    categoryItemKey = mData.get(position).getLinkOptions().getCategory().getName();

                    if (mData.get(position).getLinkOptions().getFacets() != null) {
                        for (int i = 0; i < mData.get(position).getLinkOptions().getFacets().getNewArrivals().size(); i++) {
                            SearchChipModel searchChipModel = new SearchChipModel(BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME,
                                    mData.get(position).getLinkOptions().getFacets().getNewArrivals().get(i), null, i);
                            mChipFilter.add(searchChipModel);
                        }
                    }
                } else {

                    if (mMainLinkOption != null && mMainLinkOption.getCategory() != null) {
                        categoryItemId = mMainLinkOption.getCategory().getId();
                        categoryItemName = mMainLinkOption.getCategory().getName();
                        categoryItemKey = mMainLinkOption.getCategory().getName();

                        if (mMainLinkOption.getFacets() != null) {
                            for (int i = 0; i < mMainLinkOption.getFacets().getNewArrivals().size(); i++) {
                                SearchChipModel searchChipModel = new SearchChipModel(BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME,
                                        mMainLinkOption.getFacets().getNewArrivals().get(i), null, i);
                                mChipFilter.add(searchChipModel);
                            }
                        }

                    } else if (mMainLinkOption != null && mMainLinkOption.getFacets() != null) {

                        for (int i = 0; i < mMainLinkOption.getFacets().getNewArrivals().size(); i++) {
                            SearchChipModel searchChipModel = new SearchChipModel(BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME,
                                    mMainLinkOption.getFacets().getNewArrivals().get(i), null, i);
                            mChipFilter.add(searchChipModel);
                        }

                    } else {
                        categoryItemId = mData.get(position).getId();
                        categoryItemName = mData.get(position).getName();
                        categoryItemKey = mData.get(position).getKey();
                    }

                }

                if (mLastSelectedViewHolder != null) {
                    mLastSelectedViewHolder.subCategoryCheck.setVisibility(View.GONE);
                    mLastSelectedViewHolder.subCategoryTitle.setTypeface(null, Typeface.NORMAL);
                }

                View previousItemView = activity.getMainController().getSelectedSubCategoryItem();
                if (previousItemView != null) {
                    SubCategoryItemViewHolder subCategoryItemViewHolder = new SubCategoryItemViewHolder(previousItemView);
                    subCategoryItemViewHolder.subCategoryCheck.setVisibility(View.GONE);
                    subCategoryItemViewHolder.subCategoryTitle.setTypeface(null, Typeface.NORMAL);
                }

                activity.getMainController().setSelectedSubCategoryItem(holder.itemView);
                holder.subCategoryCheck.setVisibility(View.VISIBLE);
                holder.subCategoryTitle.setTypeface(null, Typeface.BOLD);
                mLastSelectedViewHolder = holder;

                mCategoryAdapterClickListener.onSubCategoryItemClicked(
                        categoryItemId,
                        categoryItemName,
                        categoryItemKey, mChipFilter);


                View lastItemView = activity.getMainController().getPreviousSubcategoryItem();
                if (lastItemView != null) {
                    SubCategoriesAdapter.SubCategoriesViewHolder subCategoryItemViewHolder = new SubCategoriesAdapter.SubCategoriesViewHolder(lastItemView);
                    subCategoryItemViewHolder.subCategoryCheckImageView.setVisibility(View.INVISIBLE);
                    activity.getMainController().setPreviousSubcategoryItem(null);
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void replaceData(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        mData = new ArrayList<>(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    static class SubCategoryItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_subcategory_item_name)
        TextView subCategoryTitle;

        @BindView(R.id.viewholder_subcategory_item_check)
        ImageView subCategoryCheck;

        boolean isSelected = false;

        public SubCategoryItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition && mAnimateInsert) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.slide_to_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
