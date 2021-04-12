package au.com.dealsdirect.ui.controller.returns.returnorders.viewholder;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.returnorders.GetReturnOrders;
import au.com.dealsdirect.ui.controller.returns.returnorders.adapter.ReturnOrdersImageAdapter;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/30/17.
 */

public class ReturnOrderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.new_returns_order_row_container)
    LinearLayout newReturnsOrderProductItem;

    @BindView(R.id.new_current_orders_item_name)
    TextView newReturnsOrderItemName;

    @BindView(R.id.return_orders_recyclerview)
    RecyclerView newReturnsRecyclerView;

    @BindView(R.id.return_orders_description)
    TextView description;

    public ReturnOrderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setup(GetReturnOrders item, View.OnClickListener onClickListener) {
        final Context context = itemView.getContext();

        String invoiceText = context.getResources().getString(R.string.invoice_text) + " " + item.getInvoiceNumber();
        newReturnsOrderItemName.setText(invoiceText);

        ReturnOrdersImageAdapter imageAdapter = new ReturnOrdersImageAdapter(item.getItems());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        newReturnsRecyclerView.setAdapter(imageAdapter);
        newReturnsRecyclerView.setLayoutManager(layoutManager);
        imageAdapter.notifyDataSetChanged();

        newReturnsOrderProductItem.setOnClickListener(onClickListener);

        final HashMap<String, String> map = new HashMap<>();
        final ArrayList<String> list = new ArrayList<>();
        list.add("Items");
        map.put("Items", Integer.toString(item.getItemsCount()));
        list.add("Invoice Total");
        map.put("Invoice Total", PriceUtils.getPriceStringValue(item.getTotalAmount()));
        list.add("Status");
        map.put("Status", item.getStatus());
        list.add("Tracking");
        map.put("Tracking", item.getTracking());

        setupDescription(list, map);
    }

    private void setupDescription(List<String> orderedKeys, Map<String, String> values) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (int i = 0; i < orderedKeys.size(); i += 1) {
            if (i != 0) {
                spannableStringBuilder.append("\n");
            }
            final String key = orderedKeys.get(i);
            final String value = values.get(key);
            spannableStringBuilder.append(key, new StyleSpan(Typeface.NORMAL), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(": ");
            spannableStringBuilder.append(value, new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        description.setText(spannableStringBuilder);
    }


}
