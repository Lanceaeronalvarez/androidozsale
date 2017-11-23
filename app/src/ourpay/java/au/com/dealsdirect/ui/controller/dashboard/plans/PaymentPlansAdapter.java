package au.com.dealsdirect.ui.controller.dashboard.plans;
/*
 * Created by CodeineBot on 6/7/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.Payment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentPlansAdapter extends RecyclerView.Adapter<PaymentPlansAdapter.ViewHolder> {

    Context mContext;
    PaymentPlansController mController;
    List<Payment> mData;

    public PaymentPlansAdapter(PaymentPlansController controller, List<Payment> data) {
        this.mController = controller;
        this.mData = data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_payment_plan_title)
        TextView title;

        @BindView(R.id.row_payment_plan_id)
        TextView id;

        @BindView(R.id.row_payment_plan_value)
        TextView value;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_payment_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Payment item = mData.get(position);

        holder.title.setText(item.getTitle());
        holder.id.setText(item.getId());
        holder.value.setText(item.getCurrency() + item.getValue());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mController.showPaymentDetailsController(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
