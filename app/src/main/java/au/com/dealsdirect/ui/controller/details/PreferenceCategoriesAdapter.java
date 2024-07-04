package au.com.dealsdirect.ui.controller.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PreferenceCategoriesAdapter extends RecyclerView.Adapter<PreferenceCategoriesAdapter.SaleFilterViewHolder> {

    private PreferenceCategoriesClickListener mClickListener;
    private ArrayList  mData = new ArrayList();
    private HashMap<String, Boolean> mCategories = new LinkedHashMap<>();
    boolean isEnabled;
    public PreferenceCategoriesAdapter(Context context, PreferenceCategoriesClickListener clickListener, Map<String, String>  mData, HashMap<String, Boolean> categories, boolean isEnabled) {
        this.mData.addAll(mData.entrySet());
        this.isEnabled = isEnabled;
        mCategories = categories;
        mClickListener = clickListener;
    }

    @Override
    public SaleFilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_preference_categories, parent, false);
        SaleFilterViewHolder vh = new SaleFilterViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(SaleFilterViewHolder holder, int position) {
        PreferenceCategoriesAdapter.SaleFilterViewHolder vh = (PreferenceCategoriesAdapter.SaleFilterViewHolder) holder;

        Map.Entry<String, String> item = (Map.Entry) mData.get(position);
        holder.categoryText.setText(item.getKey());

        holder.filterCheckbox.setEnabled(isEnabled);
        holder.filterCheckbox.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        holder.disabledCheckbox.setVisibility(isEnabled ? View.GONE : View.VISIBLE);

        if(isEnabled){
            if(item.getValue() == "all"){
                if(mCategories.containsValue(false)){
                    holder.filterCheckbox.setChecked(false);
                }else{
                    holder.filterCheckbox.setChecked(true);
                }
            }else{
                holder.filterCheckbox.setChecked(mCategories.get(item.getValue().toLowerCase()));
            }
        }

        holder.filterCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(item.getValue() == "all"){
                    for (Map.Entry<String, Boolean> entry : mCategories.entrySet()) {
                        mCategories.put(entry.getKey(), isChecked);
                    }
                }else{
                    mCategories.put(item.getValue().toLowerCase(), isChecked);
                }
                mClickListener.onCategoryClicked(mCategories);
            }
        });

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

    public static class SaleFilterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_category_name)
        public TextView categoryText;

        @BindView(R.id.row_filter_checkbox)
        public CheckBox filterCheckbox;

        @BindView(R.id.row_filter_disabled_checkbox)
        public RelativeLayout disabledCheckbox;

        public SaleFilterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
