package au.com.dealsdirect.ui.controller.checkout.checkout.steps.cartreview;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Shipment;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.events.GA4EventParams;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutListener;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutOrderAdapter;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.custom.BottomPopupView;
import au.com.dealsdirect.ui.custom.BottomPopupWebViewContentAdapter;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.utils.ActivityLaunchUtil;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.OnClick;

public class CheckoutStepsCartReviewController extends BaseController implements CheckoutStepsCartReviewMvpView, CheckoutListener {

    public static final String TAG = "CheckoutStepsCartReviewController";

    @Inject
    CheckoutStepsCartReviewMvpPresenter<CheckoutStepsCartReviewMvpView> mPresenter;

    @BindView(R.id.controller_checkout_steps_cart_review_toolbar_container)
    ViewGroup mToolbarContainer;

    @BindView(R.id.partial_toolbar_left_view)
    ImageButton mToolbarLeftView;

    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mToolbarRightView;

    @BindView(R.id.partial_toolbar_title)
    TextView mToolbarTitle;

    @BindView(R.id.controller_checkout_steps_cart_review_recyclerview_items)
    RecyclerView mRecyclerViewItems;

    @BindView(R.id.partial_checkout_summary_subtotal)
    TextView mSummarySubtotalTextView;
    @BindView(R.id.partial_checkout_summary_voucher)
    TextView mSummaryVoucherTextView;
    @BindView(R.id.partial_checkout_summary_shipping_label)
    TextView mSummaryShippingLabelTextView;
    @BindView(R.id.partial_checkout_summary_shipping_fee)
    TextView mSummaryShippingFeeTextView;
    @BindView(R.id.partial_checkout_summary_shipping_fee_container)
    ViewGroup mSummaryShippingFeeContainer;
    @BindView(R.id.partial_checkout_summary_tax)
    TextView mSummaryTaxTextView;
    @BindView(R.id.partial_checkout_summary_tax_container)
    ViewGroup mSummaryTaxContainer;
    @BindView(R.id.partial_checkout_summary_shipping_with_icon)
    RelativeLayout mFreeShippingLayout;
    @BindView(R.id.partial_checkout_summary_voucher_container)
    ViewGroup mVoucherValueContainer;

    private CheckoutOrderAdapter mRecyclerViewItemsAdapter = null;

    private boolean shouldShowToolbar = true;
    private boolean shouldShowItems = true;
    private boolean shouldShowSummary = true;

    private ViewGroup bottomPopupViewRoot = null;
    private BottomPopupView currentBottomPopupView = null;

    private List<ProductQuantityLayout> productQuantityLayouts = new ArrayList<>();

    public boolean isShouldShowToolbar() {
        return shouldShowToolbar;
    }

    public void setShouldShowToolbar(boolean shouldShowToolbar) {
        this.shouldShowToolbar = shouldShowToolbar;
    }

    public boolean shouldShowItems() {
        return shouldShowItems;
    }

    public void setShouldShowItems(boolean shouldShowItems) {
        this.shouldShowItems = shouldShowItems;
    }

    public boolean isShouldShowSummary() {
        return shouldShowSummary;
    }

    public void setShouldShowSummary(boolean shouldShowSummary) {
        this.shouldShowSummary = shouldShowSummary;
    }

    public void setBottomPopupViewRoot(ViewGroup bottomPopupViewRoot) {
        this.bottomPopupViewRoot = bottomPopupViewRoot;
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_checkout_steps_cart_review, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        bottomPopupViewRoot = (ViewGroup) view;

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mToolbarContainer.setVisibility(shouldShowToolbar ? View.VISIBLE : View.GONE);
        mToolbarRightView.setVisibility(View.INVISIBLE);
        mToolbarTitle.setText(view.getResources().getText(R.string.checkout));

        setupRecyclerViewItems();
        setupSummaryShipping();
        setupSummaryVouchers();
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackPressed() {
        if (getRouter().getBackstackSize() > 1) {
            getRouter().popCurrentController();
        }
    }

    @Override
    public void refreshCart() {
        resetProductQuantityLayoutLoaders();

        setupRecyclerViewItems();
        setupSummaryShipping();
        setupSummaryVouchers();
    }

    private void setupRecyclerViewItems() {
        if (mRecyclerViewItemsAdapter == null) {
            mRecyclerViewItemsAdapter = new CheckoutOrderAdapter(
                    mActivity,
                    null,
                    mPresenter.isShippingByPostcodeEnabled(),
                    mPresenter.getTemplateTextsRepository().getImpossibleToDeliverAtLocation(),
                    this,
                    this::showBottomPopupView);
            mRecyclerViewItemsAdapter.setEligibleProductsLinkListener(locationFilterHash -> mActivity.getMainController().openLocationFilterHash(locationFilterHash));
            mRecyclerViewItemsAdapter.setItemQuantityChangedListener(new CheckoutOrderAdapter.ItemQuantityChangedListener() {
                @Override
                public void onIncrease(String itemId, int newCount, ProductQuantityLayout view) {
                    productQuantityLayouts.add(view);
                    mPresenter.fetchAdjustItemQuantity("IncreaseOrderItem", itemId, null);
                }

                @Override
                public void onDecrease(String itemId, int newCount, ProductQuantityLayout view) {
                    productQuantityLayouts.add(view);
                    mPresenter.fetchAdjustItemQuantity("DecreaseOrderItem", itemId, null);

                    if (newCount == 0) {
                        logRemoveItemFromCart(view.getContext());
                    }
                }
            });
            mRecyclerViewItems.setAdapter(mRecyclerViewItemsAdapter);
            mRecyclerViewItems.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        }
        if (mPresenter.getCart() == null) {
            mRecyclerViewItemsAdapter.replaceData(null);
        } else {
            mRecyclerViewItemsAdapter.replaceData(mPresenter.getCart().getMappedShipments(), mPresenter.getCart().getDeliveryAddress() != null);
        }
    }

    private void setupSummaryShipping() {
        if (mPresenter.getCart() == null || mPresenter.getCart().getSummary() == null) {
            return;
        }

        final Summary summary = mPresenter.getCart().getSummary();
        final DeliveryAddress deliveryAddress = mPresenter.getCart().getDeliveryAddress();
        final boolean isShipmentAvailable = isShipmentAvailable(mPresenter.getCart().getShipments());
        final boolean isAddressValid = deliveryAddress != null;

        mSummaryShippingFeeContainer.setVisibility(View.VISIBLE);

        mSummarySubtotalTextView.setText(PriceUtils.getPriceStringValue(summary.getSubtotal()));
        String postcode = deliveryAddress != null ? deliveryAddress.getPostcode() : null;

        if (!mPresenter.isShippingByPostcodeEnabled() ||
                postcode == null || summary.getDelivery() == null) {
            mSummaryShippingLabelTextView.setText(mActivity.getResources().getString(R.string.shipping_text));
        } else {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(
                    mActivity.getResources().getString(R.string.shipping_text)
            );
            spannableStringBuilder.append(" (");
            int start = spannableStringBuilder.length();
            int color = mActivity.getResources().getColor(R.color.checkout_item_footer_other_text_color);
            spannableStringBuilder.append(
                    postcode,
                    new StyleSpan(BOLD),
                    SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(
                    new ForegroundColorSpan(color),
                    start,
                    spannableStringBuilder.length(),
                    SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(")");
            mSummaryShippingLabelTextView.setText(spannableStringBuilder);
        }

        if (mPresenter.isShippingByPostcodeEnabled() && !isShipmentAvailable) {
            mSummaryShippingFeeTextView.setVisibility(View.VISIBLE);
            final String unavailableText = mPresenter.getTemplateTextsRepository() == null ? "Unavailable" : mPresenter.getTemplateTextsRepository().getUnavailable();
            mSummaryShippingFeeTextView.setText(unavailableText);
            mSummaryShippingFeeTextView.setTextColor(mActivity.getResources().getColor(R.color.checkout_item_footer_red_text_color));
            mFreeShippingLayout.setVisibility(View.GONE);
        } else if (!isAddressValid) {
            mSummaryShippingFeeTextView.setVisibility(View.VISIBLE);
            mFreeShippingLayout.setVisibility(View.GONE);
            mSummaryShippingFeeTextView.setText(mActivity.getResources().getString(R.string.enter_address_above));
            mSummaryShippingFeeTextView.setTextColor(mActivity.getResources().getColor(R.color.enter_address_text_color));
        } else if (summary.getDelivery() == null) {
            mSummaryShippingFeeTextView.setVisibility(View.GONE);
            mFreeShippingLayout.setVisibility(View.GONE);
        } else if (summary.getDelivery() == 0) {
            mSummaryShippingFeeTextView.setVisibility(View.GONE);
            mFreeShippingLayout.setVisibility(View.VISIBLE);
        } else {
            mSummaryShippingFeeTextView.setVisibility(View.VISIBLE);
            mSummaryShippingFeeTextView.setText(PriceUtils.getPriceStringValue(summary.getDelivery()));
            mSummaryShippingFeeTextView.setTextColor(mActivity.getResources().getColor(R.color.text_dark));
            mFreeShippingLayout.setVisibility(View.GONE);
        }
    }

    private void setupSummaryVouchers() {
        if (mPresenter.getCart() == null || mPresenter.getCart().getSummary() == null) {
            return;
        }

        final Summary summary = mPresenter.getCart().getSummary();

        mSummaryVoucherTextView.setText(PriceUtils.getPriceStringValue(summary.getDiscount()));

        if (summary.getTax() > 0) {
            mSummaryTaxTextView.setText(PriceUtils.getPriceStringValue(summary.getTax()));
            mSummaryTaxContainer.setVisibility(View.VISIBLE);
        } else {
            mSummaryTaxContainer.setVisibility(View.GONE);
        }

        mVoucherValueContainer.setVisibility(summary.getDiscount() > 0 ? View.VISIBLE : View.GONE);
    }

    private boolean isShipmentAvailable(List<Shipment> shipments) {
        if (shipments == null) {
            return false;
        }
        for (Shipment shipment : shipments) {
            if (!shipment.getShippingAvailability()) {
                return false;
            }
        }
        return true;
    }

    private void showBottomPopupView(String textContent) {
        if (currentBottomPopupView != null) {
            currentBottomPopupView.dismiss(true);
        }
        final BottomPopupWebViewContentAdapter adapter = new BottomPopupWebViewContentAdapter();
        currentBottomPopupView = new BottomPopupView(bottomPopupViewRoot, adapter);

        adapter.setWebViewContent(textContent);
        adapter.setOnCloseButtonClickListener(() -> currentBottomPopupView.dismiss(true));
        adapter.setWebViewClientOverrideUrlLoading(url -> {
            if (!url.contains("about:blank")) {
                ActivityLaunchUtil.launchActivity(mActivity, url);
            }
            return true;
        });
        currentBottomPopupView.show(true);
    }

    private void resetProductQuantityLayoutLoaders() {
        for (ProductQuantityLayout productQuantityLayout : productQuantityLayouts) {
            productQuantityLayout.resetLoaders();
        }
        productQuantityLayouts.clear();
    }

    public void logRemoveItemFromCart(Context context) {
        GA4EventParams.GA4RemoveFromCartParams ga4EventParams = new GA4EventParams.GA4RemoveFromCartParams();
        prepareItemsForGA4EventParams(ga4EventParams);
        ga4EventParams.setCurrency(Settings.getSelectedCountry().currencyCode);
        if (mPresenter.getCart() != null &&
                mPresenter.getCart().getSummary() != null &&
                mPresenter.getCart().getSummary().getTotal() != null) {
            ga4EventParams.setValue(mPresenter.getCart().getSummary().getTotal());
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, context);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, CheckoutController.class.getSimpleName());
        parameters.put(DataCollector.EventParameters.GA4_EVENT_PARAMS, ga4EventParams);

        DataCollector.logEvent(Events.RemoveFromCart, parameters);
    }

    private void prepareItemsForGA4EventParams(GA4EventParams params) {
        if (mPresenter.getCart() == null || mPresenter.getCart().getMappedShipments() == null) {
            return;
        }
        final ArrayList<GA4EventParams.Item> ga4Items = new ArrayList<>();
        for (CartDetailsMapper.MappedShipment shipment : mPresenter.getCart().getMappedShipments()) {
            for (Item item : shipment.getMappedItems()) {
                GA4EventParams.Item ga4Item = new GA4EventParams.Item();
                ga4Item.setItemId(item.getItemID());
                ga4Item.setItemName(item.getItem());
                ga4Item.setPrice(item.getPrice());
                ga4Item.setQuantity(item.getQty());
                ga4Items.add(ga4Item);
            }
        }
        params.setItems(ga4Items);
    }

    @Override
    public void showItemDetail(View sourceView, int position, String seoIdentifierId, String imageUrl, String skuId, String saleId, boolean isFreeDelivery, String itemName, String brandName, String price, String oldPrice, String productID) {
        if (CommonUtils.loadSaleItem(mActivity, productID).isEmpty()) {
            return;
        }

        SaleItemDetailsController.Parameters.FromCheckout parameters = new SaleItemDetailsController.Parameters.FromCheckout(
                position,
                imageUrl,
                CommonUtils.loadSaleItem(mActivity, productID),
                skuId,
                CommonUtils.loadSaleId(mActivity, productID),
                itemName,
                brandName,
                price,
                "",
                "",
                isFreeDelivery,
                false);

        RouterTransaction routerTransaction = RouterTransaction
                .with(SaleItemDetailsController.newInstance(parameters))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());

        mActivity.getMainController().resetShopRouter();
        mActivity.getMainController().showShopController();
        mActivity.getMainController().getShopRouter().pushController(routerTransaction);
    }
}
