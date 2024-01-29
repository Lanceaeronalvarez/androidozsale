package au.com.dealsdirect.ui.controller.categories.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpPresenter;
import au.com.dealsdirect.ui.controller.categories.listener.NewSaleCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SaleCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MTC on 2019-12-05.
 */
public class NewSaleCategoryAdapter extends RecyclerView.Adapter<NewSaleCategoryAdapter.NewSaleCategoryViewHolder> {

    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private NewSaleCategoryClickListener mCategoryAdapterClickListener;
    private Context mContext;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private HashMap<String, Boolean> activeStates = new HashMap<>();

    public NewSaleCategoryAdapter(Context context,
                                  List<GetCategoryTreeResponse> data,
                                  NewSaleCategoryClickListener categoryClickListener,
                                  Map<String, List<GetCategoryTreeResponse>> categoryMap) {

        mContext = context;
        mData = data;
        mCategoryAdapterClickListener = categoryClickListener;
        mCategoryMap = categoryMap;
    }

    @NonNull
    @Override
    public NewSaleCategoryAdapter.NewSaleCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_newsalecategory, parent, false);
        return new NewSaleCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewSaleCategoryAdapter.NewSaleCategoryViewHolder holder, int position) {
        if(mData.get(position).getName().equals("All")){
         holder.categoryContainer.setVisibility(View.GONE);
        }
        String subCategoryTitle = mData.get(position).getName();
        holder.subCategoryTitle.setText(subCategoryTitle);
        GetCategoryTreeResponse item = mData.get(position);

        if(item.getChildren() != null){
            holder.subCategoryImageButton.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(view -> {
            mCategoryAdapterClickListener.onCategoryClicked(position, mData.get(position), subCategoryTitle);
        });

        holder.subCategoryImageButton.setOnClickListener(view -> {
            mCategoryAdapterClickListener.onCategoryClicked(position, mData.get(position), subCategoryTitle);
        });

    }

    public GetCategoryTreeResponse getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void replaceData(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        mData = new ArrayList<>(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    public static class NewSaleCategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_salecategory_container)
        RelativeLayout categoryContainer;

        @BindView(R.id.viewholder_salecategory_title)
        TextView subCategoryTitle;

        @BindView(R.id.viewholder_salecategory_image_button)
        ImageButton subCategoryImageButton;

        public CategoriesMvpPresenter mPresenter;

        public NewSaleCategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public NewSaleCategoryViewHolder(View itemView, CategoriesMvpPresenter presenter) {
            super(itemView);
            mPresenter = presenter;
            ButterKnife.bind(this, itemView);

        }
    }

}
