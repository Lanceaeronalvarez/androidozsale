package au.com.dealsdirect.ui.controller.shops.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

class FooterViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.adView_banner)
    View adView;

    FooterViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
