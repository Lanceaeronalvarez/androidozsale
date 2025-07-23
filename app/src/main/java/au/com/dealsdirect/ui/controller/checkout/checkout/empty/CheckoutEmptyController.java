package au.com.dealsdirect.ui.controller.checkout.checkout.empty;

import static au.com.dealsdirect.data.network.model.events.WishlistEventRequest.WishListInfo.ReferrerValue.PRODUCT_PAGE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.events.WishlistEventRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetBestSellerResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.EventTypeId;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.bestsellers.BestSellersWidgetHelper;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.RecentlyViewedWidgetHelper;
import au.com.dealsdirect.ui.controller.checkout.checkout.split.CheckoutSplitController;
import au.com.dealsdirect.ui.controller.checkout.checkout.steps.CheckoutStepsController;
import au.com.dealsdirect.ui.controller.saleitemdetails.HorizontalScrollingItemsAdapter;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.custom.BottomPopupView;
import au.com.dealsdirect.ui.custom.BottomPopupWebViewContentAdapter;
import au.com.dealsdirect.utils.ActivityLaunchUtil;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.ScrollingImageHorizontal.HorizontalRecyclerItemsViewHolder;
import butterknife.BindView;

public class CheckoutEmptyController extends BaseController implements CheckoutEmptyMvpView {

    public static final String TAG = "CheckoutEmptyController";

    private static final long WIDGET_AGE_THRESHOLD = 120000;

    @Inject
    CheckoutEmptyMvpPresenter<CheckoutEmptyMvpView> mPresenter;

    @BindView(R.id.no_cart_items_layout)
    ViewGroup layout;

    @Nullable
    @BindView(R.id.partial_checkout_empty_button)
    View mGoToShopButton;
    @Nullable
    @BindView(R.id.partial_checkout_empty_widget_area)
    ViewGroup mWidgetArea;

    private int lastBestSellerItemPosition = -1;
    private HorizontalRecyclerItemsViewHolder widgetAreaHorizontalRecyclerItemsViewHolder = null;
    private BestSellersWidgetHelper bestSellersWidgetHelper = null;
    private RecentlyViewedWidgetHelper recentlyViewedWidgetHelper = null;
    private BottomPopupView currentBottomPopupView = null;

    private long lastTimeWidgetReloaded = -1;

    private boolean loggedInStatus = false;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_checkout_empty, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
        mPresenter.onDetach();
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.loadCart(null, null, false);
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        mPresenter.loadCart(null, null, false);
    }

    @Override
    protected void setUp(View view) {
        layout.setVisibility(View.GONE);
    }

    @Override
    public void showCart() {
        mActivity.getMainController().showCartItemsSize();
        if (mPresenter.getCart() == null ||
                mPresenter.getCart().getItems() == null ||
                mPresenter.getCart().getItems().isEmpty()) {
            if (loggedInStatus != mPresenter.checkIsLoggedIn() &&
                    widgetAreaHorizontalRecyclerItemsViewHolder != null &&
                    mWidgetArea != null &&
                    mWidgetArea.indexOfChild(widgetAreaHorizontalRecyclerItemsViewHolder.itemView) >= 0) {
                mWidgetArea.removeView(widgetAreaHorizontalRecyclerItemsViewHolder.itemView);
            }
            reloadWidget();
            loggedInStatus = mPresenter.checkIsLoggedIn();

            layout.setVisibility(View.VISIBLE);
            CommonUtils.fadeInView(layout, null);
        } else {
            gotoCheckoutSteps();
        }
    }

    private void gotoCheckoutSteps() {
        Controller controller;
        String tag;
        if (mPresenter.isTablet()) {
            controller = new CheckoutSplitController();
            tag = CheckoutSplitController.TAG;
        } else {
            controller = new CheckoutStepsController();
            tag = CheckoutStepsController.TAG;
        }

        getRouter().setRoot(RouterTransaction.with(controller)
                .tag(tag)
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showBestSellers(List<GetBestSellerResponse> getBestSellerResponses) {
        if (mWidgetArea == null) {
            return;
        }

        if (getBestSellerResponses == null || getBestSellerResponses.isEmpty()) {
            if (widgetAreaHorizontalRecyclerItemsViewHolder != null &&
                    mWidgetArea.indexOfChild(widgetAreaHorizontalRecyclerItemsViewHolder.itemView) >= 0) {
                mWidgetArea.removeView(widgetAreaHorizontalRecyclerItemsViewHolder.itemView);
            }
            return;
        }

        final List<SaleItemProduct> items = new ArrayList<>(getBestSellerResponses);

        HorizontalScrollingItemsAdapter adapter = new HorizontalScrollingItemsAdapter(items, true, false, true);
        adapter.setOnItemTappedListener((item, position, size) -> {
            lastBestSellerItemPosition = position;

            SaleItemDetailsController.Parameters.FromSaleItemProduct parameters = new SaleItemDetailsController.Parameters.FromSaleItemProduct(item);

            RouterTransaction routerTransaction = RouterTransaction
                    .with(SaleItemDetailsController.newInstance(parameters))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler());

            mActivity.getMainController().resetShopRouter();
            mActivity.getMainController().showShopController();
            mActivity.getMainController().getShopRouter().pushController(routerTransaction);
        });

        adapter.setOnPriceInfoTappedListener(item -> mPresenter.getPricingInfoText(item.getSeoIdentifier()));

        adapter.setWishlistListener(new HorizontalScrollingItemsAdapter.WishlistListener() {
            @Override
            public void addToWishlist(SaleItemProduct item) {
                final String productId = item.getId();
                CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback = () -> logWishlistEvent(productId, true);
                mPresenter.addProductToWishlist(item.getId(), item.getSeoIdentifier(), "", delayedCallback);
            }

            @Override
            public void removeFromWishlist(SaleItemProduct item) {
                final String productId = item.getId();
                CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback = () -> logWishlistEvent(productId, false);
                mPresenter.removeProductFromWishlist(item.getId(), delayedCallback);
            }

            @Override
            public boolean isProductInWishlist(SaleItemProduct item) {
                return mPresenter.isProductInWishlist(item.getId());
            }
        });

        final int orientation = ScreenUtils.getOrientation(mActivity);
        if (widgetAreaHorizontalRecyclerItemsViewHolder != null) {
            mWidgetArea.removeView(widgetAreaHorizontalRecyclerItemsViewHolder.itemView);
        }
        bestSellersWidgetHelper = new BestSellersWidgetHelper(adapter, mActivity, mPresenter.isTablet());
        widgetAreaHorizontalRecyclerItemsViewHolder = bestSellersWidgetHelper.createViewHolder(mWidgetArea, orientation);
        mWidgetArea.addView(widgetAreaHorizontalRecyclerItemsViewHolder.itemView);
        widgetAreaHorizontalRecyclerItemsViewHolder.onViewBound();
        bestSellersWidgetHelper.onBindViewHolder(widgetAreaHorizontalRecyclerItemsViewHolder, orientation);
    }

    @Override
    public void showRecentlyViewedItems(List<RecentlyViewedItemResponse> response) {
        if (mWidgetArea == null) {
            return;
        }

        if (response == null || response.isEmpty()) {
            if (widgetAreaHorizontalRecyclerItemsViewHolder != null &&
                    mWidgetArea.indexOfChild(widgetAreaHorizontalRecyclerItemsViewHolder.itemView) >= 0) {
                mWidgetArea.removeView(widgetAreaHorizontalRecyclerItemsViewHolder.itemView);
            }
            return;
        }

        final List<SaleItemProduct> items = new ArrayList<>(response);

        HorizontalScrollingItemsAdapter adapter = new HorizontalScrollingItemsAdapter(items, true, false, true);
        adapter.setOnItemTappedListener((item, position, size) -> {
            lastBestSellerItemPosition = position;

            SaleItemDetailsController.Parameters.FromSaleItemProduct parameters = new SaleItemDetailsController.Parameters.FromSaleItemProduct(item);

            RouterTransaction routerTransaction = RouterTransaction
                    .with(SaleItemDetailsController.newInstance(parameters))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler());

            mActivity.getMainController().resetShopRouter();
            mActivity.getMainController().showShopController();
            mActivity.getMainController().getShopRouter().pushController(routerTransaction);
        });

        adapter.setOnPriceInfoTappedListener(item -> mPresenter.getPricingInfoText(item.getSeoIdentifier()));

        adapter.setWishlistListener(new HorizontalScrollingItemsAdapter.WishlistListener() {
            @Override
            public void addToWishlist(SaleItemProduct item) {
                final String productId = item.getId();
                CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback = () -> logWishlistEvent(productId, true);
                mPresenter.addProductToWishlist(item.getId(), item.getSeoIdentifier(), "", delayedCallback);
            }

            @Override
            public void removeFromWishlist(SaleItemProduct item) {
                final String productId = item.getId();
                CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback = () -> logWishlistEvent(productId, false);
                mPresenter.removeProductFromWishlist(item.getId(), delayedCallback);
            }

            @Override
            public boolean isProductInWishlist(SaleItemProduct item) {
                return mPresenter.isProductInWishlist(item.getId());
            }
        });

        final int orientation = ScreenUtils.getOrientation(mActivity);
        if (widgetAreaHorizontalRecyclerItemsViewHolder != null) {
            mWidgetArea.removeView(widgetAreaHorizontalRecyclerItemsViewHolder.itemView);
        }
        recentlyViewedWidgetHelper = new RecentlyViewedWidgetHelper(adapter, mActivity, mPresenter.isTablet());
        widgetAreaHorizontalRecyclerItemsViewHolder = recentlyViewedWidgetHelper.createViewHolder(mWidgetArea, orientation);
        mWidgetArea.addView(widgetAreaHorizontalRecyclerItemsViewHolder.itemView);
        widgetAreaHorizontalRecyclerItemsViewHolder.onViewBound();
        recentlyViewedWidgetHelper.onBindViewHolder(widgetAreaHorizontalRecyclerItemsViewHolder, orientation);
    }

    @Override
    public void showPricingInfoText(String rrpText, Double totalPercentOff, Double originalPrice, String combinedPricingInfoText) {
        if (mActivity.getSupplierOriginalPriceInfoHelper() != null) {
            showItemPricingInfoView(
                    mActivity.getSupplierOriginalPriceInfoHelper()
                            .getOriginalPriceInfoWebViewContent(rrpText, totalPercentOff, originalPrice));
        } else {
            showItemPricingInfoView(combinedPricingInfoText);
        }
    }

    private void showItemPricingInfoView(String pricingInfoText) {
        if (layout == null) {
            return;
        }
        if (currentBottomPopupView != null) {
            currentBottomPopupView.setListener(null);
            currentBottomPopupView.dismiss(true);
        }
        final BottomPopupWebViewContentAdapter adapter = new BottomPopupWebViewContentAdapter();
        currentBottomPopupView = new BottomPopupView(layout, adapter);

        adapter.setWebViewContent(pricingInfoText);
        adapter.setOnCloseButtonClickListener(() -> currentBottomPopupView.dismiss(true));
        adapter.setWebViewClientOverrideUrlLoading(url -> {
            if (!url.contains("about:blank")) {
                ActivityLaunchUtil.launchActivity(mActivity, url);
            }
            return true;
        });
        currentBottomPopupView.setListener(new BottomPopupView.BottomPopupViewListener() {
            @Override
            public void willShow() {
                if (mGoToShopButton == null) {
                    return;
                }
                CommonUtils.fadeOutView(mGoToShopButton, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        mGoToShopButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mGoToShopButton.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onShow() {

            }

            @Override
            public void willDismiss() {

            }

            @Override
            public void onDismiss() {
                if (mGoToShopButton == null) {
                    return;
                }
                mGoToShopButton.setVisibility(View.VISIBLE);
                CommonUtils.fadeInView(mGoToShopButton, null);
            }
        });
        currentBottomPopupView.show(true);
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

    private void reloadWidget() {
        if (shouldReloadWidget() ||
                widgetAreaHorizontalRecyclerItemsViewHolder == null ||
                (mWidgetArea != null &&
                        mWidgetArea.indexOfChild(widgetAreaHorizontalRecyclerItemsViewHolder.itemView) < 0)) {
            if (mPresenter.checkIsLoggedIn()) {
                mPresenter.loadRecentlyViewedItems();
            } else {
                mPresenter.loadBestSellers("");
            }
            lastTimeWidgetReloaded = System.currentTimeMillis();
        }
    }

    private boolean shouldReloadWidget() {
        final long now = System.currentTimeMillis();
        final long elapsed = now - lastTimeWidgetReloaded;
        return elapsed >= WIDGET_AGE_THRESHOLD;
    }
}
