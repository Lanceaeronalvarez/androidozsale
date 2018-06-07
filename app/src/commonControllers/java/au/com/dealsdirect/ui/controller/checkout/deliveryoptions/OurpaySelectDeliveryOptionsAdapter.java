package au.com.dealsdirect.ui.controller.checkout.deliveryoptions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.GetDeliveryServicePackageDetails;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 30/05/2018.
 */

public class OurpaySelectDeliveryOptionsAdapter extends RecyclerView.Adapter<OurpaySelectDeliveryOptionsAdapter.OurPaySelectDeliveryOptionsViewHolder> {
    private List<GetDeliveryServicePackageDetails.ResponseValue.Value> mOurpayDeliveryOptions = Collections.emptyList();
    private DeliveryServicePackageDetail mDeliveryServiceDetailPackage;
    private String mDeliveryServiceDetailPackageId = "";
    private DeliveryOptionsMvpPresenter mPresenter;
    private GetDeliveryServicePackageDetails.ResponseValue.Value mPreviousItem;
    private SetDeliveryOptionsObjectGenerator mSetDeliveryOptionsObjectGenerator;
    private boolean mNewSelection;

    public OurpaySelectDeliveryOptionsAdapter(List<GetDeliveryServicePackageDetails.ResponseValue.Value> ourpayDeliveryOptions,
                                              DeliveryServicePackageDetail deliveryServicePackageDetail, DeliveryOptionsMvpPresenter mvpPresenter,
                                              SetDeliveryOptionsObjectGenerator setDeliveryOptionsObjectGenerator) {
        mOurpayDeliveryOptions = ourpayDeliveryOptions;
        mDeliveryServiceDetailPackage = deliveryServicePackageDetail;
        if(mDeliveryServiceDetailPackage != null){
            mDeliveryServiceDetailPackageId = mDeliveryServiceDetailPackage.getDeliveryServicePackageDetailID();
        }
        mPresenter = mvpPresenter;
        mSetDeliveryOptionsObjectGenerator = setDeliveryOptionsObjectGenerator;
    }

    @Override
    public OurPaySelectDeliveryOptionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_delivery_option_ops_sub_item, parent, false);
        return new OurPaySelectDeliveryOptionsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OurPaySelectDeliveryOptionsViewHolder holder, int position) {
        GetDeliveryServicePackageDetails.ResponseValue.Value item = mOurpayDeliveryOptions.get(position);

        //should only do this upon first load
        if(mDeliveryServiceDetailPackageId.equals(item.getID()) && !mNewSelection){
            item.setSelected(true);
        }

        boolean isSelected = item.getSelected();

        if(isSelected){
            mPreviousItem = item;
        }

        holder.itemView.setOnClickListener(v -> onSelectListener(item));
        holder.checkView.setVisibility(isSelected? View.VISIBLE : View.INVISIBLE);
        holder.opsDeliveryOptionTextView.setText(mOurpayDeliveryOptions.get(position).getName());
        holder.opsDeliveryOptionPriceTextView.setText(PriceUtils.getPriceStringValue(mOurpayDeliveryOptions.get(position).getAmount()));
    }

    @Override
    public int getItemCount() {
        return mOurpayDeliveryOptions.size();
    }


    private void onSelectListener(GetDeliveryServicePackageDetails.ResponseValue.Value currentItem) {

        int previousItemPos = mOurpayDeliveryOptions.indexOf(mPreviousItem);
        int currentItemPos = mOurpayDeliveryOptions.indexOf(currentItem);

        currentItem.setSelected(true);

        if (mPreviousItem != null && mPreviousItem != currentItem) { //unselect previous selected.
            mPreviousItem.setSelected(false);
            notifyItemChanged(previousItemPos);
            notifyItemChanged(currentItemPos);
            mNewSelection = true;
        }

        mPresenter.setDeliveryOption(mSetDeliveryOptionsObjectGenerator.createSetDeliveryOptionRequest(currentItem.getID(), true));
    }

    public static class OurPaySelectDeliveryOptionsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ourpay_select_delivery_option_check)
        View checkView;
        @BindView(R.id.ourpay_select_delivery_option_text_view)
        TextView opsDeliveryOptionTextView;
        @BindView(R.id.ourpay_select_delivery_option_price_text_view)
        TextView opsDeliveryOptionPriceTextView;

        public OurPaySelectDeliveryOptionsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
