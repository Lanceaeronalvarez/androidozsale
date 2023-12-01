package au.com.dealsdirect.ui.controller.returns.currentreturns.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturn;
import au.com.dealsdirect.ui.controller.returns.currentreturns.viewholder.CurrentReturnViewHolder;
import au.com.dealsdirect.ui.controller.returns.returnsteps.ReturnTrackingClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/29/17.
 */

public class CurrentReturnAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int VIEW_TYPE_HEADER = -1;
    private final static int VIEW_TYPE_ITEM = 0;
    private final static int VIEW_TYPE_SPACER = 1;

    private List<CurrentReturn> mCurrentReturnList;

    private CurrentReturnItemListener listener;

    private ReturnTrackingClickListener trackingClickListener;

    public CurrentReturnAdapter(List<CurrentReturn> currentReturnsList,
                                CurrentReturnItemListener listener,
                                ReturnTrackingClickListener trackingClickListener) {
        mCurrentReturnList = currentReturnsList;
        this.trackingClickListener = trackingClickListener;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new SubtitleViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.viewholder_subtitle,
                                parent, false));
            case VIEW_TYPE_ITEM:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_current_return, parent, false);
                return new CurrentReturnViewHolder(v);
            default:
                return new SpacerViewHolder(LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.partial_checkout_item_spacer,
                                parent,
                                false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_HEADER:
                ((SubtitleViewHolder) holder).subtitle.setText("MY RETURNS");
                break;
            case VIEW_TYPE_ITEM:
                final CurrentReturn item = mCurrentReturnList.get((position - 1) / 2);
                ((CurrentReturnViewHolder) holder).setup(item, trackingClickListener);
                final View optionButton = ((CurrentReturnViewHolder) holder).getCurrentReturnItemOption();
                optionButton.setOnClickListener(v -> listener.optionsOpened(item ,optionButton));

                holder.itemView.setOnClickListener(v -> listener.itemSelected(item));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position - 1) % 2;
    }

    @Override
    public int getItemCount() {
        return Math.max(0, mCurrentReturnList.size() * 2 - 1) + 1;
    }

    public void updateCurrentReturnsList(List<CurrentReturn> currentReturnsList) {
        mCurrentReturnList = currentReturnsList;
        notifyDataSetChanged();
    }

    static class SubtitleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_subtitle)
        TextView subtitle;

        SubtitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class SpacerViewHolder extends RecyclerView.ViewHolder {
        public SpacerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface CurrentReturnItemListener {
        void itemSelected(CurrentReturn item);
        void optionsOpened(CurrentReturn item, View anchor);
    }
}
