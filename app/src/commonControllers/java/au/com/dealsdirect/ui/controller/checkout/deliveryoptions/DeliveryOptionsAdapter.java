package au.com.dealsdirect.ui.controller.checkout.deliveryoptions;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.GetDeliveryServicePackageDetails;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.service.ourpay.OurpayTemplateText;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 30/05/2018.
 */

public class DeliveryOptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SetDeliveryOptionsObjectGenerator {

    private static final String OPTION_PARAMETERS_OPS_TYPE = "OurpaySelect";
    private static final String OPTION_PARAMETERS_NON_OPS_TYPE = "";

    private DeliveryOptionsMvpPresenter mPresenter;
    private MainActivity mActivity;
    private List<DeliveryOption> mDeliveryOptions = Collections.emptyList();
    private List<GetDeliveryServicePackageDetails.ResponseValue.Value> mOurpaySelectDeliveryOptions;
    private String mOurPaySelectDescText;
    private String mExpressDescText;
    private String mDeliveryOptionNameText;
    private String mDeliveryAddressId;
    private String mDeliveryServicePackageDetailId = "";
    private DeliveryServicePackageDetail mDeliveryServicePackageDetail;
    private DeliveryOption mPreviousItem;
    private DeliveryOption mCurrentItem;
    private RecyclerView mRecyclerView;
    private DeliveryOptionsOurPaySelectViewHolder mOurPaySelectViewHolder;
    private SetDeliveryOptionsObjectGenerator mSetDeliveryOptionsObjectGenerator;

    public DeliveryOptionsAdapter(MainActivity mainActivity, RecyclerView recyclerView,
                                  List<DeliveryOption> deliveryOptionList,
                                  String deliveryAddressId, DeliveryServicePackageDetail deliveryServiceDetailPackage,
                                  DeliveryOptionsMvpPresenter mvpPresenter) {
        mActivity = mainActivity;
        mRecyclerView = recyclerView;
        mOurPaySelectDescText = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_DELIVERYOPTION_OPS_DESCRIPTION);
        mExpressDescText = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_DELIVERYOPTION_EXPRESS_DESCRIPTION);
        mDeliveryOptions = deliveryOptionList;
        mDeliveryAddressId = deliveryAddressId;
        mDeliveryServicePackageDetail = deliveryServiceDetailPackage;
        if (mDeliveryServicePackageDetail != null) {
            mDeliveryServicePackageDetailId = mDeliveryServicePackageDetail.getDeliveryServicePackageDetailID();
        }

        mPresenter = mvpPresenter;
        mSetDeliveryOptionsObjectGenerator = this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_delivery_option_regular, parent, false);
            return new DeliveryOptionsRegularViewHolder(v);
        } else if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_delivery_option_ops, parent, false);
            mOurPaySelectViewHolder = new DeliveryOptionsOurPaySelectViewHolder(v);
            mPresenter.getDeliveryServicePackageDetails();
            return mOurPaySelectViewHolder;
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DeliveryOption deliveryOption = mDeliveryOptions.get(position);
        boolean isSelected = deliveryOption.getSelected();

        if (isSelected) {
            mPreviousItem = mCurrentItem = deliveryOption;
        }

        holder.itemView.setOnClickListener(v -> onSelectListener(deliveryOption, holder));

        if (holder instanceof DeliveryOptionsRegularViewHolder) {
            ((DeliveryOptionsRegularViewHolder) holder).deliveryOptionExpressDescTextView.setVisibility(View.GONE);

            List<String> deliveryOptionTitles = deliveryOption.getDeliveryOptions();
            String deliveryOptionTitle = deliveryOptionTitles.isEmpty() ? "" : deliveryOptionTitles.get(0);
            String deliveryOptionName = "";
            Double deliveryPrice = deliveryOption.getPrice();

            if (OurpayTemplateText.DeliveryOptions.STANDARD.equalsName(deliveryOptionTitle)) {  // get the name from template texts;
                deliveryOptionName = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_DELIVERYOPTION_STANDARD_TITLE);
            } else if (OurpayTemplateText.DeliveryOptions.EXPRESS.equalsName(deliveryOptionTitle)) {
                deliveryOptionName = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_DELIVERYOPTION_EXPRESS_TITLE);
                ((DeliveryOptionsRegularViewHolder) holder).deliveryOptionExpressDescTextView.setVisibility(View.VISIBLE);
                ((DeliveryOptionsRegularViewHolder) holder).deliveryOptionExpressDescTextView.setText(mExpressDescText);
            }

            ((DeliveryOptionsRegularViewHolder) holder).deliveryOptionCheckBox.setChecked(isSelected);
            ((DeliveryOptionsRegularViewHolder) holder).deliveryOptionTypeTextView.setText(deliveryOptionName);
            ((DeliveryOptionsRegularViewHolder) holder).deliveryOptionPriceTextView
                    .setText(deliveryPrice != null ? PriceUtils.getPriceStringValue(deliveryPrice) : null);

        } else if (holder instanceof DeliveryOptionsOurPaySelectViewHolder) {

            ((DeliveryOptionsOurPaySelectViewHolder) holder).ourpaySelectCheckBox.setChecked(isSelected);
            ((DeliveryOptionsOurPaySelectViewHolder) holder).ourpaySelectDescTextView.setText(mOurPaySelectDescText);
            ((DeliveryOptionsOurPaySelectViewHolder) holder).ourpaySelectOptionsRecyclerView.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            String completeTncText = mActivity.getString(R.string.by_choosing_ourpay_select) + " " + mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_OURPAY_TC_TEXT);
            ((DeliveryOptionsOurPaySelectViewHolder) holder).ourpaySelectTncTextView.setText(Html.fromHtml(completeTncText));
            ((DeliveryOptionsOurPaySelectViewHolder) holder).ourpaySelectTncTextView.setOnClickListener(v -> mPresenter.onTermsAndConditionsClicked());

        }
    }

    private void onSelectListener(DeliveryOption item, RecyclerView.ViewHolder holder) {

        if (holder instanceof DeliveryOptionsRegularViewHolder) {
            mDeliveryOptionNameText = ((DeliveryOptionsRegularViewHolder) holder).deliveryOptionTypeTextView.getText().toString();
        } else if (holder instanceof DeliveryOptionsOurPaySelectViewHolder) {
            mDeliveryOptionNameText = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_DELIVERYOPTION_OPS_TITLE);
        }

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

        if (!OurpayTemplateText.DeliveryOptions.OURPAYSELECT.equalsName(mCurrentItem.getDeliveryOptions().get(0))) {
            mPresenter.setDeliveryOption(createSetDeliveryOptionRequest("", false));
        } else {
            if (mDeliveryServicePackageDetail != null && mDeliveryServicePackageDetail.getPurchased()) {
                mPresenter.setDeliveryOption(createSetDeliveryOptionRequest(mDeliveryServicePackageDetailId, true));
            }
        }
    }

    @Override
    public SetDeliveryOption.OptionParameters createSetDeliveryOptionRequest(String deliveryServicePackageDetailId, boolean isOurpaySelect) {

        mCurrentItem.setName(mDeliveryOptionNameText);

        if (isOurpaySelect) {
            mCurrentItem.setOurPaySelect(true);
            mCurrentItem.setAgreedWithTerms(true);
        }

        Gson gson = new Gson();
        String deliveryOptionJsonString = gson.toJson(mCurrentItem);

        SetDeliveryOption.OptionParameters optionParameters
                = new SetDeliveryOption.OptionParameters(mDeliveryAddressId,
                isOurpaySelect ? OPTION_PARAMETERS_OPS_TYPE : OPTION_PARAMETERS_NON_OPS_TYPE,
                deliveryOptionJsonString, deliveryServicePackageDetailId);

        return optionParameters;
    }

    @Override
    public int getItemCount() {
        return mDeliveryOptions.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (!mDeliveryOptions.get(position).getDeliveryOptions().isEmpty() &&
                OurpayTemplateText.DeliveryOptions.OURPAYSELECT.
                        equalsName(mDeliveryOptions.get(position).getDeliveryOptions().get(0))) {
            return 1;
        }
        return 0;
    }

    public void setOurPaySelectDeliveryOptions(List<GetDeliveryServicePackageDetails.ResponseValue.Value> ourPaySelectDeliveryOptions) {
        mOurpaySelectDeliveryOptions = ourPaySelectDeliveryOptions;

        if (mOurpaySelectDeliveryOptions != null && !mOurpaySelectDeliveryOptions.isEmpty() &&
                mDeliveryServicePackageDetail == null || !mDeliveryServicePackageDetail.getPurchased()) {
            OurpaySelectDeliveryOptionsAdapter adapter = new OurpaySelectDeliveryOptionsAdapter(mOurpaySelectDeliveryOptions, mDeliveryServicePackageDetail, mPresenter, mSetDeliveryOptionsObjectGenerator);
            Context context = mOurPaySelectViewHolder.ourpaySelectOptionsRecyclerView.getContext();
            mOurPaySelectViewHolder.ourpaySelectOptionsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mOurPaySelectViewHolder.ourpaySelectOptionsRecyclerView.setAdapter(adapter);
        }
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

    public static class DeliveryOptionsOurPaySelectViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.delivery_option_check_box)
        CheckBox ourpaySelectCheckBox;
        @BindView(R.id.delivery_option_ops_recycler_view)
        RecyclerView ourpaySelectOptionsRecyclerView;
        @BindView(R.id.delivery_option_ops_desc)
        TextView ourpaySelectDescTextView;
        @BindView(R.id.delivery_option_ops_text_tc)
        TextView ourpaySelectTncTextView;

        public DeliveryOptionsOurPaySelectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
