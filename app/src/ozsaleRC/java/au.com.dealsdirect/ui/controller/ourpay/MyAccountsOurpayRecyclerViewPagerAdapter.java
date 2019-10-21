package au.com.dealsdirect.ui.controller.ourpay;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import butterknife.ButterKnife;

public class MyAccountsOurpayRecyclerViewPagerAdapter extends RecyclerView.Adapter {

    private List<MyAccountsOurpayCellAdapter> mCellAdapters = null;

    public void setCellAdapters(List<MyAccountsOurpayCellAdapter> cellAdapters) {
        this.mCellAdapters = cellAdapters;
        notifyDataSetChanged();
    }

    public List<MyAccountsOurpayCellAdapter> getCellAdapters() {
        return mCellAdapters;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        ViewHolder(View view) {
            super(view);
            recyclerView = (RecyclerView) view;
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView view = new RecyclerView(parent.getContext());

        view.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));
        view.setLayoutParams(new RelativeLayout
                .LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));


        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;

        vh.recyclerView.setAdapter(mCellAdapters.get(position));
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        ViewHolder vh = (ViewHolder) holder;

        vh.recyclerView.setAdapter(null);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemViewType(int position) {
        if (mCellAdapters == null || position >= mCellAdapters.size()) {
            return 0;
        } else {
            return mCellAdapters.get(position).getViewType();
        }
    }

    @Override
    public int getItemCount() {
        if (mCellAdapters == null) {
            return 0;
        } else {
            return mCellAdapters.size();
        }
    }
}
