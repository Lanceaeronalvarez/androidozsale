package au.com.dealsdirect.ui.controller.returns.returnorders.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.returnorders.GetReturnOrders;
import au.com.dealsdirect.ui.controller.returns.returnorders.viewholder.ReturnOrderViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/30/17.
 */

public class ReturnOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int VIEW_TYPE_HEADER = -1;
    private final static int VIEW_TYPE_ITEM = 0;
    private final static int VIEW_TYPE_SPACER = 1;

    private OnSelectListener onSelectListener;

    private List<GetReturnOrders> mNewReturnOrderList;
    private Context mContext;

    public ReturnOrdersAdapter(
            Context context,
            List<GetReturnOrders> newReturnsOrderList,
            OnSelectListener onSelectListener) {

        mContext = context;
        mNewReturnOrderList = newReturnsOrderList;
        this.onSelectListener = onSelectListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new SubtitleViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.viewholder_subtitle,
                                parent, false));
            case VIEW_TYPE_ITEM:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_return_order, parent, false);
                return new ReturnOrderViewHolder(v);
            default:
                return new SpacerViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.partial_checkout_item_spacer,
                                parent,
                                false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_HEADER:
                ((SubtitleViewHolder) holder).subtitle.setText("SELECT INVOICE");
                break;
            case VIEW_TYPE_ITEM:
                final ReturnOrderViewHolder viewHolder = (ReturnOrderViewHolder) holder;
                final GetReturnOrders item = mNewReturnOrderList.get((position - 1) / 2);

                viewHolder.setup(item, v -> onSelectListener.onSelect(item));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mNewReturnOrderList == null) {
            return 0;
        }
        return Math.max(0, mNewReturnOrderList.size() * 2 - 1) + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position - 1) % 2;
    }

    static class SubtitleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_subtitle)
        TextView subtitle;

        SubtitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class SpacerViewHolder extends RecyclerView.ViewHolder {
        public SpacerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnSelectListener {
        void onSelect(GetReturnOrders item);
    }
}
