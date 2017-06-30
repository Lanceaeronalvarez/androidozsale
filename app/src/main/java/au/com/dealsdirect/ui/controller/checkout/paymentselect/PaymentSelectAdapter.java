package au.com.dealsdirect.ui.controller.checkout.paymentselect;
/*
 * Created by CodeineBot on 1/11/17.
 */

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.AddressesItem;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.utils.ImageUtils;

public class PaymentSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<PaymentMethod> mData;
    private PaymentSelectMvpPresenter<PaymentSelectMvpView> mPresenter;
    private boolean isFromCart = false;


    public PaymentSelectAdapter(Context context, ArrayList<PaymentMethod> data,
                                PaymentSelectMvpPresenter<PaymentSelectMvpView> presenter,
                                boolean fromCart) {

        this.mContext = context;
        this.mData = data;
        this.mPresenter = presenter;
        this.isFromCart = fromCart;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.partial_payment_select_item, parent, false);
        return new PaymentSelectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PaymentSelectViewHolder vh = (PaymentSelectViewHolder) holder;

        PaymentMethod item = mData.get(position);

        ImageUtils.loadImage(mContext,item.getImageUrl(),vh.image);

        vh.name.setText(item.getPaymentType());
        vh.details.setText(item.getDescription());
        vh.divider.setVisibility(View.VISIBLE);

        vh.remove.setVisibility(isFromCart? View.GONE: View.VISIBLE);

        if(!isFromCart) {
            vh.remove.setOnClickListener(view1 -> mPresenter.removeUserPaymentMethod(item));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void replaceData(ArrayList<PaymentMethod> items){
        mData = items;
        notifyDataSetChanged();
    }

    public static class PaymentSelectViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView details;
        ImageView image;
        View divider;
        TextView remove;

        public PaymentSelectViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.partial_checkout_payment_name);
            details = (TextView) itemView.findViewById(R.id.partial_checkout_payment_details);
            image = (ImageView) itemView.findViewById(R.id.partial_checkout_payment_image);
            divider = itemView.findViewById(R.id.item_payment_select_divider);
            remove = (TextView) itemView.findViewById(R.id.partial_checkout_payment_remove);
        }
    }

}
