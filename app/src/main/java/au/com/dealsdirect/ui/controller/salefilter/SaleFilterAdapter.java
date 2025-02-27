package au.com.dealsdirect.ui.controller.salefilter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SaleFilterAdapter extends RecyclerView.Adapter<SaleFilterAdapter.SaleFilterViewHolder> {

    private final SaleFilterClickListener mSaleFilterAdapterClickListener;
    private final int mFilterLevel;
    private String mFilterType;
    private final boolean mShowSubCategories;
    private boolean mShowSort;
    private boolean isBrandFilter;
    private final boolean mShowColor;
    private final boolean mShowCategory;
    private Set<SearchChipModel> mSearchItemsList;
    private Set<SearchChipModel> mPreSelectedFilters;
    private ArrayList<String> mData = new ArrayList<>();
    private final List<SearchChipModel> mSelectedFilters;
    private final List<String> mSelectedCategoryKeys = new ArrayList<>();
    private final List<Integer> mCategoryLevel = new ArrayList<>();
    private final List<GetCategoryTreeResponse> mCategoryItems = new ArrayList<>();
    private final List<SortingResponse> mSortingList = new ArrayList<>();
    private final List<String> mfilterCountList = new ArrayList<>();
    private final List<String> mSubCategoriesToShow = new ArrayList<>();
    private final List<Pair<String, String>> mBrandsWithTotal = new ArrayList<>();

    public SaleFilterAdapter(SaleFilterClickListener clickListener,
                             int filterLevel,
                             List<String> tabTitles,
                             String filterType,
                             List<SearchChipModel> selectedFilters,
                             List<GetCategoryTreeResponse> mCategoryTree,
                             boolean showSubCategories,
                             Set<String> categoryKeys,
                             ArrayList<SortingResponse> sortingList,
                             Boolean showSort,
                             ArrayList<String> filterCountList,
                             Boolean showColor,
                             boolean showCategory,
                             ArrayList<Pair<String, String>> brandsWithTotal) {
        mSaleFilterAdapterClickListener = clickListener;
        mFilterLevel = filterLevel;
        mFilterType = filterType;
        mShowSubCategories = showSubCategories;
        mSelectedFilters = selectedFilters;
        mShowColor = showColor;
        mShowCategory = showCategory;
        if (showSubCategories) {
            mSelectedCategoryKeys.addAll(categoryKeys);
            transformData(mCategoryTree);
        } else if (showSort) {
            mShowSort = true;
            mSortingList.addAll(sortingList);
            for (SortingResponse sortData : sortingList) {
                mData.add(sortData.getTitle());
            }
        } else {
            mfilterCountList.addAll(filterCountList);
            mData = (ArrayList<String>) tabTitles;
        }
        if (mFilterType.equalsIgnoreCase(BundleKeys.BRANDS_FACETFILTER_NAME)) {
            isBrandFilter = true;
            mBrandsWithTotal.addAll(brandsWithTotal);
        }
    }

    @NonNull
    @Override
    public SaleFilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_sales_filter, parent, false);
        return new SaleFilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SaleFilterViewHolder holder, int position) {

        holder.categoryText.setText(mData.get(position));

        if (mFilterLevel == 1) {
            if (mData.get(position).contains("Sort") ||
                    mData.get(position).contains("Delivery") ||
                    mData.get(position).contains("New arrival")) {
                holder.saleFilterTile.setVisibility(View.GONE);
            }
            if (!mShowColor && mData.get(position).contains("Color")) {
                holder.saleFilterTile.setVisibility(View.GONE);
            }
            if (!mShowCategory && mData.get(position).equals("Category")) {
                holder.saleFilterTile.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(view -> {
                final int pos = holder.getBindingAdapterPosition();
                if (pos < 0 || pos >= mData.size()) {
                    return;
                }
                mSaleFilterAdapterClickListener.onCategoryClicked(mData.get(pos), pos);
            });
        } else {
            holder.filterCheckbox.setVisibility(View.VISIBLE);
            holder.filterChevron.setVisibility(View.GONE);
            holder.productCountText.setVisibility(View.VISIBLE);

            if (!mShowSort &&
                    !Objects.equals(mFilterType, BundleKeys.COLORS_FACETFILTER_NAME) &&
                    !mfilterCountList.isEmpty()) {
                final String productCountText = "(" + mfilterCountList.get(position) + ")";
                holder.productCountText.setText(productCountText);
            }

            //Brand filter improvements
            if (Objects.equals(mFilterType, BundleKeys.BRANDS_FACETFILTER_NAME)) {
                holder.categoryText.setText(mBrandsWithTotal.get(position).first);
                final String productCountText = "(" + mBrandsWithTotal.get(position).second + ")";
                holder.productCountText.setText(productCountText);
            }

            if (mShowSubCategories) {
                holder.filterCheckbox.setButtonDrawable(R.drawable.custom_checkbox_selector);
                String currentCategoryKey = mCategoryItems.get(position).getKey();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.saleFilterTile.getLayoutParams();
                if (Integer.parseInt(mfilterCountList.get(position)) <= 1) {
                    holder.productCountText.setVisibility(View.GONE);
                }

                if (!mCategoryItems.get(position).getChildren().isEmpty()) {
                    holder.filterChevron.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    holder.filterChevron.setVisibility(View.VISIBLE);
                    if (mSubCategoriesToShow.contains(mCategoryItems.get(position).getChildren().get(0).getKey())) {
                        holder.filterChevron.setImageResource(R.drawable.ic_arrow_down);
                        holder.filterChevron.setScaleY(-1f);
                    } else {
                        holder.filterChevron.setImageResource(R.drawable.ic_arrow_down);
                        holder.filterChevron.setScaleY(1f);
                    }
                }

                if (mCategoryLevel.get(position) == 0) {
                    holder.categoryText.setVisibility(View.GONE);
                    holder.parentCategoryName.setVisibility(View.VISIBLE);
                    holder.parentCategoryName.setText(mData.get(position));
                } else {
                    lp.setMargins((30 * mCategoryLevel.get(position)), 0, 0, 0);
                    holder.saleFilterTile.setLayoutParams(lp);

                    if (mSubCategoriesToShow.contains(mCategoryItems.get(position).getKey())) {
                        holder.saleFilterTile.setVisibility(View.VISIBLE);
                    } else {
                        holder.saleFilterTile.setVisibility(View.GONE);
                    }
                }

                holder.filterCheckbox.setChecked(mSelectedCategoryKeys.contains(currentCategoryKey));

                if (holder.filterCheckbox.isChecked()) {
                    // Add category if parent category is not selected
                    if (mCategoryLevel.get(position) == 1) {
                        if (!mSelectedCategoryKeys.contains(currentCategoryKey.split(">>>")[0])) {
                            mSaleFilterAdapterClickListener.onAddCategoryFilter(currentCategoryKey, mCategoryItems.get(position).getName());
                        }
                    }

                    if (mCategoryLevel.get(position) >= 2) {
                        String categoryNameKey = currentCategoryKey.substring(0, currentCategoryKey.lastIndexOf(">") - 2);
                        if (!mSelectedCategoryKeys.contains(categoryNameKey)) {
                            mSaleFilterAdapterClickListener.onAddCategoryFilter(currentCategoryKey, mCategoryItems.get(position).getName());
                        }
                    }
                }

                if (!mCategoryItems.get(position).getChildren().isEmpty()) {
                    // Check parent category if all child category is checked
                    int totalChildCount = 0;
                    int totalCheckedChildCount = 0;

                    totalChildCount += mCategoryItems.get(position).getChildren().size();

                    for (GetCategoryTreeResponse subCategories : mCategoryItems.get(position).getChildren()) {
                        if (mSelectedCategoryKeys.contains(subCategories.getKey())) {
                            totalCheckedChildCount += 1;
                        } else if (mSelectedCategoryKeys.contains(currentCategoryKey)) {
                            mSelectedCategoryKeys.add(subCategories.getKey());
                            mSaleFilterAdapterClickListener.onRemoveCategoryFilter(currentCategoryKey, subCategories.getName());
                            totalCheckedChildCount += 1;
                        }

                        if (!subCategories.getChildren().isEmpty()) {
                            totalChildCount += subCategories.getChildren().size();
                            for (GetCategoryTreeResponse subCategories2 : subCategories.getChildren()) {
                                if (mSelectedCategoryKeys.contains(subCategories2.getKey())) {
                                    totalCheckedChildCount += 1;
                                } else if (mSelectedCategoryKeys.contains(currentCategoryKey)) {
                                    mSelectedCategoryKeys.add(subCategories2.getKey());
                                    mSaleFilterAdapterClickListener.onRemoveCategoryFilter(currentCategoryKey, subCategories2.getName());
                                    totalCheckedChildCount += 1;
                                }

                                if (!subCategories2.getChildren().isEmpty()) {
                                    totalChildCount += subCategories2.getChildren().size();
                                    for (GetCategoryTreeResponse subCategories3 : subCategories2.getChildren()) {
                                        if (mSelectedCategoryKeys.contains(subCategories3.getKey())) {
                                            totalCheckedChildCount += 1;
                                        } else if (mSelectedCategoryKeys.contains(currentCategoryKey)) {
                                            mSelectedCategoryKeys.add(subCategories3.getKey());
                                            mSaleFilterAdapterClickListener.onRemoveCategoryFilter(currentCategoryKey, subCategories3.getName());
                                            totalCheckedChildCount += 1;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (totalCheckedChildCount == totalChildCount) {
                        holder.filterCheckbox.setChecked(true);
                        if (!mSelectedCategoryKeys.contains(currentCategoryKey)) {
                            mSelectedCategoryKeys.add(currentCategoryKey);
                            categoriesToShowOrHide(position, true);
                        }
                    } else {
                        if (totalCheckedChildCount != 0) {
                            holder.filterCheckbox.setButtonDrawable(R.drawable.ic_checkbox_indeterminate_small);
                        }
                        holder.filterCheckbox.setChecked(false);
                        mSelectedCategoryKeys.remove(currentCategoryKey);
                    }
                }

                holder.filterCheckbox.setOnClickListener(v -> {
                    final int pos = holder.getBindingAdapterPosition();
                    if (pos < 0 || pos >= mCategoryItems.size() || pos >= mCategoryLevel.size()) {
                        return;
                    }
                    final String catKey = mCategoryItems.get(pos).getKey();
                    if (holder.filterCheckbox.isChecked()) {
                        mSelectedCategoryKeys.add(mCategoryItems.get(pos).getKey());
                        mSelectedCategoryKeys.addAll(categoriesToShowOrHide(pos, true));
                    } else {
                        mSelectedCategoryKeys.remove(mCategoryItems.get(pos).getKey());
                        mSelectedCategoryKeys.removeAll(categoriesToShowOrHide(pos, false));
                        final String categoryNameKey = mCategoryItems.get(pos).getKey().replace(">>>" + mCategoryItems.get(pos).getName(), "");
                        mSelectedCategoryKeys.remove(categoryNameKey);
                        mSelectedCategoryKeys.remove(catKey.split(">>>")[0]);
                        if (mCategoryLevel.get(pos) == 2) {
                            mSaleFilterAdapterClickListener.onRemoveCategoryFilter(catKey, catKey.split(">>>")[1]);
                        }
                        if (mCategoryLevel.get(pos) == 3) {
                            final String categoryNameKey2 = categoryNameKey.replace(">>>" + catKey.split(">>>")[2], "");
                            mSelectedCategoryKeys.remove(categoryNameKey2);
                            mSaleFilterAdapterClickListener.onRemoveCategoryFilter(catKey, catKey.split(">>>")[2]);
                        }
                        mSaleFilterAdapterClickListener.onRemoveCategoryFilter(catKey.split(">>>")[0], catKey.split(">>>")[0]);
                    }
                    mSaleFilterAdapterClickListener.updateSeeAllProducts();
                    notifyDataSetChanged();
                });

                holder.saleFilterTile.setOnClickListener(view -> {
                    final int pos = holder.getBindingAdapterPosition();
                    if (pos < 0 || pos >= mCategoryItems.size()) {
                        return;
                    }
                    holder.filterChevron.setImageResource(R.drawable.ic_chevron_up);
                    for (GetCategoryTreeResponse child : mCategoryItems.get(pos).getChildren()) {
                        if (mSubCategoriesToShow.contains(child.getKey())) {
                            mSubCategoriesToShow.remove(child.getKey());
                        } else {
                            mSubCategoriesToShow.add(child.getKey());
                        }
                    }
                    notifyDataSetChanged();
                });
            } else if (mShowSort) {
                final String categoryText = mData.get(position).substring(0, 1).toUpperCase() + mData.get(position).substring(1).toLowerCase();
                holder.categoryText.setText(categoryText);
                for (SearchChipModel chip : mSelectedFilters) {
                    if (Objects.equals(chip.getFilterType(), mFilterType)) {
                        if (chip.getChipTitle().contains(mData.get(position))) {
                            holder.filterCheckbox.setChecked(true);
                        }
                    }
                }
                holder.filterCheckbox.setOnClickListener(view -> {
                    mSaleFilterAdapterClickListener.removeAllSort();
                    if (!holder.filterCheckbox.isChecked()) {
                        mSaleFilterAdapterClickListener.removeAllSort();
                    } else {
                        addChip(holder.getBindingAdapterPosition());
                    }
                    notifyDataSetChanged();
                });
            } else {

                for (SearchChipModel chip : mSelectedFilters) {
                    if (chip.getFilterType().equals(mFilterType)) {
                        if (chip.getChipTitle().equals(mData.get(position)) && !isBrandFilter) {
                            holder.filterCheckbox.setChecked(true);
                            if (mShowSort) {
                                holder.filterCheck.setVisibility(View.VISIBLE);
                            }
                        } else if (isBrandFilter && chip.getChipTitle().equals(mBrandsWithTotal.get(position).first)) {
                            holder.filterCheckbox.setChecked(true);
                        }
                    }
                }

                holder.filterCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    final int pos = holder.getBindingAdapterPosition();
                    if (isChecked) {
                        addChip(pos);
                    } else {
                        removeChip(pos);
                    }
                });
            }
        }

    }

    private List<String> categoriesToShowOrHide(int position, boolean isAdd) {
        if (position < 0 || position >= mCategoryItems.size()) {
            return Collections.emptyList();
        }
        List<String> mNewSelectedCategoryKeys = new ArrayList<>();
        if (isAdd) {
            mSaleFilterAdapterClickListener.onAddCategoryFilter(mCategoryItems.get(position).getKey(), mCategoryItems.get(position).getName());
        } else {
            mSaleFilterAdapterClickListener.onRemoveCategoryFilter(mCategoryItems.get(position).getKey(), mCategoryItems.get(position).getName());
        }
        if (!mCategoryItems.get(position).getChildren().isEmpty()) {
            for (GetCategoryTreeResponse subCategories : mCategoryItems.get(position).getChildren()) {
                mSaleFilterAdapterClickListener.onRemoveCategoryFilter(subCategories.getKey(), subCategories.getName());
                if (!mSelectedCategoryKeys.contains(subCategories.getKey()) || !isAdd) {
                    mNewSelectedCategoryKeys.add(subCategories.getKey());
                }

                if (!subCategories.getChildren().isEmpty()) {
                    for (GetCategoryTreeResponse subCategories2 : subCategories.getChildren()) {
                        mSaleFilterAdapterClickListener.onRemoveCategoryFilter(subCategories2.getKey(), subCategories2.getName());
                        if (!mSelectedCategoryKeys.contains(subCategories2.getKey()) || !isAdd) {
                            mNewSelectedCategoryKeys.add(subCategories2.getKey());
                        }

                        if (!subCategories2.getChildren().isEmpty()) {
                            for (GetCategoryTreeResponse subCategories3 : subCategories2.getChildren()) {
                                mSaleFilterAdapterClickListener.onRemoveCategoryFilter(subCategories3.getKey(), subCategories3.getName());
                                if (!mSelectedCategoryKeys.contains(subCategories3.getKey()) || !isAdd) {
                                    mNewSelectedCategoryKeys.add(subCategories3.getKey());
                                }
                            }
                        }
                    }
                }
            }
        }
        return mNewSelectedCategoryKeys;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    private void addChip(int position) {
        if (position < 0) {
            return;
        }
        String chipTitle;
        if (isBrandFilter) {
            if (position >= mBrandsWithTotal.size()) {
                return;
            }
            chipTitle = mBrandsWithTotal.get(position).first;
        } else {
            if (position >= mData.size()) {
                return;
            }
            chipTitle = mData.get(position);
        }
        String key = mShowSort ? (position < mSortingList.size() ? mSortingList.get(position).getKey() : "") : "";
        SearchChipModel newChip = new SearchChipModel(mFilterType, chipTitle, key);
        mSearchItemsList.add(newChip);
        mSaleFilterAdapterClickListener.onAddFilter(mFilterType, chipTitle, newChip);
    }

    private void removeChip(int position) {
        if (position < 0) {
            return;
        }
        String chipTitle;
        if (isBrandFilter) {
            if (position >= mBrandsWithTotal.size()) {
                return;
            }
            chipTitle = mBrandsWithTotal.get(position).first;
        } else {
            if (position >= mData.size()) {
                return;
            }
            chipTitle = mData.get(position);
        }

        SearchChipModel chipToRemove = null;

        if (mPreSelectedFilters != null && !mPreSelectedFilters.isEmpty()) {
            final Set<SearchChipModel> newSet = new HashSet<>(mPreSelectedFilters);
            for (SearchChipModel chip : mPreSelectedFilters) {
                if (chip.getChipTitle().equals(chipTitle)) {
                    newSet.remove(chip);
                }
            }
            mPreSelectedFilters = newSet;
        }

        if (mSelectedFilters != null && !mSelectedFilters.isEmpty()) {
            for (SearchChipModel chip : mSelectedFilters) {
                if (chip.getChipTitle().equals(chipTitle) &&
                        chip.getFilterType().equals(mFilterType)) {
                    chipToRemove = chip;
                }
            }
        }

        if (chipToRemove != null) {
            mSearchItemsList.remove(chipToRemove);
            mSaleFilterAdapterClickListener.onRemoveFilter(mFilterType, isBrandFilter ? mBrandsWithTotal.get(position).first : mData.get(position), chipToRemove);
        }
    }

    public void setSearchItemsList(Set<SearchChipModel> list) {
        mSearchItemsList = list;
    }

    public static class TransformedNode {
        private final GetCategoryTreeResponse mObject;
        private final int mLevel;

        TransformedNode(GetCategoryTreeResponse object, int level) {
            mObject = object;
            mLevel = level;
        }

        int getLevel() {
            return mLevel;
        }

        GetCategoryTreeResponse getObject() {
            return mObject;
        }
    }

    public List<SaleFilterAdapter.TransformedNode> transformData(List<GetCategoryTreeResponse> data) {
        final ArrayList<SaleFilterAdapter.TransformedNode> list = new ArrayList<>();
        for (GetCategoryTreeResponse node : data) {
            node.traverseTree(new GetCategoryTreeResponse.TreeTraversalBlock() {
                @Override
                public boolean execute(GetCategoryTreeResponse parent, Object option) {
                    int level = (Integer) option;
                    list.add(new TransformedNode(parent, level));

                    mCategoryItems.add(parent);
                    mCategoryLevel.add(level);
                    mfilterCountList.add(String.valueOf(parent.getCount()));
                    mData.add(parent.getName());
                    return true;
                }

                @Override
                public Object transformOption(GetCategoryTreeResponse parent, Object option) {
                    return (Integer) option + 1;
                }
            }, 0);
        }

        return list;
    }

    public static class SaleFilterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_category_name)
        public TextView categoryText;

        @BindView(R.id.filter_product_count)
        public TextView productCountText;

        @BindView(R.id.row_filter_checkbox)
        public MaterialCheckBox filterCheckbox;

        @BindView(R.id.row_filter_chevron)
        public ImageView filterChevron;

        @BindView(R.id.row_filter_check)
        public ImageView filterCheck;

        @BindView(R.id.sale_filter_tile)
        public LinearLayout saleFilterTile;

        @BindView(R.id.parent_category_name)
        public TextView parentCategoryName;

        public SaleFilterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
