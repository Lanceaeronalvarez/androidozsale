package au.com.dealsdirect.ui.controller.checkout.checkouthost;

import static au.com.dealsdirect.data.network.model.events.WishlistEventRequest.WishListInfo.ReferrerValue.PRODUCT_PAGE;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.events.WishlistEventRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetBestSellerResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.data.network.model.vouchers.Voucher;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.EventTypeId;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.bestsellers.BestSellersWidgetHelper;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper.MappedShipment;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutListener;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutOrderAdapter;
import au.com.dealsdirect.ui.controller.checkout.checkout.RecentlyViewedWidgetHelper;
import au.com.dealsdirect.ui.controller.saleitemdetails.HorizontalScrollingItemsAdapter;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.custom.BottomPopupView;
import au.com.dealsdirect.ui.custom.BottomPopupWebViewContentAdapter;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.ui.custom.transitions.ArcZoomChangeHandler;
import au.com.dealsdirect.utils.ActivityLaunchUtil;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.ScrollingImageHorizontal.HorizontalRecyclerItemsViewHolder;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by smartwave on 13/06/2018.
 */

public class CheckoutHostController extends BaseController implements CheckoutHostMvpView, CheckoutListener {

    @Inject
    CheckoutMvpPresenter<CheckoutMvpView> mPresenter;

    @BindView(R.id.controller_checkout_host_left_container)
    ViewGroup mLeftContainer;
    @BindView(R.id.controller_checkout_host_empty_container)
    ViewGroup mEmptyContainer;
    @BindView(R.id.checkout_detail_container)
    ViewGroup mCheckoutDetailContainer;
    @BindView(R.id.controller_checkout_container)
    ViewGroup mCheckoutContainer;

    @BindView(R.id.partial_toolbar_left_view)
    View mToolbarLeftButton;
    @BindView(R.id.partial_toolbar_title)
    TextView mTitleTextView;
    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mToolbarRightButton;


    @BindView(R.id.controller_checkout_orders_label)
    TextView mOrdersLabel;

    @BindView(R.id.controller_checkout_recyclerview_items)
    RecyclerView mRecyclerView;

    @Nullable
    @BindView(R.id.partial_checkout_empty_widget_area)
    ViewGroup mWidgetArea;

    private ViewGroup checkoutHostView = null;

    private Router mCheckoutDetailRouter;
    private CheckoutMvpView mCheckoutDetailView;
    private CheckoutOrderAdapter mAdapter;
    private List<MappedShipment> mItemList = new ArrayList<>();

    private boolean hasDeliveryAddress = false;
    private boolean mIsCheckoutHostUpdated;
    private boolean mHasSavedInstance = false;

    private BottomPopupView currentBottomPopupView = null;

    private int lastBestSellerItemPosition = -1;
    private HorizontalRecyclerItemsViewHolder bestSellersViewHolder = null;
    private BestSellersWidgetHelper bestSellersWidgetHelper = null;
    private RecentlyViewedWidgetHelper recentlyViewedWidgetHelper = null;


    public static CheckoutHostController newInstance() {
        return new CheckoutHostController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public CheckoutHostController(Bundle args) {
        super(args);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_checkout_host, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        if (view instanceof ViewGroup) {
            checkoutHostView = (ViewGroup) view;
        }

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        //disable toolbar left and right buttons
        mToolbarLeftButton.setVisibility(View.INVISIBLE);
        mToolbarRightButton.setVisibility(View.INVISIBLE);
        mTitleTextView.setText(getString(R.string.account_orders));

        mCheckoutDetailRouter = getChildRouter(mCheckoutDetailContainer);
        CommonControllerChangeListener.addToRouter(mCheckoutDetailRouter);

        final CheckoutController checkoutController = CheckoutController.newInstance(this);
        mCheckoutDetailRouter.setRoot(RouterTransaction.with(checkoutController).tag(CheckoutController.TAG));

        mCheckoutDetailView = checkoutController;

        mAdapter = new CheckoutOrderAdapter(
                mActivity,
                mItemList,
                mPresenter,
                this,
                priceInfo -> showBottomPopupView(priceInfo));
        mAdapter.setShouldAddSpacerOnTop(mPresenter.isTablet());
        mAdapter.setEligibleProductsLinkListener(locationFilterHash -> mActivity.getMainController().openLocationFilterHash(locationFilterHash));
        mAdapter.setItemQuantityChangedListener(new CheckoutOrderAdapter.ItemQuantityChangedListener() {
            @Override
            public void onIncrease(String itemId, int newCount, ProductQuantityLayout view) {
                mPresenter.fetchAdjustItemQuantity("IncreaseOrderItem", itemId, null, view);
            }

            @Override
            public void onDecrease(String itemId, int newCount, ProductQuantityLayout view) {
                mPresenter.fetchAdjustItemQuantity("DecreaseOrderItem", itemId, null, view);

                if (newCount == 0) {
                    logRemoveItemFromCart(view.getContext());
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));

        mToolbarLeftButton.setOnClickListener(v -> {
            mActivity.onBackPressed();
        });
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        if (mCheckoutDetailView != null) {
            loadCart();
        }
    }

    @Override
    public boolean handleBack() {

        return super.handleBack();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
    }

    @Override
    public void loadCart() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.loadCart();
        }
    }

    @Override
    public void onOrientationChanged(Configuration newConfiguration) {
        final List<RouterTransaction> backstack = mCheckoutDetailRouter.getBackstack();
        ((BaseController) backstack.get(0).controller()).onOrientationChanged(newConfiguration);
        ((BaseController) backstack.get(backstack.size() - 1).controller()).onOrientationChanged(newConfiguration);
    }

    @Override
    public void showMyPayDetails(CheckoutDetailsMapper value, Ourpay ourpay) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showMyPayDetails(value, ourpay);
        }
    }

    @Override
    public void showCartDetails(List<MappedShipment> items) {

        showCartDetailsOnChild(items);

        showCartDetailsOnHost(items);
    }

    @Override
    public void showCartDetailsOnChild(List<MappedShipment> items) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showCartDetailsOnChild(items);
        }
    }

    @Override
    public void showCartDetailsOnHost(List<MappedShipment> items) {
        if (items == null || items.isEmpty()) {
            showNoCartItemsLayout();
            return;
        }
        mItemList = items;

        refreshItemList(hasDeliveryAddress);

        mEmptyContainer.setVisibility(View.GONE);
        mLeftContainer.setVisibility(View.VISIBLE);
        mCheckoutDetailContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAddressDetails(DeliveryAddress deliveryAddress, List<DecorationInfoList> decorationInfoList) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showAddressDetails(deliveryAddress, decorationInfoList);
            mCheckoutDetailView.showCartDetailsFooter(deliveryAddress != null);
            mCheckoutDetailView.showCartDetailsPostcode(deliveryAddress != null ? deliveryAddress.getPostcode() : null);
        }
    }

    @Override
    public void showCartDetailsFooter(boolean show) {
        hasDeliveryAddress = show;
        refreshItemList(hasDeliveryAddress);
    }

    @Override
    public void showCartDetailsPostcode(String postcode) {
        if (mAdapter == null) {
            return;
        }
        mAdapter.setPostcodeOverride(postcode);
        mAdapter.notifyDataSetChanged();
    }

    private void refreshItemList(boolean showFooter) {
        if (mAdapter == null) {
            return;
        }
        mAdapter.replaceData(mItemList, showFooter);
    }


    @Override
    public void showDeliveryOptions(List<DeliveryOption> deliveryOptions, DeliveryServicePackageDetail deliveryServicePackageDetail) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showDeliveryOptions(deliveryOptions, deliveryServicePackageDetail);
        }
    }

    @Override
    public void showPaymentDetails(PaymentMethod paymentMethod) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showPaymentDetails(paymentMethod);
        }
    }

    @Override
    public void showVoucherDetails(List<Voucher> vouchers) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showVoucherDetails(vouchers);
        }
    }

    @Override
    public void setIsShipmentAvailable(boolean isShipmentAvailable) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.setIsShipmentAvailable(isShipmentAvailable);
        }
    }

    @Override
    public void showSummaryDetails(Summary summary) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showSummaryDetails(summary);
        }
    }

    @Override
    public void setPaymentList(List<PaymentMethod> paymentList) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.setPaymentList(paymentList);
        }
    }

    @Override
    public void showAfterpayPanel(boolean isAvailable, String description) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showAfterpayPanel(isAvailable, description);
        }
    }

    @Override
    public void hideAfterpayPanel() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.hideAfterpayPanel();
        }
    }

    @Override
    public void showLPayPanel() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showLPayPanel();
        }
    }

    @Override
    public void hideLPayPanel() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.hideLPayPanel();
        }
    }

    @Override
    public void showKlarnaPanel(String description) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showKlarnaPanel(description);
        }
    }

    @Override
    public void hideKlarnaPanel() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.hideKlarnaPanel();
        }
    }

    @Override
    public void showZipPayPanel() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showZipPayPanel();
        }
    }

    @Override
    public void hideZipPayPanel() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.hideZipPayPanel();
        }
    }

    @Override
    public void storeCartDetails(CheckoutDetailsMapper value) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.storeCartDetails(value);
        }
    }

    @Override
    public void triggerLoginTicket() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.triggerLoginTicket();
        }
    }

    @Override
    public void updateCheckoutBadge() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.updateCheckoutBadge();
        }
    }

    @Override
    public boolean isCartLoading() {
        if (mCheckoutDetailView != null) {
            return mCheckoutDetailView.isCartLoading();
        } else {
            return false;
        }
    }

    @Override
    public void setCartIsLoading(boolean val) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.setCartIsLoading(val);
        }
    }

    @Override
    public boolean isOurPaySelectDeliveryMethod() {
        if (mCheckoutDetailView != null) {
            return mCheckoutDetailView.isOurPaySelectDeliveryMethod();
        } else {
            return false;
        }
    }

    @Override
    public Router getDisplayRouter() {
        return mCheckoutDetailRouter;
    }

//    @Override
//    public void initializeVisaCheckout() {
//
//    }

    private void showNoCartItemsLayout() {
        mEmptyContainer.setVisibility(View.VISIBLE);
        mLeftContainer.setVisibility(View.GONE);
        mCheckoutDetailContainer.setVisibility(View.GONE);
    }

    @Optional
    @OnClick(R.id.partial_checkout_empty_button)
    void shopNow() {
        mActivity.getMainController().showShopController();
    }

    @Override
    public void showItemDetail(View sourceView, int position, String seoIdentifierId, String imageUrl,
                               String skuId, String saleId, boolean isFreeDelivery,
                               String itemName, String brandName, String price, String oldPrice,
                               String productID) {

        if (CommonUtils.loadSaleItem(mActivity, productID).isEmpty()) {
            return;
        }

        SaleItemDetailsController.Parameters.FromCheckout parameters = new SaleItemDetailsController.Parameters.FromCheckout(position,
                null,
                imageUrl,
                CommonUtils.loadSaleItem(mActivity, productID),
                skuId,
                CommonUtils.loadSaleId(mActivity, productID),
                itemName,
                brandName,
                price,
                oldPrice,
                "", "", isFreeDelivery, false);

        RouterTransaction routerTransaction = RouterTransaction
                .with(SaleItemDetailsController.newInstance(parameters));

        int[] originalPos = new int[2];
        sourceView.getLocationOnScreen(originalPos);
        int left = originalPos[0];
        int top = originalPos[1];
        int width = sourceView.getWidth();
        int height = sourceView.getHeight();
        routerTransaction = routerTransaction
                .pushChangeHandler(new ArcZoomChangeHandler(left, top, width, height))
                .popChangeHandler(new ArcZoomChangeHandler(left, top, width, height));

        getRouter().pushController(routerTransaction);

    }

    public Router getCheckoutDetailRouter() {
        return mCheckoutDetailRouter;
    }

    @Override
    public void showAgeRestriction(boolean hasAgeRestriction) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showAgeRestriction(hasAgeRestriction);
        }
    }

    @Override
    public void updateCartWithMappedValues(CheckoutDetailsMapper mappedValues) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.updateCartWithMappedValues(mappedValues);
        }
    }

    public void logRemoveItemFromCart(Context context) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, context);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, CheckoutController.class.getSimpleName());

        DataCollector.logEvent(Events.RemoveFromCart, parameters);
    }

    private void showBottomPopupView(String textContent) {
        if (checkoutHostView == null) {
            return;
        }
        if (currentBottomPopupView != null) {
            currentBottomPopupView.dismiss(true);
        }
        final BottomPopupWebViewContentAdapter adapter = new BottomPopupWebViewContentAdapter();
        currentBottomPopupView = new BottomPopupView(checkoutHostView, adapter);

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

    @Override
    public void showBestSellers(List<GetBestSellerResponse> getBestSellerResponses) {
        if (mWidgetArea == null) {
            return;
        }

        final List<SaleItemProduct> items = new ArrayList<>(getBestSellerResponses);

        if (items.isEmpty()) {
            if (bestSellersViewHolder != null && mWidgetArea.indexOfChild(bestSellersViewHolder.itemView) < 0) {
                mWidgetArea.removeView(bestSellersViewHolder.itemView);
            }
            return;
        }

        HorizontalScrollingItemsAdapter adapter = new HorizontalScrollingItemsAdapter(items, true, false, true);
        adapter.setOnItemTappedListener((item, position, size) -> {
            lastBestSellerItemPosition = position;

            SaleItemDetailsController.Parameters.FromSaleItemProduct parameters = new SaleItemDetailsController.Parameters.FromSaleItemProduct(item);

            RouterTransaction routerTransaction = RouterTransaction
                    .with(SaleItemDetailsController.newInstance(parameters));

            routerTransaction = routerTransaction
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler());

            getRouter().pushController(routerTransaction);
        });

        adapter.setOnPriceInfoTappedListener(item -> mPresenter.getPricingInfoText(item.getSeoIdentifier()));

        adapter.setWishlistListener(new HorizontalScrollingItemsAdapter.WishlistListener() {
            @Override
            public void addToWishlist(SaleItemProduct item) {
                final String productId = item.getId();
                CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback = () -> {
                    logWishlistEvent(productId, true);
                };
                mPresenter.addProductToWishlist(item.getId(), item.getSeoIdentifier(), "", delayedCallback);
            }

            @Override
            public void removeFromWishlist(SaleItemProduct item) {
                final String productId = item.getId();
                CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback = () -> {
                    logWishlistEvent(productId, false);
                };
                mPresenter.removeProductFromWishlist(item.getId(), delayedCallback);
            }

            @Override
            public boolean isProductInWishlist(SaleItemProduct item) {
                return mPresenter.isProductInWishlist(item.getId());
            }
        });

        bestSellersWidgetHelper = new BestSellersWidgetHelper(adapter, mActivity, mPresenter.isTablet());
        final int orientation = ScreenUtils.getOrientation(mActivity);
        if (bestSellersViewHolder == null) {
            bestSellersViewHolder = bestSellersWidgetHelper.createViewHolder(mWidgetArea, orientation);
        }
        if (mWidgetArea.indexOfChild(bestSellersViewHolder.itemView) < 0) {
            mWidgetArea.addView(bestSellersViewHolder.itemView);
        }
        bestSellersViewHolder.onViewBound();
        bestSellersWidgetHelper.onBindViewHolder(bestSellersViewHolder, orientation);
    }

    @Override
    public void showPricingInfoText(String rrpText, Double totalPercentOff, Double originalPrice, String combinedPricingInfoText) {
        if (mActivity.getSupplierOriginalPriceInfoHelper() != null) {
            showBottomPopupView(
                    mActivity.getSupplierOriginalPriceInfoHelper()
                            .getOriginalPriceInfoWebViewContent(rrpText, totalPercentOff, originalPrice));
        } else {
            showBottomPopupView(combinedPricingInfoText);
        }
    }

    @Override
    public void showRecentlyViewedItems(List<RecentlyViewedItemResponse> response) {
        if (mWidgetArea == null) {
            return;
        }

        if (response == null || response.isEmpty()) {
            if (bestSellersViewHolder != null && mWidgetArea.indexOfChild(bestSellersViewHolder.itemView) < 0) {
                mWidgetArea.removeView(bestSellersViewHolder.itemView);
            }
            return;
        }

        final List<SaleItemProduct> items = new ArrayList<>(response);

        HorizontalScrollingItemsAdapter adapter = new HorizontalScrollingItemsAdapter(items, true, false, true);
        adapter.setOnItemTappedListener((item, position, size) -> {
            lastBestSellerItemPosition = position;

            SaleItemDetailsController.Parameters.FromSaleItemProduct parameters = new SaleItemDetailsController.Parameters.FromSaleItemProduct(item);

            RouterTransaction routerTransaction = RouterTransaction
                    .with(SaleItemDetailsController.newInstance(parameters));

            routerTransaction = routerTransaction
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler());

            getRouter().pushController(routerTransaction);
        });

        adapter.setOnPriceInfoTappedListener(item -> mPresenter.getPricingInfoText(item.getSeoIdentifier()));

        adapter.setWishlistListener(new HorizontalScrollingItemsAdapter.WishlistListener() {
            @Override
            public void addToWishlist(SaleItemProduct item) {
                final String productId = item.getId();
                CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback = () -> {
                    logWishlistEvent(productId, true);
                };
                mPresenter.addProductToWishlist(item.getId(), item.getSeoIdentifier(), "", delayedCallback);
            }

            @Override
            public void removeFromWishlist(SaleItemProduct item) {
                final String productId = item.getId();
                CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback = () -> {
                    logWishlistEvent(productId, false);
                };
                mPresenter.removeProductFromWishlist(item.getId(), delayedCallback);
            }

            @Override
            public boolean isProductInWishlist(SaleItemProduct item) {
                return mPresenter.isProductInWishlist(item.getId());
            }
        });

        final int orientation = ScreenUtils.getOrientation(mActivity);
        if (bestSellersViewHolder != null) {
            mWidgetArea.removeView(bestSellersViewHolder.itemView);
        }
        recentlyViewedWidgetHelper = new RecentlyViewedWidgetHelper(adapter, mActivity, mPresenter.isTablet());
        bestSellersViewHolder = recentlyViewedWidgetHelper.createViewHolder(mWidgetArea, orientation);
        mWidgetArea.addView(bestSellersViewHolder.itemView);
        bestSellersViewHolder.onViewBound();
        recentlyViewedWidgetHelper.onBindViewHolder(bestSellersViewHolder, orientation);
    }

    private void logWishlistEvent(String productId, boolean liked) {
        WishlistEventRequest request = new WishlistEventRequest();
        request.setEventType(EventTypeId.EVENT_WISHLIST);

        WishlistEventRequest.WishListInfo wishlistInfo = new WishlistEventRequest.WishListInfo();
        request.setWishlistInfo(wishlistInfo);

        wishlistInfo.setOperation(liked ? 1 : 0);
        wishlistInfo.setProductId(productId);
        wishlistInfo.setReferrer(PRODUCT_PAGE);
        wishlistInfo.setProductsQuantity(mPresenter.wishlistCount());

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.WISHLIST_EVENT_REQUEST, request);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, TAG);
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);

        DataCollector.logEvent(Events.WishlistEvent, parameters);
    }
}
