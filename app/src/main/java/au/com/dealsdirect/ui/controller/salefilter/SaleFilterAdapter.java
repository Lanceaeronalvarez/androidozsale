package au.com.dealsdirect.ui.controller.salefilter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SaleFilterAdapter extends RecyclerView.Adapter<SaleFilterAdapter.SaleFilterViewHolder> {

    private SaleFilterClickListener mSaleFilterAdapterClickListener;
    private Context mContext;
    private int mFilterLevel;
    private String mFilterType = "";
    private String mCategoryKey = "";
    private String mSourceType = "";
    private boolean mShowSubCategories;
    private boolean mShowSort;
    private boolean mShowColor;
    private boolean mShowCategory;
    private Set<SearchChipModel> mSearchItemsList;
    private Set<SearchChipModel> mPreSelectedFilters;
    private ArrayList<String> mData = new ArrayList<>();
    private List<SearchChipModel> mSelectedFilters = new ArrayList<>();
    private List<String> mSelectedCategoryKeys = new ArrayList<>();
    private List<Integer> mCategoryLevel = new ArrayList<>();
    private List<GetCategoryTreeResponse> mCategoryItems = new ArrayList<>();
    private ArrayList<SortingResponse> mSortingList = new ArrayList<>();
    private ArrayList<String> mfilterCountList = new ArrayList<>();

    public SaleFilterAdapter(Context context, SaleFilterClickListener clickListener, int filterLevel, List<?> data, List<String> tabTitles, String filterType, List<SearchChipModel> selectedFilters, List<GetCategoryTreeResponse> mCategoryTree, boolean showSubCategories, Set<String> categoryKeys, ArrayList<SortingResponse> sortingList, Boolean showSort, ArrayList<String> filterCountList, Boolean showColor, String categoryKey, String sourceType, boolean showCategory) {
        mContext = context;
        mSaleFilterAdapterClickListener = clickListener;
        mFilterLevel = filterLevel;
        mFilterType = filterType;
        mShowSubCategories = showSubCategories;
        mSelectedFilters = selectedFilters;
        mShowColor = showColor;
        mCategoryKey = categoryKey;
        mSourceType = sourceType;
        mShowCategory = showCategory;
        if(showSubCategories){
            mSelectedCategoryKeys.addAll(categoryKeys);
            transformData(mCategoryTree);
        }else if(showSort){
            mShowSort = true;
            mSortingList.addAll(sortingList);
            for(SortingResponse sortData: sortingList){
                mData.add(sortData.getTitle());
            }
        }else{
            mfilterCountList.addAll(filterCountList);
            mData = (ArrayList<String>) tabTitles;
        }
    }

    @Override
    public SaleFilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_sales_filter, parent, false);
        SaleFilterViewHolder vh = new SaleFilterViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(SaleFilterViewHolder holder, int position) {
        SaleFilterAdapter.SaleFilterViewHolder vh = (SaleFilterAdapter.SaleFilterViewHolder) holder;

        holder.categoryText.setText(mData.get(position));

        if(mFilterLevel == 1){
            if(mData.get(position).contains("Sort") || mData.get(position).contains("Delivery") || mData.get(position).contains("New arrival") ){
                holder.saleFilterTile.setVisibility(View.GONE);
            }
            if(mShowColor == false && mData.get(position).contains("Color")){
                holder.saleFilterTile.setVisibility(View.GONE);
            }
            if(mShowCategory == false && mData.get(position).equals("Category")){
                holder.saleFilterTile.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(view -> {
                mSaleFilterAdapterClickListener.onCategoryClicked(mData.get(position), position);
            });
        }else{
            holder.filterCheckbox.setVisibility(View.VISIBLE);
            holder.filterChevron.setVisibility(View.GONE);
            holder.productCountText.setVisibility(View.VISIBLE);

            if(!mShowSort && mFilterType != BundleKeys.COLORS_FACETFILTER_NAME && !mfilterCountList.isEmpty()){
                holder.productCountText.setText("(" + mfilterCountList.get(position) + ")");
            }

            if(mShowSubCategories){
                String currentCategoryKey = mCategoryItems.get(position).getKey();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.saleFilterTile.getLayoutParams();
                if(Integer.valueOf(mfilterCountList.get(position)) <= 1){
                    holder.productCountText.setVisibility(View.GONE);
                }

                if(mCategoryLevel.get(position) == 0){
                    holder.categoryText.setVisibility(View.GONE);
                    holder.parentCategoryName.setVisibility(View.VISIBLE);
                    holder.parentCategoryName.setText(mData.get(position));
                    if(position != 0){
                        lp.setMargins(0, 150, 0, 0);
                        holder.saleFilterTile.setLayoutParams(lp);
                    }
                }

                if(mSelectedCategoryKeys.contains(currentCategoryKey)) {
                    holder.filterCheckbox.setChecked(true);
                }else{
                    holder.filterCheckbox.setChecked(false);
                }

                if(mCategoryLevel.get(position) == 0 &&
                        !mCategoryItems.get(position).getChildren().isEmpty()){
                    // Check parent category if all child category is checked
                    int totalChildCount = 0;
                    int totalCheckedChildCount = 0;
                    for(int i = 0; i < mCategoryItems.size(); i++) {
                        String iCategory = mCategoryItems.get(i).getKey();
                        if (iCategory.split(">")[0].equals(currentCategoryKey.split(">")[0])) {
                            if(mCategoryLevel.get(i) != 0){
                                totalChildCount +=1;
                                if(mSelectedCategoryKeys.contains(iCategory)){
                                    totalCheckedChildCount +=1;
                                }
                            }
                        }
                    }
                    if(totalCheckedChildCount == totalChildCount){
                        holder.filterCheckbox.setChecked(true);
                    }
                }

                holder.filterCheckbox.setOnClickListener( v -> {
                    if(holder.filterCheckbox.isChecked()){
                        if(mCategoryLevel.get(position) == 0){
                            if(mCategoryItems.get(position).getChildren().isEmpty()){
                                mSaleFilterAdapterClickListener.onAddRemoveFilter(true, currentCategoryKey, mData.get(position).trim(), mShowSubCategories, null, true);
                                mSelectedCategoryKeys.add(currentCategoryKey);
                            }else{
                                // if parent category is checked, all child should be checked too.
                                List<String> mNewSelectedCategoryKeys = new ArrayList<>();
                                for(int i = 0; i < mCategoryItems.size(); i++) {
                                    String iCategory = mCategoryItems.get(i).getKey();
                                    if (iCategory.split(">")[0].equals(currentCategoryKey)) {
                                        if(!mNewSelectedCategoryKeys.contains(iCategory) && !mSelectedCategoryKeys.contains(iCategory)){
                                            if(mCategoryLevel.get(i) != 0){
                                                mSaleFilterAdapterClickListener.onAddRemoveFilter(true, iCategory, mData.get(i).trim(), mShowSubCategories, null, i+1 == mCategoryItems.size());
                                                mNewSelectedCategoryKeys.add(iCategory);
                                            }
                                        }
                                    }
                                }
                                mSelectedCategoryKeys.addAll(mNewSelectedCategoryKeys);
                                mSaleFilterAdapterClickListener.updateSeeAllProducts();
                            }
                        }else{
                            if(!mSelectedCategoryKeys.contains(currentCategoryKey)){
                                mSaleFilterAdapterClickListener.onAddRemoveFilter(true, currentCategoryKey, mData.get(position).trim(), mShowSubCategories, null, true);
                                mSelectedCategoryKeys.add(currentCategoryKey);
                            }
                        }
                        notifyDataSetChanged();
                    }else{
                        List<String> mSelectedToBeRemovedCategoryKeys = new ArrayList<>();
                        if(mCategoryLevel.get(position) == 0){
                            // if parent category is unchecked, all child should be unchecked too.
                            for(int i = 0; i < mCategoryItems.size(); i++) {
                                String iCategory = mCategoryItems.get(i).getKey();
                                if (iCategory.split(">")[0].equals(currentCategoryKey) ) {
                                    mSaleFilterAdapterClickListener.onAddRemoveFilter(false, iCategory, mData.get(i).trim(), mShowSubCategories, null, i+1 == mCategoryItems.size());
                                    mSelectedToBeRemovedCategoryKeys.add(iCategory);
                                }
                            }
                            mSaleFilterAdapterClickListener.updateSeeAllProducts();
                        }else{
                            mSelectedToBeRemovedCategoryKeys.add(currentCategoryKey);
                            mSaleFilterAdapterClickListener.onAddRemoveFilter(false, currentCategoryKey, mData.get(position), mShowSubCategories, null, true);
                        }
                        mSelectedCategoryKeys.removeAll(mSelectedToBeRemovedCategoryKeys);
                        notifyDataSetChanged();
                    }
                });
            }else if(mShowSort){
                holder.categoryText.setText(mData.get(position).substring(0, 1).toUpperCase() + mData.get(position).substring(1).toLowerCase());
                for(SearchChipModel chip : mSelectedFilters){
                    if(chip.getFilterType() == mFilterType){
                        if(chip.getChipTitle().contains(mData.get(position))){
                            holder.filterCheckbox.setChecked(true);
                        }
                    }
                }
                holder.filterCheckbox.setOnClickListener(view -> {
                    mSaleFilterAdapterClickListener.removeAllSort();
                    if(!holder.filterCheckbox.isChecked()){
                        mSaleFilterAdapterClickListener.removeAllSort();
                    }else{
                        addChip(position);
                    }
                    notifyDataSetChanged();
                });
            }else{
                for(SearchChipModel chip : mSelectedFilters){
                    if(chip.getFilterType().equals(mFilterType)){
                        if(chip.getChipTitle().equals(mData.get(position))){
                            holder.filterCheckbox.setChecked(true);
                            if(mShowSort){
                                holder.filterCheck.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                holder.filterCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            addChip(position);
                        }else{
                            removeChip(position);
                        }
                    }
                });
            }
        }

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
        SearchChipModel newChip = new SearchChipModel(mFilterType, mData.get(position), mShowSort ? mSortingList.get(position).getKey() :"");
        mSearchItemsList.add(newChip);
        mSaleFilterAdapterClickListener.onAddRemoveFilter(true, mFilterType, mData.get(position), mShowSubCategories, newChip, true);
    }

    private void removeChip(int position) {
        SearchChipModel chipToRemove = null;

        if (mPreSelectedFilters != null && mPreSelectedFilters.size() != 0) {
            final Set<SearchChipModel> newSet = new HashSet<>(mPreSelectedFilters);
            for (Iterator<SearchChipModel> it = mPreSelectedFilters.iterator(); it.hasNext(); ) {
                SearchChipModel chip = it.next();
                if (chip.getChipTitle().equals(mData.get(position))) {
                    newSet.remove(chip);
                }
            }
            mPreSelectedFilters = newSet;
        }

        if (mSelectedFilters != null && mSelectedFilters.size() != 0) {
            final Set<SearchChipModel> newSet = new HashSet<>(mSelectedFilters);
            for (Iterator<SearchChipModel> it = mSelectedFilters.iterator(); it.hasNext(); ) {
                SearchChipModel chip = it.next();
                if (chip.getChipTitle().equals(mData.get(position)) && chip.getFilterType().equals(mFilterType)) {
                    newSet.remove(chip);
                    chipToRemove = chip;
                }
            }
        }

        if (chipToRemove != null) {
            mSearchItemsList.remove(chipToRemove);
            mSaleFilterAdapterClickListener.onAddRemoveFilter(false, mFilterType, mData.get(position), mShowSubCategories, chipToRemove, true);
        }
    }

    public void setSearchItemsList(Set<SearchChipModel> list) {
        mSearchItemsList = list;
    }

    public class TransformedNode {
        private GetCategoryTreeResponse mObject;
        private int mLevel;

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
        ArrayList<SaleFilterAdapter.TransformedNode> list = new ArrayList<>();
        for (GetCategoryTreeResponse node: data) {
            node.traverseTree(new GetCategoryTreeResponse.TreeTraversalBlock() {
                @Override
                public boolean execute(GetCategoryTreeResponse parent, Object option) {
                    int level = ((Integer) option).intValue();
                    list.add(new SaleFilterAdapter.TransformedNode(parent, Integer.valueOf(level)));
                    return true;
                }

                @Override
                public Object transformOption(GetCategoryTreeResponse parent, Object option) {
                    return Integer.valueOf(((Integer) option).intValue() + 1);
                }
            }, Integer.valueOf(0));
        }
        for(SaleFilterAdapter.TransformedNode node: list){
            GetCategoryTreeResponse categoryItem = node.getObject();

            if(mSourceType.equals("CategorySearch")){
                if (mCategoryKey.split(">")[0].equals(categoryItem.getKey()) ) {
                    mCategoryItems.add(categoryItem);
                    mCategoryLevel.add(node.getLevel());
                    mfilterCountList.add(String.valueOf(categoryItem.getCount()));
                    mData.add(categoryItem.getName());
                }
                if(mCategoryKey.equals(categoryItem.getKey())){
                    if(!categoryItem.getChildren().isEmpty()){
                        List<GetCategoryTreeResponse> child = categoryItem.getChildren();
                        for(GetCategoryTreeResponse children: child){
                            mCategoryItems.add(children);
                            mCategoryLevel.add(2);
                            mfilterCountList.add(String.valueOf(children.getCount()));
                            mData.add(children.getName());
                        }
                    }else{
                        if(node.getLevel() == 2){
                            mData.clear();
                            mCategoryItems.clear();
                            mCategoryLevel.clear();
                            mfilterCountList.clear();
                        }
                        mCategoryItems.add(categoryItem);
                        mCategoryLevel.add(node.getLevel());
                        mfilterCountList.add(String.valueOf(categoryItem.getCount()));
                        mData.add(categoryItem.getName());
                    }
                }else if(mCategoryKey == ""){
                    addCategoriesToList(node, categoryItem);
                }
            }else{
                addCategoriesToList(node, categoryItem);
            }
        }
        return list;
    }

    private void addCategoriesToList(TransformedNode node, GetCategoryTreeResponse categoryItem) {
        if(node.getLevel() == 0){
            mCategoryItems.add(categoryItem);
            mCategoryLevel.add(node.getLevel());
            mfilterCountList.add(String.valueOf(categoryItem.getCount()));
            mData.add(categoryItem.getName());
        }else{
            if(!categoryItem.getChildren().isEmpty()){
                List<GetCategoryTreeResponse> child = categoryItem.getChildren();
                for(GetCategoryTreeResponse children: child){
                    mCategoryItems.add(children);
                    mCategoryLevel.add(2);
                    mfilterCountList.add(String.valueOf(children.getCount()));
                    mData.add(children.getName());
                }
            }else{
                if(node.getLevel() == 1){
                    mCategoryItems.add(categoryItem);
                    mCategoryLevel.add(node.getLevel());
                    mfilterCountList.add(String.valueOf(categoryItem.getCount()));
                    mData.add(categoryItem.getName());
                }
            }
        }
    }

    public static class SaleFilterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_category_name)
        public TextView categoryText;

        @BindView(R.id.filter_product_count)
        public TextView productCountText;

        @BindView(R.id.row_filter_checkbox)
        public CheckBox filterCheckbox;

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
