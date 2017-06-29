package au.com.dealsdirect.ui.controller.categories.adapter;

import android.support.transition.TransitionManager;
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
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpPresenter;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SubCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private CategoriesMvpPresenter mPresenter;
    private SubCategoryClickListener mSubCategoryAdapterClickListener;
    private SubCategoryItemClickListener mSubCategoryItemClickListener;
    private SubCategoryItemsAdapter mSubCategoryItemsAdapter;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private int mExpandedPosition = -1;

    public SubCategoriesAdapter(
            List<GetCategoryTreeResponse> data,
            CategoriesMvpPresenter presenter,
            SubCategoryClickListener subCategoryClickListener,
            SubCategoryItemClickListener subCategoryItemClickListener,
            Map<String, List<GetCategoryTreeResponse>> categoryMap) {

        mData = data;
        mPresenter = presenter;
        mSubCategoryAdapterClickListener = subCategoryClickListener;
        mSubCategoryItemClickListener = subCategoryItemClickListener;
        mCategoryMap = categoryMap;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_subcategory, parent, false);
        SubCategoriesAdapter.SubCategoriesViewHolder vh = new SubCategoriesAdapter.SubCategoriesViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (!mData.isEmpty()){
            if (!mData.get(position).getName().isEmpty()){
                ((SubCategoriesViewHolder) holder)
                        .subCategoryTitle.setText(mData.get(position).getName());
            }

            List<GetCategoryTreeResponse> subCategoryItems = getSubCategoryItems(mData.get(position).getKey());

            if (subCategoryItems.isEmpty()){
                ((SubCategoriesViewHolder) holder).subCategoryDropdownImage.setVisibility(View.GONE);
            }else{
                final boolean isExpanded = position==mExpandedPosition;
                ((SubCategoriesViewHolder) holder)
                        .subCategoryItemsRecyclerView.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                ((SubCategoriesViewHolder) holder).subCategoryDropdownImage
                        .setBackgroundDrawable(holder.itemView.getContext().getResources().getDrawable(isExpanded?R.drawable.ic_remove:R.drawable.ic_add_gray ));


                holder.itemView.setActivated(isExpanded);

                ((SubCategoriesViewHolder) holder).subCategoryDropdownImage
                        .setOnClickListener(view -> {

                            ((SubCategoriesViewHolder) holder).subCategoryDropdownImage
                                    .setBackgroundDrawable(holder.itemView.getContext().getResources()
                                            .getDrawable(R.drawable.ic_remove));

                            ((SubCategoriesViewHolder) holder)
                                    .subCategoryItemsRecyclerView.setVisibility(View.VISIBLE);


                            mExpandedPosition = isExpanded ? -1:position;
                            TransitionManager.beginDelayedTransition(
                                        ((SubCategoriesViewHolder) holder)
                                                .subCategoryItemsRecyclerView);
                            notifyDataSetChanged();

                        });


            }
            mSubCategoryItemsAdapter = new SubCategoryItemsAdapter(subCategoryItems, mPresenter, mSubCategoryItemClickListener);
            ((SubCategoriesViewHolder) holder).subCategoryItemsRecyclerView .setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
            ((SubCategoriesViewHolder) holder).subCategoryItemsRecyclerView .setAdapter(mSubCategoryItemsAdapter);


            ((SubCategoriesViewHolder) holder).subCategoryTitle.setOnClickListener(view -> {
                mSubCategoryAdapterClickListener.onSubCategoryClicked(
                        mData.get(position).getId(),
                        mData.get(position).getName(),
                        mData.get(position).getKey());
            });



        }

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

        @BindView(R.id.viewholder_subcategory_title)
        TextView subCategoryTitle;

        @BindView(R.id.viewholder_subcategory_items_recyclerview)
        RecyclerView subCategoryItemsRecyclerView;

        @BindView(R.id.viewholder_subcategory_dropdown_icon)
        ImageView subCategoryDropdownImage;

        public SubCategoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener((v)->{
                //do public sales banner api call
            });
        }
    }

    public List<GetCategoryTreeResponse> getSubCategoryItems(String categoryKey){

        return mCategoryMap.get(categoryKey);
    }
}
