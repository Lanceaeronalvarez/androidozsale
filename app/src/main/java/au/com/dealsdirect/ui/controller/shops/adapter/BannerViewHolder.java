package au.com.dealsdirect.ui.controller.shops.adapter;

import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;

class BannerViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.viewholder_banner_layout)
    ViewGroup layout;

    @BindView(R.id.viewholder_banner_image)
    ImageView image;

    @BindView(R.id.viewholder_banner_name)
    TextView name;

    @BindView(R.id.viewholder_banner_discount)
    TextView discount;

    @BindView(R.id.viewholder_banner_free_delivery)
    View freeShipping;

    @BindView(R.id.viewholder_banner_percent_off)
    TextView percentOff;

    @BindView(R.id.viewholder_banner_stickers_container1)
    LinearLayout stickersContainer1;
    @BindView(R.id.viewholder_banner_stickers_container2)
    LinearLayout stickersContainer2;
    @BindView(R.id.viewholder_banner_stickers_container3)
    LinearLayout stickersContainer3;

    BannerViewHolder(View view, int height, int viewType) {
        super(view);
        ButterKnife.bind(this, view);

        if (height > 0) {
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.height = height;
            layout.setLayoutParams(params);
        }

        if (BannersAdapter.isViewHolderOldType(viewType)) {
            if (view.getContext().getResources().getBoolean(R.bool.is_using_older_banner)) {
                name.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.bg_banner_name_old));
            } else {
                name.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.bg_banner_name_new));
            }
        }

        if (freeShipping instanceof TextView) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("  FREE SHIPPING");
            Drawable d = ContextCompat.getDrawable(view.getContext(), R.drawable.ic_free_shipping_white);
            if (d != null) {
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                ImageSpan imageSpan = new ImageSpan(d, DynamicDrawableSpan.ALIGN_BASELINE);
                spannableStringBuilder.setSpan(imageSpan, 0, 1, SPAN_INCLUSIVE_EXCLUSIVE);
            }
            ((TextView) freeShipping).setText(spannableStringBuilder);
        }
    }

    BannerViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    void rearrangeStickers(final int maxWidth) {
        stickersContainer1.removeAllViews();
        stickersContainer2.removeAllViews();
        stickersContainer3.removeAllViews();
        final List<LinearLayout> containerViews = new ArrayList<LinearLayout>(3) {{
            add(stickersContainer1);
            add(stickersContainer2);
            add(stickersContainer3);
        }};
        final Sticker discountSticker = new Sticker(discount, 3);
        final Sticker percentOffSticker = new Sticker(percentOff, 2);
        final Sticker freeShippingSticker = new Sticker(freeShipping, 1);
        final List<List<Sticker>> containers = new ArrayList<List<Sticker>>(3) {{
            add(new LinkedList<Sticker>() {{
                add(discountSticker);
                add(percentOffSticker);
                add(freeShippingSticker);
            }});
            add(new LinkedList<>());
            add(new LinkedList<>());
        }};
        for (int i = 0; i < containers.size() - 1; i++) {
            final List<Sticker> container = containers.get(i);
            final List<Sticker> nextContainer = containers.get(i + 1);

            while (totalWidth(container) >= maxWidth && container.size() > 1) {
                Sticker sticker = getStickerWithSmallestWeight(container);
                container.remove(sticker);
                nextContainer.add(0, sticker);
            }
        }
        for (int i = 0; i < containers.size() && i < containerViews.size(); i++) {
            List<Sticker> container = containers.get(i);
            LinearLayout containerView = containerViews.get(i);
            for (int j = 0; j < container.size(); j++) {
                containerView.addView(container.get(j).view, j);
            }
        }
    }

    private static int totalWidth(List<Sticker> stickers) {
        int totalWidth = 0;
        for (Sticker sticker : stickers) {
            totalWidth += sticker.width;
        }
        return totalWidth;
    }

    private static Sticker getStickerWithSmallestWeight(List<Sticker> stickers) {
        if (stickers.isEmpty()) {
            return null;
        }
        Sticker output = stickers.get(0);
        for (int i = 1; i < stickers.size(); i++) {
            Sticker sticker = stickers.get(i);
            if (output.weight > sticker.weight) {
                output = sticker;
            }
        }
        return output;
    }

    private static class Sticker {
        View view;
        int weight;
        int width = 0;

        public Sticker(View view, int weight) {
            this.view = view;
            this.weight = weight;
            computeWidth();
        }

        private void computeWidth() {
            if (view.getVisibility() != View.GONE) {
                view.measure(0, 0);
                width = view.getMeasuredWidth();
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    width += ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).getMarginStart();
                    width += ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).getMarginEnd();
                }
            } else {
                width = 0;
            }
        }
    }

    Disposable subscription;
}
