package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SubCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private SubCategoryClickListener mSubCategoryAdapterClickListener;
    private SubCategoryItemClickListener mSubCategoryItemClickListener;
    private SubCategoryItemsAdapter mSubCategoryItemsAdapter;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private String mChosenCategoryKey;

    public SubCategoriesAdapter(
            String chosenCategorykey,
            List<GetCategoryTreeResponse> data,
            SubCategoryClickListener subCategoryClickListener,
            SubCategoryItemClickListener subCategoryItemClickListener,
            Map<String, List<GetCategoryTreeResponse>> categoryMap) {

        mChosenCategoryKey = chosenCategorykey;
        mData = data;
        mSubCategoryAdapterClickListener = subCategoryClickListener;
        mSubCategoryItemClickListener = subCategoryItemClickListener;
        mCategoryMap = categoryMap;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_subcategory, parent, false);
        return new SubCategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Context context = holder.itemView.getContext();

        if (!mData.isEmpty()){
            if (!mData.get(position).getName().equals("empty")){
                if (!mData.get(position).getName().isEmpty()){

                    ((SubCategoriesViewHolder) holder)
                            .subCategoryTitle.setText(mData.get(position).getName());

                    if(mData.get(position).getKey().equals(mChosenCategoryKey)){
                        ((SubCategoriesViewHolder) holder)
                                .subCategoryTitle.setTextColor(context.getResources().getColor(R.color.category_text_active));
                    }else{
                        ((SubCategoriesViewHolder) holder)
                                .subCategoryTitle.setTextColor(context.getResources().getColor(R.color.category_text_inactive));
                    }
                        //noinspection ConstantConditions

                }

                List<GetCategoryTreeResponse> subCategoryItems = getSubCategoryItems(mData.get(position).getKey());

                mSubCategoryItemsAdapter = new SubCategoryItemsAdapter(mChosenCategoryKey, subCategoryItems, mSubCategoryItemClickListener, mCategoryMap);
                ((SubCategoriesViewHolder) holder).subCategoryItemsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                ((SubCategoriesViewHolder) holder).subCategoryItemsRecyclerView.setAdapter(mSubCategoryItemsAdapter);

                if (subCategoryItems != null && !subCategoryItems.isEmpty()){
                    holder.itemView.setActivated(false);
                    ((SubCategoriesViewHolder) holder).subCategoryItemsRecyclerView.setVisibility(View.VISIBLE);


                    ((SubCategoriesViewHolder) holder).mViewholder_subcategory_container
                            .setOnClickListener(view -> {
                                    mSubCategoryAdapterClickListener.onSubCategoryClicked(mData.get(position));
                            });
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mData!=null)
            return mData.size();
        return 0;
    }

    public void replaceData(List<GetCategoryTreeResponse> getCategoryTreeResponses){
        mData = new ArrayList<>(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    static class SubCategoriesViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.viewholder_subcategory_container)
        RelativeLayout mViewholder_subcategory_container;

        @BindView(R.id.viewholder_subcategory_title)
        TextView subCategoryTitle;

        @BindView(R.id.viewholder_subcategory_items_recyclerview)
        RecyclerView subCategoryItemsRecyclerView;

        SubCategoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener((v)->{
                //do public sales banner api call
            });
        }
    }

    private List<GetCategoryTreeResponse> getSubCategoryItems(String categoryKey){
        return mCategoryMap.get(categoryKey);
    }


    public void setActiveCategoryKey(String categoryKey){
        mChosenCategoryKey = categoryKey;
        notifyDataSetChanged();
    }
}
