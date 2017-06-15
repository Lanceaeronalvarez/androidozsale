package au.com.dealsdirect.ui.controller.categories;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.ui.controller.categories.listener.CategoryClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * jp Created by smartwave on 08/06/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GetPublicSalesCategoriesResponse.SaleList> mData = new ArrayList<>();
    private CategoriesMvpPresenter mPresenter;
    private CategoryClickListener mCategoryAdapterClickListener;

    public CategoriesAdapter(
            List<GetPublicSalesCategoriesResponse.SaleList> data,
            CategoriesMvpPresenter presenter,
            CategoryClickListener categoryClickListener) {

        mData = data;
        mPresenter = presenter;
        mCategoryAdapterClickListener = categoryClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_categories_row, parent, false);
        CategoriesViewHolder vh = new CategoriesViewHolder(view,mPresenter);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CategoriesViewHolder) holder).categoryText.setText(mData.get(position).name);
        ((CategoriesViewHolder) holder).itemView.setOnClickListener(view -> {
            mCategoryAdapterClickListener.onCategoryClicked(mData.get(position).iD);
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void replaceData(List<GetPublicSalesCategoriesResponse.SaleList> saleList){
        mData = new ArrayList<>(saleList);
        notifyDataSetChanged();
    }

    static class CategoriesViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.row_category_name)
        TextView categoryText;

        CategoriesMvpPresenter mPresenter;

        public CategoriesViewHolder(View itemView, CategoriesMvpPresenter presenter) {
            super(itemView);
            mPresenter = presenter;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener((v)->{
                //do public sales banner api call
            });
        }
    }
}
