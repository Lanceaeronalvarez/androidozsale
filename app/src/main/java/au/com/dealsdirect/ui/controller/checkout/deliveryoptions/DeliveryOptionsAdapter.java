package au.com.dealsdirect.ui.controller.checkout.deliveryoptions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.service.deliveryoptions.DeliveryOptions;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 30/05/2018.
 */

public class DeliveryOptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SetDeliveryOptionsObjectGenerator {

    private static final String OPTION_PARAMETERS_NON_OPS_TYPE = "";

    private MainActivity mActivity;
    private List<DeliveryOption> mDeliveryOptions = Collections.emptyList();
    private String mDeliveryOptionNameText;
    private String mDeliveryAddressId;
    private String mDeliveryServicePackageDetailId = null;
    private DeliveryServicePackageDetail mDeliveryServicePackageDetail;
    private DeliveryOption mPreviousItem;
    private DeliveryOption mCurrentItem;
    private SetDeliveryOptionsObjectGenerator mSetDeliveryOptionsObjectGenerator;
    private boolean mIsAddressValid;
    private DeliveryOptionsAdapterHelper helper;

    public DeliveryOptionsAdapter(MainActivity mainActivity,
                                  List<DeliveryOption> deliveryOptionList,
                                  String deliveryAddressId,
                                  DeliveryServicePackageDetail deliveryServiceDetailPackage,
                                  boolean isAddressValid,
                                  DeliveryOptionsAdapterHelper deliveryOptionsAdapterHelper) {
        helper = deliveryOptionsAdapterHelper;
        mActivity = mainActivity;
        mDeliveryOptions = deliveryOptionList;
        mDeliveryAddressId = deliveryAddressId;
        mDeliveryServicePackageDetail = deliveryServiceDetailPackage;
        mIsAddressValid = isAddressValid;
        if (mDeliveryServicePackageDetail != null) {
            mDeliveryServicePackageDetailId = mDeliveryServicePackageDetail.getDeliveryServicePackageDetailID();
        }

        mSetDeliveryOptionsObjectGenerator = this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_delivery_option_regular, parent, false);
        return new DeliveryOptionsRegularViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final DeliveryOption deliveryOption = mDeliveryOptions.get(position);
        final boolean isSelected = deliveryOption.getSelected();

        if (isSelected) {
            mPreviousItem = mCurrentItem;
            mCurrentItem = deliveryOption;
        }

        if (deliveryOption.isAvailable()) {
            holder.itemView.setOnClickListener(v -> onSelectListener(deliveryOption, holder));
        } else {
            holder.itemView.setOnClickListener(null);
        }

        final DeliveryOptionsRegularViewHolder deliveryOptionsRegularViewHolder = ((DeliveryOptionsRegularViewHolder) holder);

        deliveryOptionsRegularViewHolder.deliveryOptionExpressDescTextView.setVisibility(View.GONE);

        final List<String> deliveryOptionTitles = deliveryOption.getDeliveryOptions();
        final String deliveryOptionTitle = deliveryOptionTitles.isEmpty() ? "" : deliveryOptionTitles.get(0);
        String deliveryOptionName = "";
        final Double deliveryPrice = deliveryOption.getPrice();
        String deliveryPriceText = null;
        if (mIsAddressValid && deliveryPrice != null) {
            deliveryPriceText = deliveryPrice > 0 ? PriceUtils.getPriceStringValue(deliveryPrice) : mActivity.getResources().getString(R.string.free_text);
        }

        if (DeliveryOptions.STANDARD.equalsName(deliveryOptionTitle)) {  // get the name from template texts;
            deliveryOptionName = helper.getStandardTitleText();
        } else if (DeliveryOptions.EXPRESS.equalsName(deliveryOptionTitle)) {
            deliveryOptionName = helper.getExpressTitleText();
            deliveryOptionsRegularViewHolder.deliveryOptionExpressDescTextView.setVisibility(View.VISIBLE);
            deliveryOptionsRegularViewHolder.deliveryOptionExpressDescTextView.setText(helper.getExpressDescriptionText());
        }

        deliveryOptionsRegularViewHolder.deliveryOptionCheckBox.setChecked(isSelected);
        deliveryOptionsRegularViewHolder.deliveryOptionTypeTextView.setText(deliveryOptionName);
        deliveryOptionsRegularViewHolder.deliveryOptionPriceTextView.setText(deliveryPriceText);
    }

    private void onSelectListener(DeliveryOption item, RecyclerView.ViewHolder holder) {
        mDeliveryOptionNameText = ((DeliveryOptionsRegularViewHolder) holder).deliveryOptionTypeTextView.getText().toString();

        int previousItemPos = mDeliveryOptions.indexOf(mPreviousItem);
        int currentItemPos = mDeliveryOptions.indexOf(item);

        //select current item
        item.setSelected(true);

        if (mPreviousItem != null && mPreviousItem != item) { //unselect previous selected.
            mPreviousItem.setSelected(false);
            notifyItemChanged(previousItemPos);
            notifyItemChanged(currentItemPos);
        }

        //save as current item
        mCurrentItem = item;

        helper.setDeliveryOption(createSetDeliveryOptionRequest(null));
    }

    @Override
    public SetDeliveryOption.OptionParameters createSetDeliveryOptionRequest(String deliveryServicePackageDetailId) {

        mCurrentItem.setName(mDeliveryOptionNameText);

        Gson gson = new Gson();
        String deliveryOptionJsonString = gson.toJson(mCurrentItem);

        SetDeliveryOption.OptionParameters optionParameters
                = new SetDeliveryOption.OptionParameters(
                mDeliveryAddressId,
                OPTION_PARAMETERS_NON_OPS_TYPE,
                deliveryOptionJsonString,
                deliveryServicePackageDetailId);

        return optionParameters;
    }

    @Override
    public int getItemCount() {
        return mDeliveryOptions.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public static class DeliveryOptionsRegularViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.delivery_option_check_box)
        CheckBox deliveryOptionCheckBox;
        @BindView(R.id.delivery_option_title_text_view)
        TextView deliveryOptionTypeTextView;
        @BindView(R.id.delivery_option_price_text_view)
        TextView deliveryOptionPriceTextView;
        @BindView(R.id.delivery_option_express_desc_text_view)
        TextView deliveryOptionExpressDescTextView;

        public DeliveryOptionsRegularViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface DeliveryOptionsAdapterHelper {
        String getStandardTitleText();

        String getExpressTitleText();

        String getExpressDescriptionText();

        void setDeliveryOption(SetDeliveryOption.OptionParameters parameters);
    }
}
