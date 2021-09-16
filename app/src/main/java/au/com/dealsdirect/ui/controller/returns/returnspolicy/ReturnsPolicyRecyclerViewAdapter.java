package au.com.dealsdirect.ui.controller.returns.returnspolicy;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReturnsPolicyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_TOP = 0;
    private static final int ITEM_TYPE_SUBTITLE = 1;
    private static final int ITEM_TYPE_FAQ = 2;

    List<Item> items;

    public ReturnsPolicyRecyclerViewAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_TOP:
                return new ReturnsPolicyTopViewHolder(LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.viewholder_returns_policy_top, parent, false));
            case ITEM_TYPE_SUBTITLE:
                return new SubtitleViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_subtitle, parent, false));
            case ITEM_TYPE_FAQ:
            default:
                return new ItemViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_text_item_wrap_content, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);

        switch (holder.getItemViewType()) {
            case ITEM_TYPE_TOP: {
                final TopItem topItem = (TopItem) item;
                final ReturnsPolicyTopViewHolder topViewHolder = (ReturnsPolicyTopViewHolder) holder;
                topViewHolder.setup(topItem.adapter, topItem.onButtonClickListener);
                break;
            }
            case ITEM_TYPE_SUBTITLE: {
                final SubtitleViewHolder subtitleViewHolder = (SubtitleViewHolder) holder;
                subtitleViewHolder.setup(((SubtitleItem) item).subtitle);
                break;
            }
            case ITEM_TYPE_FAQ: {
                final FaqItem faqItem = (FaqItem) item;
                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.setup(faqItem.title, faqItem.rightImage, faqItem.onItemClickListener);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getItemId();
    }

    static class ReturnsPolicyTopViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.return_policy_top_recyclerview)
        RecyclerView recyclerView;

        @BindView(R.id.return_policy_top_button)
        Button button;

        ReturnsPolicyTopViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(
                    recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);

            final PagerSnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);
        }

        void setup(ReturnsPolicyTopRecyclerViewAdapter adapter, OnItemClickListener onClickButton) {
            recyclerView.setAdapter(adapter);
            if (onClickButton != null) {
                button.setOnClickListener(v -> onClickButton.onClick());
            } else {
                button.setOnClickListener(null);
            }
        }
    }

    static class SubtitleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_subtitle)
        TextView subtitle;

        SubtitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setup(String subtitle) {
            this.subtitle.setText(subtitle);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_viewholder_text)
        TextView itemName;

        @BindView(R.id.row_viewholder_image_right)
        ImageView itemRightImage;

        ItemViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void setup(String itemName, Drawable itemRightImage, OnItemClickListener onItemClickListener) {
            this.itemName.setText(itemName);
            this.itemRightImage.setImageDrawable(itemRightImage);
            if (onItemClickListener != null) {
                itemView.setOnClickListener(v -> onItemClickListener.onClick());
            } else {
                itemView.setOnClickListener(null);
            }
        }
    }

    public interface OnItemClickListener {
        void onClick();
    }

    public interface Item {
        int getItemId();
    }

    public static class TopItem implements Item {
        ReturnsPolicyTopRecyclerViewAdapter adapter;
        OnItemClickListener onButtonClickListener;

        public TopItem(ReturnsPolicyTopRecyclerViewAdapter adapter, OnItemClickListener onButtonClickListener) {
            this.adapter = adapter;
            this.onButtonClickListener = onButtonClickListener;
        }

        @Override
        public int getItemId() {
            return ITEM_TYPE_TOP;
        }
    }

    public static class SubtitleItem implements Item {
        String subtitle;

        public SubtitleItem(String subtitle) {
            this.subtitle = subtitle;
        }

        @Override
        public int getItemId() {
            return ITEM_TYPE_SUBTITLE;
        }
    }

    public static class FaqItem implements Item {
        String title;
        Drawable rightImage;
        OnItemClickListener onItemClickListener;

        public FaqItem(String title, Drawable rightImage, OnItemClickListener onItemClickListener) {
            this.title = title;
            this.rightImage = rightImage;
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public int getItemId() {
            return ITEM_TYPE_FAQ;
        }
    }
}
