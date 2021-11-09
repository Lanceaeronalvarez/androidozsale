package au.com.dealsdirect.ui.controller.saleitems;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SaleItemFooterViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.adView_banner)
    View adView;

    SaleItemFooterViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
