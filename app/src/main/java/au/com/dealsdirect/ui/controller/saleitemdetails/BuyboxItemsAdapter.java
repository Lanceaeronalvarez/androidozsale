package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitemdetails.SaleItemDetails;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BuyboxItemsAdapter extends RecyclerView.Adapter<BuyboxItemsAdapter.ViewHolder> {

    private static final String SELLER_LINK = "[[sellerLink]]";

    private final String sellerNameTemplate;
    private final String selectButtonTitle;
    private final List<SaleItemDetails.BuyBoxItem> buyBoxGroup;
    private final BuyBoxItemsHelper helper;

    public BuyboxItemsAdapter(List<SaleItemDetails.BuyBoxItem> buyBoxGroup, String sellerNameTemplate, String selectButtonTitle, BuyBoxItemsHelper helper) {
        this.sellerNameTemplate = sellerNameTemplate;
        this.selectButtonTitle = selectButtonTitle;
        this.buyBoxGroup = buyBoxGroup;
        this.helper = helper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_buybox_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setup(buyBoxGroup.get(position), sellerNameTemplate, selectButtonTitle, helper);
    }

    @Override
    public int getItemCount() {
        return buyBoxGroup.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.price_text)
        TextView priceTextView;
        @BindView(R.id.sold_by_text)
        TextView soldByTextView;
        @BindView(R.id.select_button)
        Button selectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setup(SaleItemDetails.BuyBoxItem item, String sellerNameTemplate, String selectButtonTitle, BuyBoxItemsHelper helper) {
            StringBuilder priceText = new StringBuilder();
            if (item.getSalePrice() == null || item.getSalePrice().getValue() == 0) {
                priceText.append(PriceUtils.getPriceStringValue(item.getPrice().getValue()));
            } else {
                priceText.append(PriceUtils.getPriceStringValue(item.getSalePrice().getValue()));
            }
            if (item.getShippingText() != null && !item.getShippingText().isEmpty()) {
                if (item.getShippingText().charAt(0) != ' ') {
                    priceText.append(" ");
                }
                priceText.append(item.getShippingText());
            }
            priceTextView.setText(priceText);

            SpannableStringBuilder sellerName = new SpannableStringBuilder();
            if (item.getSellerName() != null && !item.getSellerName().isEmpty()) {
                int start = -1;
                final int index = sellerNameTemplate.indexOf(SELLER_LINK);
                if (index >= 0) {
                    sellerName.append(sellerNameTemplate);
                    sellerName.replace(index, index + SELLER_LINK.length(), item.getSellerName());
                    start = index;
                } else {
                    sellerName.append(sellerNameTemplate);
                    if (item.getSellerName().charAt(0) != ' ') {
                        priceText.append(" ");
                    }
                    start = sellerName.length();
                    sellerName.append(item.getSellerName());
                }

                if (start >= 0) {
                    sellerName.setSpan(new UnderlineSpan(), start, start + item.getSellerName().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
            soldByTextView.setText(sellerName);
            soldByTextView.setOnClickListener(v -> {
                if (helper != null) {
                    helper.onLinkPressed(item);
                }
            });

            selectButton.setText(selectButtonTitle);
            selectButton.setOnClickListener(v -> {
                if (helper != null) {
                    helper.onButtonPressed(item);
                }
            });
        }
    }

    public interface BuyBoxItemsHelper {
        void onLinkPressed(SaleItemDetails.BuyBoxItem item);
        void onButtonPressed(SaleItemDetails.BuyBoxItem item);
    }
}
