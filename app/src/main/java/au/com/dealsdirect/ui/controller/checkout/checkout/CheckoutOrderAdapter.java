package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.ui.custom.PersonalisationLayout;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 28/06/2017.
 */

public class CheckoutOrderAdapter extends RecyclerView.Adapter<CheckoutOrderAdapter.ViewHolder> {

    private Context mContext;
    private List<Item> mData;
    private CheckoutMvpPresenter<CheckoutMvpView> mPresenter;
    private int resLayout;
    private static final int MAX_ITEM_QTY = 5;
    private CheckoutListener mClickListener;

    public CheckoutOrderAdapter(Context context, List<Item> data, CheckoutMvpPresenter<CheckoutMvpView> presenter,
                                CheckoutListener clickListener) {
        this.mContext = context;
        this.mData = data;
        this.mPresenter = presenter;
        this.mClickListener = clickListener;
    }

    @Override
    public CheckoutOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.partial_checkout_item, parent, false);
        return new CheckoutOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CheckoutOrderAdapter.ViewHolder holder, int position) {
        Item item = mData.get(position);

        //   (1) fix when item.fileName is null
        if (item.fileName != null) {
            if (!item.fileName.isEmpty()) {
                String newImageFilename = LegacyStringImageUtils.generateImageUrl(item.brandID, item.imageID, item.fileName);
                if (!holder.imageFilename.equals(newImageFilename)) {
                    holder.imageFilename = newImageFilename;
                    // rather display nothing than display the wrong image
                    holder.image.setImageDrawable(null);
                }
                ImageUtils.loadImageDontAnimate(holder.imageFilename, holder.image);
            }
        }

        holder.name.setText(item.item);
        if (item.size == null || item.size.length() < 0) {
            holder.sizeText.setVisibility(View.INVISIBLE);
            holder.sizeValue.setVisibility(View.INVISIBLE);
        } else {
            holder.sizeText.setVisibility(View.VISIBLE);
            holder.sizeValue.setVisibility(View.VISIBLE);
            holder.sizeValue.setText(item.size);
        }
        holder.colorText.setVisibility(View.GONE);

        holder.personalisationLayout.inflateForCheckout(mContext, item.getCustomizableItemDetailsList());

        holder.price.setText(PriceUtils.getPriceStringValue(item.price));
        holder.quantityLayout.setMax(MAX_ITEM_QTY);
        holder.quantityLayout.setQuantity(item.qty);
        holder.quantityLayout.setAutoUpdateQuantity(false);
        holder.quantityLayout.setEditTextToNonEditable();

        int subTotalVisibility = item.qty > 1 ? View.VISIBLE : View.GONE;
        holder.subTotal.setVisibility(subTotalVisibility);
        if (holder.subTotalLabel != null) { holder.subTotalLabel.setVisibility(subTotalVisibility); }
        if(item.qty > 1) { holder.subTotal.setText(PriceUtils.getPriceStringValue(item.getSubtotal())); }

        holder.quantityLayout.setOnQuantityChangeListener(new ProductQuantityLayout.onQuantityChangeListener() {
            @Override
            public void onQuantityIncrease(ProductQuantityLayout view, int value) {
                mPresenter.fetchAdjustItemQuantity("IncreaseOrderItem", item.id, view);
            }

            @Override
            public void onQuantityDecrease(ProductQuantityLayout view, int value) {
                mPresenter.fetchAdjustItemQuantity("DecreaseOrderItem", item.id, view);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            mClickListener.showItemDetail(holder, 0, "",
                    LegacyStringImageUtils.generateImageUrl(item.brandID, item.imageID, item.fileName),
                    "", item.getSaleID(), false, item.getItem(), item.getItem(),
                    String.valueOf(item.getPrice()), String.valueOf(item.getPrice()), item.getItemID());
        });
    }

    public void replaceData(List<Item> items) {
        mData = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_checkout_image)
        ImageView image;
        @BindView(R.id.item_checkout_name)
        TextView name;
        @BindView(R.id.item_checkout_size_text)
        TextView sizeText;
        @BindView(R.id.item_checkout_size_value)
        TextView sizeValue;
        @BindView(R.id.item_checkout_color_text)
        TextView colorText;
        @BindView(R.id.item_checkout_color)
        TextView colorValue;
        @BindView(R.id.item_checkout_price)
        TextView price;
        @BindView(R.id.item_checkout_quantity)
        ProductQuantityLayout quantityLayout;
        @BindView(R.id.item_subtotal_price)
        TextView subTotal;
        @Nullable
        @BindView(R.id.item_checkout_subtotal_label)
        TextView subTotalLabel;
        @Nullable
        @BindView(R.id.item_checkout_personalisation_layout)
        PersonalisationLayout personalisationLayout;

        String imageFilename = "";

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
