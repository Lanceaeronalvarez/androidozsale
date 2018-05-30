package au.com.dealsdirect.ui.controller.country;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.country.Country;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin on 12/18/17.
 */

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountriesViewHolder>{

    private List<Country> mCountries;
    private String mSelectedCountry;
    private CountryMvpPresenter mPresenter;
    private Context context;

    public CountryAdapter(ArrayList<Country> countries, Context context, CountryMvpPresenter presenter) {
        this.mCountries = countries;
        this.context = context;
        this.mPresenter = presenter;
    }

    @Override
    public CountriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_language, parent, false);
        CountryAdapter.CountriesViewHolder vh = new CountryAdapter.CountriesViewHolder(view, mPresenter);
        return vh;
    }

    @Override
    public void onBindViewHolder(CountriesViewHolder holder, int position) {
        if(mSelectedCountry.equals(mCountries.get(position).getShopCode()) || mSelectedCountry == mCountries.get(position).getShopCode()) {
            holder.mCountryText.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
        holder.mCountryText.setText(mCountries.get(position).getCountry());

        holder.itemView.setOnClickListener(v ->{
            mPresenter.onCountryItemClick(mCountries.get(position));
            holder.mCountryText.setTextColor(context.getResources().getColor(R.color.filter_text_active));
        });
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        if (mCountries!=null)
            return mCountries.size();
        return 0;
    }


    public void replaceData(List<Country> countries, String selectedCountry){
        mCountries = countries;
        mSelectedCountry = selectedCountry;
    }

    static class CountriesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.row_text_language)
        TextView mCountryText;

        CountryMvpPresenter mPresenter;

        public CountriesViewHolder(View itemView, CountryMvpPresenter presenter) {
            super(itemView);
            mPresenter = presenter;
            ButterKnife.bind(this, itemView);
        }
    }
}
