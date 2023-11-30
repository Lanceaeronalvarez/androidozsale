package au.com.dealsdirect.ui.controller.returns.newreturn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnItem;
import au.com.dealsdirect.ui.controller.returns.newreturn.viewholder.NewReturnOrderViewHolder;
import au.com.dealsdirect.ui.controller.returns.newreturn.viewholder.NewReturnOrderViewHolder.ValueChangedListener;

/**
 * dp Created by Admin on 7/25/17.
 */

public class NewReturnOrdersAdapter extends RecyclerView.Adapter<NewReturnOrderViewHolder> {

    private Boolean[] mDataChecked;

    private List<NewReturnItem> mCurrentReturnList;
    private Context mContext;
    private String mProductId;
    private int mInvoiceNumber;

    private ValueChangedListener listener;

    public NewReturnOrdersAdapter(
            List<NewReturnItem> orderList,
            Context context,
            ValueChangedListener listener,
            String productId, int invoiceNumber) {

        mCurrentReturnList = orderList;
        mContext = context;
        this.listener = listener;
        mProductId = productId;
        mInvoiceNumber = invoiceNumber;
        setupDataChecked();
    }

    private void setupDataChecked() {
        mDataChecked = new Boolean[mCurrentReturnList.size()];
        for (int i = 0; i < mCurrentReturnList.size(); i++) {
            final NewReturnItem item = mCurrentReturnList.get(i);
            mDataChecked[i] = mProductId != null && !mProductId.isEmpty() && item.getId().equalsIgnoreCase(mProductId);
        }
    }

    @NonNull
    @Override
    public NewReturnOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.viewholder_new_return_order, parent, false);
        return new NewReturnOrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NewReturnOrderViewHolder holder, int position) {
        final NewReturnItem item = mCurrentReturnList.get(position);

        holder.setup(item, mInvoiceNumber);

        final ValueChangedListener itemListener = (item1, isChecked) -> {
            mDataChecked[position] = isChecked;
            if (listener != null) {
                listener.updateReturnValue(item1, isChecked);
            }
        };

        holder.setupCheckBox(item, mDataChecked[position], itemListener);

        holder.setupQuantityLayout(item, itemListener);
    }

    @Override
    public int getItemCount() {
        return mCurrentReturnList != null ? mCurrentReturnList.size() : 0;
    }

    public List<NewReturnItem> getData() {
        return mCurrentReturnList;
    }
}
