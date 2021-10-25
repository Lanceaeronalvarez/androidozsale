package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.mysale.genie.profiler.Profiler;
import com.mysale.genie.utility.RxBus;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.events.DeliveryPriceViewEventRequest;
import au.com.dealsdirect.data.network.model.events.ProductViewRequest;
import au.com.dealsdirect.data.network.model.events.RecentlyViewedEventRequest;
import au.com.dealsdirect.data.network.model.events.RecommendationEventRequest;
import au.com.dealsdirect.data.network.model.events.SellerLinkEventRequest;
import au.com.dealsdirect.data.network.model.events.WishlistEventRequest;
import au.com.dealsdirect.data.network.model.events.YouMayAlsoLikeEventRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetPostcodeShippingPriceResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetYouMayAlsoLikeResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.Attributes;
import au.com.dealsdirect.data.network.model.saleitemdetails.Personalisation;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecommendedItemsResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.SaleItemDetails;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.service.afterpay.AfterpayPanelViewHolder;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.EventRecommendedField;
import au.com.dealsdirect.service.datacollection.enums.EventTypeId;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPanel;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;
import au.com.dealsdirect.ui.controller.floatingimageviewer.FloatingImageViewerController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.priceblock.SaleItemProductPriceBlockHelper;
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.SaleDetailsImageListener;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.custom.ArcTranslateAnimation;
import au.com.dealsdirect.ui.custom.BottomPopupView;
import au.com.dealsdirect.ui.custom.BottomPopupWebViewContentAdapter;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.PersonalisationLayout;
import au.com.dealsdirect.ui.custom.transitions.ArcZoomChangeHandler;
import au.com.dealsdirect.utils.ActivityLaunchUtil;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.StringUtils;
import au.com.dealsdirect.utils.ViewUtils;
import au.com.dealsdirect.widget.ElasticDragDismissFrameLayout;
import butterknife.BindView;
import butterknife.OnClick;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;
import static au.com.dealsdirect.data.network.model.events.WishlistEventRequest.WishListInfo.ReferrerValue.PRODUCT_PAGE;

public class SaleItemDetailsController extends BaseController implements SaleItemDetailsMvpView {

    private final static int ACTIVITY_INDICATOR_DELAY = 2000; // milliseconds

    private final static boolean IS_DISCOUNT_POG_ENABLED = false;

    public abstract static class Parameters {
        private Parameters() {
        }

        public static final class FromProductList extends Parameters {
            private final String mSaleId;
            private final SaleItemProduct product;
            private final Integer mPosition;
            private final Drawable mLowResImageDrawable;
            private final String mImageURL;
            private final String mSalesOrigin;
            private final String mEndDate;

            public FromProductList(String saleId,
                                   SaleItemProduct product,
                                   Integer position,
                                   Drawable lowResImageDrawable,
                                   String imageURL,
                                   String salesOrigin,
                                   String endDate) {
                mSaleId = saleId;
                mPosition = position;
                mLowResImageDrawable = lowResImageDrawable;
                mImageURL = imageURL;
                mSalesOrigin = salesOrigin;
                mEndDate = endDate;
                this.product = product;
            }

            public String getSaleId() {
                return mSaleId;
            }

            public SaleItemProduct getProduct() {
                return product;
            }

            public Integer getPosition() {
                return mPosition;
            }

            public Drawable getLowResImageDrawable() {
                return mLowResImageDrawable;
            }

            public String getImageURL() {
                return mImageURL;
            }

            public String getSalesOrigin() {
                return mSalesOrigin;
            }

            public String getEndDate() {
                return mEndDate;
            }
        }

        // TODO: consider removing this and convert to loading with SaleItemProduct from Checkout
        public static final class FromCheckout extends Parameters {
            private final Integer mPosition;
            private final Drawable mLowResImageDrawable;
            private final String mImageURL;
            private final String mSeoIdentifierId;
            private final String mSkuId;
            private final String mSaleId;
            private final String mProductName;
            private final String mProductBrand;
            private final String mPrice;
            private final String mOldPrice;
            private final String mSalesOrigin;
            private final String mEndDate;
            private final boolean mIsFreeDelivery;
            private Boolean mIsSoldOut;

            public FromCheckout(Integer position,
                                Drawable lowResImageDrawable,
                                String imageURL,
                                String seoIdentifierId,
                                String skuId,
                                String saleId,
                                String productName,
                                String productBrand,
                                String price,
                                String oldPrice,
                                String salesOrigin,
                                String endDate,
                                boolean isFreeDelivery,
                                Boolean isSoldOut) {
                mPosition = position;
                mLowResImageDrawable = lowResImageDrawable;
                mImageURL = imageURL;
                mSeoIdentifierId = seoIdentifierId;
                mSkuId = skuId;
                mSaleId = saleId;
                mProductName = productName;
                mProductBrand = productBrand;
                mPrice = price;
                mOldPrice = oldPrice;
                mSalesOrigin = salesOrigin;
                mEndDate = endDate;
                mIsFreeDelivery = isFreeDelivery;
                mIsSoldOut = isSoldOut;
            }

            public Integer getPosition() {
                return mPosition;
            }

            public Drawable getLowResImageDrawable() {
                return mLowResImageDrawable;
            }

            public String getImageURL() {
                return mImageURL;
            }

            public String getSeoIdentifierId() {
                return mSeoIdentifierId;
            }

            public String getSkuId() {
                return mSkuId;
            }

            public String getSaleId() {
                return mSaleId;
            }

            public String getProductName() {
                return mProductName;
            }

            public String getProductBrand() {
                return mProductBrand;
            }

            public String getPrice() {
                return mPrice;
            }

            public String getOldPrice() {
                return mOldPrice;
            }

            public String getSalesOrigin() {
                return mSalesOrigin;
            }

            public String getEndDate() {
                return mEndDate;
            }

            public boolean getIsFreeDelivery() {
                return mIsFreeDelivery;
            }

            public Boolean isSoldOut() {
                return mIsSoldOut;
            }

            public void setIsSoldOut(Boolean isSoldOut) {
                mIsSoldOut = isSoldOut;
            }
        }

        public static final class FromDeepLink extends Parameters {
            private final String mSeoIdentifierId;
            private final String mSkuId;

            public FromDeepLink(String seoIdentifierId,
                                String skuId) {
                mSeoIdentifierId = seoIdentifierId;
                mSkuId = skuId;
            }

            public String getSeoIdentifierId() {
                return mSeoIdentifierId;
            }

            public String getSkuId() {
                return mSkuId;
            }
        }
    }

    private final static int PERSONALIZATION_SHAKE_DELAY = 300; //milliseconds

    @Inject
    SaleItemDetailsMvpPresenter<SaleItemDetailsMvpView> mPresenter;

    private SaleItemDetails currentItem = null;
    // TODO: store SaleItemProduct instead of these
    private String mSaleId;
    private String mSkuId;
    private Attributes mAttributes = null;
    private String mItemImageUrl;
    private Drawable mItemLowResImageDrawable = null;
    private String mSeoIdentifierId;
    private String mSaleName;
    private String mSalePrice;
    private String mSaleOldPrice;
    private String mBrandName;
    private String mSupplierId;
    private Ourpay mOurpay;
    private List<SaleItemDetails> mSkuVariants = new ArrayList<>();
    private String mEndDate;
    private boolean mIsFreeDelivery;
    private CountDownTimer mCountDownTimer;

    private boolean shouldAfterpayDetailsBeVisible = false;

    List<GetYouMayAlsoLikeResponse> mYouMayAlsoLikeList = new ArrayList<>();
    List<RecommendedItemsResponse> mRecommendedList = new ArrayList<>();

    @BindView(R.id.share_right)
    ImageView mLikeButton;
    @BindView(R.id.toolbar_right_view)
    ImageView mLikeFloatingButton;
    @BindView(R.id.productImageRecyclerView)
    RecyclerView mProductImagesRv;
    @BindView(R.id.otherImagesRecyclerView)
    RecyclerView mOtherImagesRv;
    @BindView(R.id.productName)
    TextView mProductName;
    @BindView(R.id.productBrand)
    TextView mProductBrand;

    @BindView(R.id.product_details_sizes_container)
    ViewGroup mSizesContainer;
    @BindView(R.id.product_details_size_list)
    TagFlowLayout mSizesFlowLayout;
    @BindView(R.id.product_details_size_notice)
    TextView mSizesNotSelectedNotice;
    @BindView(R.id.product_details_personalisation_layout)
    PersonalisationLayout mPersonalisationLayout;
    @BindView(R.id.product_details_shipping_desc_container)
    ViewGroup mShippingContainer;
    @BindView(R.id.product_details_shipping_postcode_preview_price)
    TextView mShippingPreviewPrice;
    @BindView(R.id.product_details_shipping_postcode_input)
    EditText mShippingPostcodeInput;
    @BindView(R.id.product_details_shipping_not_available)
    TextView mShippingPostcodeNotAvailable;
    @BindView(R.id.product_details_shipping_postcode_header)
    TextView mShippingPostcodeHeader;
    @BindView(R.id.product_details_shipping_postcode_container)
    ViewGroup mShippingPostcodeContainer;
    @BindView(R.id.product_details_shipping_desc_header)
    TextView mShippingDescHeaderText;
    @BindView(R.id.product_details_shipping_desc_webview)
    WebView mShippingDescText;
    @BindView(R.id.product_details_shipping_postcode_activity_indicator)
    ProgressBar mShippingCalculateActivityIndicator;
    @BindView(R.id.product_details_shipping_postcode_button)
    Button mShippingCalculateButton;
    @BindView(R.id.product_description_text)
    WebView mProductDescriptionText;
    @BindView(R.id.product_about_pricing_text)
    WebView mProductAboutPricing;
    @BindView(R.id.product_about)
    WebView mProductAboutText;
    @BindView(R.id.product_details_return_policy_text)
    WebView mReturnPolicyText;
    @BindView(R.id.product_details_shared_image)
    ImageView mProductSharedImage;
    @BindView(R.id.product_details_coordinator)
    CoordinatorLayout mProductCoordinatorLayout;
    @BindView(R.id.bottom_card)
    ViewGroup mProductDetailBottomCard;
    @BindView(R.id.product_details_name_price_container)
    ViewGroup mProductPriceCategory;
    @BindView(R.id.about_pricing_container)
    ViewGroup mProductPricingContainer;
    @BindView(R.id.product_about_container)
    ViewGroup mProductAboutContainer;
    @BindView(R.id.product_details_return_policy_container)
    ViewGroup mReturnPolicyContainer;
    @BindView(R.id.partial_item_details_ourpay_panel_holder)
    ViewGroup mOurpayHolder;
    @BindView(R.id.partial_item_details_afterpay_panel_holder)
    ViewGroup mAfterpayHolder;
    @BindView(R.id.controller_sale_item_detail_scrollview)
    NestedScrollView mProductDetailScrollView;
    @BindView(R.id.product_details_add_to_basket_container)
    ViewGroup mAddToCartButtonContainer;
    @BindView(R.id.product_details_add_to_basket)
    Button mAddToCartButton;
    @BindView(R.id.product_details_add_to_basket_progress_dialog)
    ProgressBar mAddToCartProgressBar;
    @BindView(R.id.product_details_button_overlay)
    ImageView mAddToCartOverlay;

    @BindView(R.id.controller_image_frame_layout)
    ViewGroup mProductDetailsImageLayout;
    @BindView(R.id.controller_sale_details_toolbar)
    ViewGroup mProductDetailsToolbar;
    @BindView(R.id.controller_product_details_title_description)
    ViewGroup mProductDetailsTitleLayout;
    @BindView(R.id.toolbar_item_brand)
    TextView mToolbarItemBrandTextView;
    @BindView(R.id.toolbar_item_name)
    TextView mToolbarItemNameTextView;
    @BindView(R.id.product_about_old_pricing_text)
    WebView mOldProductPricing;
    @BindView(R.id.main_layout)
    ViewGroup mMainContentLayout;
    @BindView(R.id.product_details_image_animate)
    ImageView mImageViewToAnimate;
    @BindView(R.id.product_details_add_to_basket_timer)
    ViewGroup mAddToCartTimer;
    @BindView(R.id.product_details_add_to_basket_timer_text_view)
    TextView mAddToCartTimerTextView;
    @BindView(R.id.product_details_add_to_basket_timer_progress_dialog)
    ProgressBar mAddToCartTimerProgressBar;
    @BindView(R.id.product_details_timer)
    TextView mTimerTextView;
    @BindView(R.id.product_details_free_delivery)
    ImageView mFreeDeliveryImageView;
    @BindView(R.id.product_details_percent_off)
    TextView mProductDiscountPogTextView;
    @BindView(R.id.controller_product_details_button_container)
    ViewGroup mProductDetailsButtonContainer;
    @BindView(R.id.controller_product_details_like_recyclerview)
    RecyclerView mYouMayAlsoLikeRecyclerview;
    @BindView(R.id.controller_product_details_like_container)
    View mYouMayAlsoLikeContainer;
    @BindView(R.id.controller_product_details_recommended_recyclerview)
    RecyclerView mRecommendedRecyclerView;
    @BindView(R.id.controller_product_details_recommended_container)
    View mRecommendedContainer;
    @BindView(R.id.controller_product_details_recently_view_recyclerview)
    RecyclerView mRecentlyViewedRecyclerView;
    @BindView(R.id.controller_product_details_recently_viewed_container)
    ViewGroup mRecentlyViewedContainer;

    @BindView(R.id.product_details_sold_out)
    TextView mSoldOutView;

    @BindView(R.id.price_block_container)
    ViewGroup priceBlockContainer;

    @BindView(R.id.product_seller_container)
    ViewGroup sellerContainer;
    @BindView(R.id.product_seller_text)
    TextView sellerTextView;

    @BindView(R.id.product_details_buybox_container)
    ViewGroup buyboxContainer;
    @BindView(R.id.product_details_buybox_header_container)
    ViewGroup buyBoxHeaderContainer;
    @BindView(R.id.product_details_buybox_title)
    TextView buyBoxTitleTextView;
    @BindView(R.id.product_details_buybox_minimize)
    ImageView buyBoxMinimizeButtonView;
    @BindView(R.id.product_details_buybox_items)
    RecyclerView buyBoxItemsRecyclerView;

    int[] mSharedImageLocation;

    public static final String TAG = SaleItemDetailsController.class.getSimpleName();

    private static final int SPANNABLE_STRING_START_INDEX = 6;
    private static final float DISCOUNT_VALUE_SCALE_FACTOR = 1.8f;

    LinearLayoutManager mProductImagesRvLayoutManager;

    private String mHtmlHeader = "";
    private String mHtmlFooter = "";

    private ArrayList<Pair<String, String>> mProductSizes = new ArrayList<>();

    private boolean mImagesLoaded = false;

    private boolean mHasSizes = false;
    private int mSelectedSizeIndex = -1;
    private boolean mAllowSelectingSoldoutSizes = false;
    private int mFromPosition = -1;
    private int mToolbarVerticalOffset;
    private boolean mIsSoldOutCombined = true;
    private int mAttempts = 0;
    private String mProductId;
    private String mMasterProductId;

    private boolean hasLoadedDetails = false;
    private boolean isAddToBasketInputBuffered = false;

    private Boolean mIsSoldout = null;

    private String mSizeGuideLink = null;

    //default sales origin
    private String mOrigin = DataCollector.EventParameters.ViewSource.SALE;

    int[] mCheckoutLocation = new int[2];
    boolean isAnimating = false;
    boolean willViewDisappear = false;

    ElasticDragDismissFrameLayout mRootView;
    View mCheckoutView;
    AHBottomNavigation mBottomNavView;

    private boolean mHasSavedInstance = false;

    private int mCarouselPosition = 0;
    private static final int CAROUSEL_VELOCITY_THRESHOLD = 100;

    private ElasticDragDismissFrameLayout.ElasticDragDismissCallback mDragDismissListener;

    private Map<String, String> mSavedPersonalizationData;

    private Handler addToCartDelayHandler;
    private Runnable addToCartDelayRunnable;

    private boolean hasLoadedPostcodeForm = false;

    private SaleItemProductPriceBlockHelper priceBlockHelper = null;

    private SaleItemProduct partialProductDetailsToShow = null;

    private SaleItemDetailsHorizontalScrollingItemsHelper recommendedItemsHelper = null;
    private SaleItemDetailsHorizontalScrollingItemsHelper youMayAlsoLikeHelper = null;
    private SaleItemDetailsHorizontalScrollingItemsHelper recentlyViewedHelper = null;

    private final Map<String, String> rrpTextCache = new HashMap<>();
    private final Map<String, String> pricingTextCache = new HashMap<>();
    private OnLoadProductDetails onLoadProductDetails = null;

    private BottomPopupView currentBottomPopupView = null;

    private interface OnLoadProductDetails {
        void onLoad(SaleItemDetails saleDetails);
    }

    final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new
            ViewTreeObserver.OnScrollChangedListener() {

                @Override
                public void onScrollChanged() {
                    if (mProductDetailScrollView != null) {
                        SaleItemDetailsController.this.onScrollChanged(mProductDetailScrollView.getScrollY());
                    }
                }
            };

    final private HorizontalScrollingItemsAdapter.WishlistListener horizontalItemsWishlistListener = new HorizontalScrollingItemsAdapter.WishlistListener() {
        @Override
        public void addToWishlist(SaleItemProduct item) {
            SaleItemDetailsMvpPresenter.WishlistDelayedCallback delayedCallback = () -> {
                logWishlistEvent(mProductId, true);
            };
            mPresenter.addProductToWishlist(item.getId(), item.getSeoIdentifier(), getMasterProductId(item), delayedCallback);
        }

        @Override
        public void removeFromWishlist(SaleItemProduct item) {
            SaleItemDetailsMvpPresenter.WishlistDelayedCallback delayedCallback = () -> {
                logWishlistEvent(mProductId, false);
            };
            mPresenter.removeProductFromWishlist(item.getId(), delayedCallback);
        }

        @Override
        public boolean isProductInWishlist(SaleItemProduct item) {
            return mPresenter.isProductInWishlist(item.getId());
        }
    };

    public SaleItemDetailsController(
            String seoIdentifierId, String imageUrl, String skuId, String saleId) {
        this(new BundleBuilder(new Bundle())
                .putString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_IMAGE_ID, imageUrl)
                .putString(BundleKeys.SALEITEMDETAILS_KEY_SEO_IDENTIFIER_ID, seoIdentifierId)
                .putString(BundleKeys.SALEITEMDETAILS_KEY_SKU_ID, skuId)
                .putString(BundleKeys.SALEITEMDETAILS_KEY_SALE_ID, saleId)
                .build());
    }

    public static SaleItemDetailsController newInstance(Bundle bundle) {
        return new SaleItemDetailsController(bundle);
    }

    public static SaleItemDetailsController newInstance(Parameters parameters) {
        SaleItemDetailsController controller = new SaleItemDetailsController(
                new BundleBuilder(new Bundle()).build());

        if (parameters instanceof Parameters.FromCheckout) {
            controller.mSaleId = ((Parameters.FromCheckout) parameters).getSaleId();
            controller.mSkuId = ((Parameters.FromCheckout) parameters).getSkuId();
            controller.mItemLowResImageDrawable = ((Parameters.FromCheckout) parameters).getLowResImageDrawable();
            controller.mItemImageUrl = ((Parameters.FromCheckout) parameters).getImageURL();
            controller.mSeoIdentifierId = ((Parameters.FromCheckout) parameters).getSeoIdentifierId();
            controller.mSaleName = ((Parameters.FromCheckout) parameters).getProductName();
            controller.mBrandName = ((Parameters.FromCheckout) parameters).getProductBrand();
            controller.mSalePrice = ((Parameters.FromCheckout) parameters).getPrice();
            controller.mSaleOldPrice = ((Parameters.FromCheckout) parameters).getOldPrice();
            controller.mFromPosition = ((Parameters.FromCheckout) parameters).getPosition();
            String origin = ((Parameters.FromCheckout) parameters).getSalesOrigin();
            controller.mEndDate = ((Parameters.FromCheckout) parameters).getEndDate();
            controller.mIsFreeDelivery = ((Parameters.FromCheckout) parameters).getIsFreeDelivery();
            controller.mOrigin = origin != null ? origin : DataCollector.EventParameters.ViewSource.SALE;
            controller.mIsSoldout = ((Parameters.FromCheckout) parameters).isSoldOut();
        } else if (parameters instanceof Parameters.FromProductList) {
            controller.mSaleId = ((Parameters.FromProductList) parameters).getSaleId();
            controller.mItemLowResImageDrawable = ((Parameters.FromProductList) parameters).getLowResImageDrawable();
            controller.mItemImageUrl = ((Parameters.FromProductList) parameters).getImageURL();
            controller.mFromPosition = ((Parameters.FromProductList) parameters).getPosition();
            String origin = ((Parameters.FromProductList) parameters).getSalesOrigin();
            controller.mEndDate = ((Parameters.FromProductList) parameters).getEndDate();
            controller.mOrigin = origin != null ? origin : DataCollector.EventParameters.ViewSource.SALE;
            controller.partialProductDetailsToShow = ((Parameters.FromProductList) parameters).getProduct();
        } else if (parameters instanceof Parameters.FromDeepLink) {
            controller.mSeoIdentifierId = ((Parameters.FromDeepLink) parameters).getSeoIdentifierId();
            controller.mSkuId = ((Parameters.FromDeepLink) parameters).getSkuId();
            controller.mOrigin = DataCollector.EventParameters.ViewSource.SALE;
        }

        return controller;
    }

    public SaleItemDetailsController(Bundle args) {
        super(args);
        mSaleId = args.getString(BundleKeys.SALEITEMDETAILS_KEY_SALE_ID);
        mSkuId = args.getString(BundleKeys.SALEITEMDETAILS_KEY_SKU_ID, "");
        mItemImageUrl = args.getString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_IMAGE_ID);
        mSeoIdentifierId = args.getString(BundleKeys.SALEITEMDETAILS_KEY_SEO_IDENTIFIER_ID);
        mSaleName = args.getString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_NAME);
        mSalePrice = args.getString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_PRICE);
        mSaleOldPrice = args.getString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_OLD_PRICE);
        mFromPosition = args.getInt(BundleKeys.SALEITEMDETAILS_KEY_POSITION);
        mOrigin = args.getString(BundleKeys.SALEITEMDETAILS_KEY_SALE_ORIGIN, DataCollector.EventParameters.ViewSource.SALE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BundleKeys.SALEITEMDETAILS_KEY_SALE_ID, mSaleId);
        outState.putString(BundleKeys.SALEITEMDETAILS_KEY_SKU_ID, mSkuId);
        outState.putString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_IMAGE_ID, mItemImageUrl);
        outState.putString(BundleKeys.SALEITEMDETAILS_KEY_SEO_IDENTIFIER_ID, mSeoIdentifierId);
        outState.putString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_NAME, mSaleName);
        outState.putString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_PRICE, mSalePrice);
        outState.putString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_OLD_PRICE, mSaleOldPrice);
        outState.putInt(BundleKeys.SALEITEMDETAILS_KEY_POSITION, mFromPosition);
        outState.putString(BundleKeys.SALEITEMDETAILS_KEY_SALE_ORIGIN, mOrigin);
        outState.putString(BundleKeys.SALEITEMDETAILS_KEY_END_DATE, mEndDate);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSaleId = savedInstanceState.getString(BundleKeys.SALEITEMDETAILS_KEY_SALE_ID);
        mSkuId = savedInstanceState.getString(BundleKeys.SALEITEMDETAILS_KEY_SKU_ID);
        mItemImageUrl = savedInstanceState.getString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_IMAGE_ID);
        mSeoIdentifierId = savedInstanceState.getString(BundleKeys.SALEITEMDETAILS_KEY_SEO_IDENTIFIER_ID);
        mSaleName = savedInstanceState.getString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_NAME);
        mSalePrice = savedInstanceState.getString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_PRICE);
        mSaleOldPrice = savedInstanceState.getString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_OLD_PRICE);
        mFromPosition = savedInstanceState.getInt(BundleKeys.SALEITEMDETAILS_KEY_POSITION);
        mOrigin = savedInstanceState.getString(BundleKeys.SALEITEMDETAILS_KEY_SALE_ORIGIN);
        mEndDate = savedInstanceState.getString(BundleKeys.SALEITEMDETAILS_KEY_END_DATE);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
    }


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_product_details, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity.getProfiler().setStartLogTime(DataCollector.EventParameters.CustomEventType.CV_ITEMDETAILS.getValue());
        hasLoadedPostcodeForm = false;
        setUp(view);
        getPriceBlockHelper().setFreeDeliveryTextViewText(null);
        if (partialProductDetailsToShow != null) {
            setupPartialProductDetails(partialProductDetailsToShow);
            if (partialProductDetailsToShow.isFreeDelivery()) {
                getPriceBlockHelper().setFreeDeliveryTextViewText(getFreeShippingSpan());
            }
        }
        if (currentItem != null) {
            setupProductDetails(currentItem);
        }
        if (mIsSoldout != null) {
            showAddToCartButton();
        }
    }

    @Override
    public void onDetach(View view) {
        mSavedPersonalizationData = mPersonalisationLayout.getDataForAddToCart();

        if (recentlyViewedHelper != null) {
            recentlyViewedHelper.onRecyclerViewDetach();
        }
        if (youMayAlsoLikeHelper != null) {
            youMayAlsoLikeHelper.onRecyclerViewDetach();
        }
        if (recommendedItemsHelper != null) {
            recommendedItemsHelper.onRecyclerViewDetach();
        }
        if (priceBlockHelper != null) {
            priceBlockHelper = null;
        }

        super.onDetach(view);
    }

    @Override
    public void onOrientationChanged(Configuration newConfiguration) {
        mProductDetailScrollView.scrollTo(0, 0);
        stretchImageView();
        if (mOurpay != null) {
            showMyPayDetails(null, mOurpay);
        }
        showYouMayAlsoLike();
    }

    @Override
    public void onViewWillAppear(Controller previousController) {
        super.onViewWillAppear(previousController);
        willViewDisappear = false;
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);

        if (!isViewAttached()) {
            return;
        }

        mLikeButton.setVisibility(View.INVISIBLE);
        mLikeFloatingButton.setVisibility(View.INVISIBLE);
        loadProductDetails(mSaleId, mSeoIdentifierId);
        if (mHasSavedInstance) {
            mActivity.getMainController().setSavedCurrentItem();
        }
    }

    @Override
    public void onViewWillDisappear(Controller nextController) {
        super.onViewWillDisappear(nextController);
        willViewDisappear = true;

        if (currentBottomPopupView != null) {
            currentBottomPopupView.dismiss(true);
        }

        stopDelayedProgressBar();

        if (!isViewAttached()) {
            return;
        }

        mProductDetailsToolbar.clearAnimation();
    }

    @Override
    public void refreshContents() {
        super.refreshContents();

        if (!hasLoadedDetails) {
            loadProductDetails(mSaleId, mSeoIdentifierId);
        }
        stretchImageView();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setUp(View view) {

        mShippingDescText.getSettings().setTextZoom(100);
        mProductDescriptionText.getSettings().setTextZoom(100);
        mProductAboutPricing.getSettings().setTextZoom(100);
        mProductAboutText.getSettings().setTextZoom(100);
        mReturnPolicyText.getSettings().setTextZoom(100);
        mOldProductPricing.getSettings().setTextZoom(100);

        if (view instanceof ElasticDragDismissFrameLayout) {
            mRootView = ((ElasticDragDismissFrameLayout) view);

            mRootView.setDragActivationAreaWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            mRootView.setDragActivationAreaHeight(Float.MAX_VALUE);
            mRootView.setDragDismissScale(0.40f);
            mRootView.setDragVerticalThreshold(8);
        }

        stretchImageView();

        if (mBrandName == null || mBrandName.isEmpty()) {
            mProductBrand.setText(Html.fromHtml("<u>" + mSaleName + "</u>"));
            mProductName.setText("");
        } else {
            mProductBrand.setText(Html.fromHtml("<u>" + mBrandName + "</u>"));
            mProductName.setText(mSaleName);
        }

        mProductDetailBottomCard.setVisibility(View.VISIBLE);

        //noinspection ConstantConditions
        mDragDismissListener
                = new ElasticDragDismissFrameLayout.ElasticDragDismissCallback() {
            @Override
            public void onDrag(float elasticOffset, float elasticOffsetPixels,
                               float rawOffset, float rawOffsetPixels) {
                mActivity.getMainController().setShouldBottomNavigationViewEnabled(false);
            }

            @Override
            public void onDragDismissed() {
                mActivity.getMainController().setShouldBottomNavigationViewEnabled(true);
                mProductDetailScrollView.scrollTo(0, 0);
                mActivity.onBackPressed();
            }

            @Override
            public void onCancel() {
                mActivity.getMainController().setShouldBottomNavigationViewEnabled(true);
            }
        };
        if (mRootView != null) {
            mRootView.addListener(mDragDismissListener);
        }

        mProductSharedImage.setTransitionName(getResources().getString(R.string.transition_sale_image_indexed, mFromPosition));

        if (mItemLowResImageDrawable != null) {
            ImageUtils.loadImageWithPlaceholder(mItemImageUrl, mProductSharedImage, mItemLowResImageDrawable, null);
            mItemLowResImageDrawable = null;
        } else {
            ImageUtils.loadImageImmediate(mItemImageUrl, mProductSharedImage, null);
        }

        mOtherImagesRv.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        SaleItemDetailsImageAdapter mSaleItemImagesIndicatorAdapter = new SaleItemDetailsImageAdapter(
                new ArrayList<>(),
                saleDetailsImageListener());
        mOtherImagesRv.setAdapter(mSaleItemImagesIndicatorAdapter);
        mOtherImagesRv.setVisibility(View.INVISIBLE);

        mProductImagesRvLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mProductImagesRv.setLayoutManager(mProductImagesRvLayoutManager);

        ArrayList<View> toggledViews = new ArrayList<View>() {{
            add(mOtherImagesRv);
            add(mProductPriceCategory);
            add(mAddToCartButton);
            add(mOurpayHolder);
            add(mAfterpayHolder);
            add(mProductDetailBottomCard);
            add(mAddToCartOverlay);
            add(mMainContentLayout);
        }};

        if (mPresenter.isTablet()) toggledViews.add(mAddToCartOverlay);

        SaleItemDetailsImageAdapter mSaleItemImagesAdapter = new SaleItemDetailsImageAdapter(
                new ArrayList<>(),
                saleDetailsImageListener());
        mProductImagesRv.setAdapter(mSaleItemImagesAdapter);
        mProductImagesRv.setEnabled(false);
        mProductImagesRv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LinearSnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mProductImagesRv);
        mProductImagesRv.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                int size = mSaleItemImagesAdapter.getItemCount();
                if (Math.abs(velocityX) > CAROUSEL_VELOCITY_THRESHOLD && size > 0) {
                    int position = mCarouselPosition + (int) Math.signum(velocityX);
                    position = Ints.constrainToRange(position, 0, size - 1);
                    mProductImagesRv.smoothScrollToPosition(position);
                    updateCarouselPageIndicator(position);
                    mRootView.setIsHorizontalDismissEnabled(
                            mProductDetailScrollView.getScrollY() <= 0
                                    && position == 0);
                }
                return false;
            }
        });
        mProductImagesRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        updateCarouselPageIndicator(getCarouselPosition());
                        mRootView.setIsHorizontalDismissEnabled(false);
                        mRootView.setIsVerticalDismissEnabled(false);
                        break;
                    default:
                        mRootView.setIsHorizontalDismissEnabled(
                                mProductDetailScrollView.getScrollY() <= 0
                                        && getCarouselPosition() == 0);
                        mRootView.setIsVerticalDismissEnabled(mProductDetailScrollView.getScrollY() <= 0);
                        break;
                }
                return false;
            }
        });

        mHtmlHeader = StringUtils.applyStyleToCSS(new StringUtils.CSSStyle() {
            @Override
            public String getBodyFontName() {
                return StringUtils.typeFaceFamilyFromFilename(
                        mActivity.getResources().getString(R.string.font_app_regular));
            }

            @Override
            public String getBodyFontColor() {
                String hex = Integer.toHexString(
                        mActivity.getResources().getColor(R.color.text_extra_dark));
                if (hex.length() > 6) {
                    hex = hex.substring(2);
                }
                return "#" + hex;
            }

            @Override
            public String getBoldFontName() {
                return StringUtils.typeFaceFamilyFromFilename(
                        mActivity.getResources().getString(R.string.font_app_regular));
            }

            @Override
            public String getBoldFontColor() {
                String hex = Integer.toHexString(
                        mActivity.getResources().getColor(R.color.text_extra_dark));
                if (hex.length() > 6) {
                    hex = hex.substring(2);
                }
                return "#" + hex;
            }
        }, mActivity.getResources()
                .getString(R.string.base_html_template_header));

        mHtmlFooter = mActivity.getResources()
                .getString(R.string.base_html_template_footer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mProductDetailScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    onScrollChanged(scrollY);
                }
            });
        } else {
            mProductDetailScrollView.setOnTouchListener(new View.OnTouchListener() {
                private ViewTreeObserver observer;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (observer == null) {
                        observer = mProductDetailScrollView.getViewTreeObserver();
                        observer.addOnScrollChangedListener(onScrollChangedListener);
                    } else if (!observer.isAlive()) {
                        observer.removeOnScrollChangedListener(onScrollChangedListener);
                        observer = mProductDetailScrollView.getViewTreeObserver();
                        observer.addOnScrollChangedListener(onScrollChangedListener);
                    }

                    return false;
                }
            });
        }

        mSoldOutView.setVisibility(mIsSoldout != null && mIsSoldout ? View.VISIBLE : View.GONE);

        mProductBrand.setOnClickListener(v -> {
            gotoProductListWithSearchQuery(mProductBrand.getText().toString());
        });

        mShippingPostcodeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.setDefaultPostcode(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mShippingPostcodeContainer.setVisibility(View.GONE);
        mShippingPostcodeNotAvailable.setVisibility(View.GONE);
        mShippingPreviewPrice.setText(null);
        mShippingPreviewPrice.setVisibility(View.GONE);
    }

    private void gotoProductListWithSearchQuery(String searchKey) {
        final SaleItemsController.Parameters.FromShopSearch parameters = new SaleItemsController.Parameters
                .FromShopSearch(null, searchKey);

        gotoProductListWithParameters(parameters);
    }

    private void gotoProductListWithParameters(SaleItemsController.Parameters parameters) {
        final SaleItemsController saleItemsController = SaleItemsController.newInstance(parameters);

        final Controller controller = getRouter().getControllerWithTag(getResources().getString(R.string.sale_items_controller_tag));
        if (controller != null) {
            getRouter().popController(controller);
            getRouter().replaceTopController(RouterTransaction.with(saleItemsController)
                    .tag(getResources().getString(R.string.sale_items_controller_tag))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        } else {
            getRouter().popToRoot(new ArcZoomChangeHandler());
            getRouter().pushController(RouterTransaction.with(saleItemsController)
                    .tag(getResources().getString(R.string.sale_items_controller_tag))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        }

        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
    }

    private void setupSaleRemainingTime(String endDate) {
        mCountDownTimer = new CountDownTimer(DateUtils.getRemainingTimeInMillis(endDate), DateUtils.DATE_UTIL_MILLIS_TO_SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isViewAttached() && mTimerTextView != null) {
                    mTimerTextView.setText(DateUtils.getRemainingTimeValue(millisUntilFinished));
                }
            }

            @Override
            public void onFinish() {
                if (mAddToCartTimer != null) {
                    mAddToCartTimer.setVisibility(View.GONE);
                }
                if (mAddToCartButtonContainer != null) {
                    mAddToCartButtonContainer.setVisibility(View.VISIBLE);
                }
                if (mAddToCartButton != null) {
                    mAddToCartButton.setEnabled(true);
                    mAddToCartButton.bringToFront();
                }
            }
        };
        mCountDownTimer.start();
    }

    private void stretchImageView() {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) mProductDetailsImageLayout.getLayoutParams();
        int bottomNavHeight = mActivity.getMainController().getBottomNav().getHeight();

        int screenAllowanceSize = mPresenter.isTablet() ? bottomNavHeight * 3 : bottomNavHeight * 2 + (int) getDimension(R.dimen.margin_extra_small);
        int mDefaultHeight = ScreenUtils.getScreenHeight(mActivity) - screenAllowanceSize;

        lp.height = mDefaultHeight;
        mProductDetailsImageLayout.setLayoutParams(lp);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        KeyboardUtils.hideSoftInput(mActivity);
        if (!mActivity.isDestroyed()) {
            ImageUtils.clearImage(mProductSharedImage);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mProductDetailScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) null);
        } else {
            mProductDetailScrollView.setOnTouchListener(null);
        }
        mProductImagesRv.setOnFlingListener(null);
        mProductImagesRv.setOnTouchListener(null);
        mProductImagesRv.setLayoutManager(null);
        mProductImagesRv.setAdapter(null);
        mOtherImagesRv.setAdapter(null);
//      not setting this to null may cause leak, but the library doesn't support setting this to null
//      mSizesFlowLayout.setAdapter(null);
        mSizesFlowLayout.setOnSelectListener(null);
        mProductDescriptionText.setWebViewClient(null);
        mReturnPolicyText.setWebViewClient(null);
        if (mCountDownTimer != null) mCountDownTimer.cancel();
        super.onDestroyView(view);
    }

    private void updatePriceDetails(SaleItemDetails saleDetail) {
        updatePriceDetails(saleDetail, true);
    }

    private void updatePriceDetails(SaleItemDetails saleDetail, boolean includePartial) {
        if (includePartial) {
            updatePartialPriceDetails(saleDetail);
        }

        //update Ourpay
        mPresenter.loadOurpayData(saleDetail);

        //update Afterpay
        mPresenter.loadAfterpayData(saleDetail.getPrice().getValue());

        mPresenter.loadPromoInfo(saleDetail.getSkuId());

        String personalisation = saleDetail.getPersonalisation();
        if (personalisation != null) {
            Map<String, String> data = mPersonalisationLayout.getDataForAddToCart();
            mPersonalisationLayout.inflateForProductDetails(mActivity, new Gson().fromJson(
                    personalisation, Personalisation.class));
            mPersonalisationLayout.populateFieldsWithDataFromAddToCart(data);
        } else {
            mPersonalisationLayout.setVisibility(View.GONE);
        }
    }

    private void updatePartialPriceDetails(SaleItemProduct saleDetail) {
        getPriceBlockHelper().setup(saleDetail, mActivity.getSupplierOriginalPriceInfoHelper() != null, false);

        //update Images
        final List<String> qualitySaleImages = getQualityImages(saleDetail.getImages());
        if (mProductImagesRv.getAdapter() instanceof SaleItemDetailsImageAdapter) {
            ((SaleItemDetailsImageAdapter) mProductImagesRv.getAdapter()).replaceData(qualitySaleImages);
        }
        if (mOtherImagesRv.getAdapter() instanceof SaleItemDetailsImageAdapter) {
            ((SaleItemDetailsImageAdapter) mOtherImagesRv.getAdapter()).replaceData(qualitySaleImages);
        }
    }

    @Override
    public void showProductDetails(SaleItemDetails saleDetail) {
        currentItem = saleDetail;
        rrpTextCache.put(saleDetail.getProductId(), saleDetail.getRrpText());
        pricingTextCache.put(saleDetail.getProductId(), saleDetail.getPricing());
        if (onLoadProductDetails != null) {
            onLoadProductDetails.onLoad(saleDetail);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupProductDetails(SaleItemDetails saleDetail) {
        if (willViewDisappear) {
            return;
        }

        setupPartialProductDetails(saleDetail);

        mPresenter.loadRecentlyViewedItems();

        mPresenter.loadYouMayAlsoLike(saleDetail.getAttributes().getProductId());

        // Disabled recommended items
//        mPresenter.loadRecommendedItems();

        mProductId = saleDetail.getProductId();
        mMasterProductId = saleDetail.getAttributes().getProductId();

        mSkuId = saleDetail.getSkuId();
        mAttributes = saleDetail.getAttributes();

        mSupplierId = saleDetail.getSupplier();

        // set product view request object for genie event
        ProductViewRequest productViewRequest = new ProductViewRequest();
        productViewRequest.setEventType(EventTypeId.EVENT_PRODUCTVIEW);

        ProductViewRequest.SkuInfo skuInfo = new ProductViewRequest.SkuInfo();
        skuInfo.setId(saleDetail.getSkuId());
        skuInfo.setName(saleDetail.getName());

        productViewRequest.setSkuInfo(skuInfo);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.MILLISECONDS,
                Profiler.getTotalTime(DataCollector.EventParameters.CustomEventType.CV_ITEMDETAILS.getValue()));
        parameters.put(DataCollector.EventParameters.PRODUCT_VIEW_REQUEST, productViewRequest);
        parameters.put(DataCollector.EventParameters.ITEM_ID, saleDetail.getSkuId());
        parameters.put(DataCollector.EventParameters.ITEM_NAME, saleDetail.getName());
        parameters.put(DataCollector.EventParameters.PRICE, saleDetail.getPrice().getValue());
        parameters.put(DataCollector.EventParameters.COUNTRY_ID, Settings.getSelectedCountry().countryId);
        parameters.put(DataCollector.EventParameters.ITEM_BRAND, saleDetail.getBrandName());
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, SaleItemDetailsController.class.getSimpleName());
        DataCollector.logEvent(Events.CVItemDetails, parameters);

        mActivity.getProfiler().setEndLogTime(DataCollector.EventParameters.CustomEventType.CV_ITEMDETAILS.getValue());

        if (!IS_DISCOUNT_POG_ENABLED) {
            mPresenter.getDynamicDiscount(saleDetail.getSkuId());
        }

        final Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_to_bottom);
        anim.setDuration(200);

        final String personalisation = saleDetail.getPersonalisation();
        final String deliveryInformation = saleDetail.getDeliveryInformation();
        final String shippingInformation = saleDetail.getShippingInformation();
        final String returnPolicy = saleDetail.getReturnPolicy();
        final String productAbout = saleDetail.getAttributes() == null ? "" : saleDetail.getAttributes().getBrandDescription() == null ? "" : saleDetail.getAttributes().getBrandDescription();

        if (personalisation != null) {
            mPersonalisationLayout.inflateForProductDetails(mActivity, new Gson().fromJson(
                    personalisation, Personalisation.class));
            if (mSavedPersonalizationData != null) {
                mPersonalisationLayout.populateFieldsWithDataFromAddToCart(mSavedPersonalizationData);
                mSavedPersonalizationData = null;
            }
        }

        mShippingContainer.setVisibility(shippingInformation != null ||
                deliveryInformation != null ||
                mShippingPostcodeContainer.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
        mShippingDescHeaderText.setVisibility(shippingInformation != null ? View.VISIBLE : View.GONE);

        getPriceBlockHelper().setPriceInfoOnClickListener(v -> onPriceInfoClicked(saleDetail));

        if (shippingInformation != null || deliveryInformation != null) {
            mShippingDescText.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mShippingDescText.startAnimation(anim);

            if (deliveryInformation == null) {
                mShippingDescText.loadDataWithBaseURL(null, mHtmlHeader + shippingInformation + mHtmlFooter,
                        "text/html", "UTF-8", null);
            } else if (shippingInformation != null) {
                mShippingDescText.loadDataWithBaseURL(null, mHtmlHeader + deliveryInformation + "<br/><br/>" + shippingInformation + mHtmlFooter,
                        "text/html", "UTF-8", null);
            } else {
                mShippingDescText.loadDataWithBaseURL(null, mHtmlHeader + deliveryInformation + mHtmlFooter,
                        "text/html", "UTF-8", null);
            }
        }

        if (returnPolicy != null) {
            mReturnPolicyContainer.setVisibility(View.VISIBLE);
            mReturnPolicyText.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    mActivity.getMainController().showReturnPolicy();
                    return true;
                }
            });
            mReturnPolicyText.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mReturnPolicyText.startAnimation(anim);
            mReturnPolicyText.loadDataWithBaseURL(null, mHtmlHeader + returnPolicy + mHtmlFooter,
                    "text/html", "UTF-8", null);
        }

        if (!productAbout.isEmpty()) {
            mProductAboutContainer.setVisibility(View.VISIBLE);
            mProductAboutText.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mProductAboutText.startAnimation(anim);
            mProductAboutText.loadDataWithBaseURL(null, mHtmlHeader + productAbout + mHtmlFooter,
                    "text/html", "UTF-8", null);
        }

        if (saleDetail.getDescription() != null) {
            mProductDescriptionText.startAnimation(anim);
            mProductDescriptionText.loadDataWithBaseURL(null, mHtmlHeader + saleDetail.getDescription() + mHtmlFooter,
                    "text/html", "UTF-8", null);
        }

        mSizeGuideLink = getSizeGuideLink(saleDetail.getDescription());

        mProductDescriptionText.getSettings()
                .setJavaScriptEnabled(true);

        mProductDescriptionText.getSettings()
                .setDomStorageEnabled(true);

        mProductDescriptionText.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.contains("about:blank")) {
                    ActivityLaunchUtil.launchActivity(mActivity, url);
                }
                return true;
            }

        });

        if (saleDetail.getSkuVariants() != null && !saleDetail.getSkuVariants().isEmpty()) {
            mSkuVariants = saleDetail.getSkuVariants();
            mProductSizes.clear();
            for (SaleItemDetails skuVariant : mSkuVariants) {
                String skuId = skuVariant.getSkuId();
                String size = skuVariant.getAttributes().getSize();
                if (size != null && !size.isEmpty()) {
                    mProductSizes.add(new Pair<>(size, skuId));
                }
            }
        }

        if (!mProductSizes.isEmpty()) {

            mHasSizes = true;

            TagAdapter mSizesAdapter = new TagAdapter<Pair<String, String>>(mProductSizes) {

                @SuppressWarnings("ConstantConditions")
                @Override
                public View getView(FlowLayout parent, int position, Pair<String, String> data) {
                    TextView tv = (TextView) mActivity.getLayoutInflater()
                            .inflate(R.layout.sizes_chips_layout,
                                    parent,
                                    false);
                    tv.setText(data.first);
                    float size = tv.getContext().getResources().getDimension(R.dimen.text_size_body);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
                    if (mSkuVariants.get(position).isSoldOut()) {
                        tv.setBackground(getDrawable(R.drawable.bg_chips_soldout));
                        tv.setTextColor(getColor(R.color.bg_chips_soldout_text));
                    }

                    return tv;
                }
            };

            mIsSoldOutCombined = true;
            for (SaleItemDetails response : mSkuVariants) {
                if (!response.isSoldOut()) {
                    mIsSoldOutCombined = false;
                    break;
                }
            }

            mSizesFlowLayout.setAdapter(mSizesAdapter);
            if (mSelectedSizeIndex >= 0) {
                mSizesFlowLayout.getAdapter().setSelectedList(Sets.newHashSet(mSelectedSizeIndex));
            }

            mSizesFlowLayout.setOnTagClickListener((view, position, parent) -> false);

            mSizesFlowLayout.setOnSelectListener(selectPosSet -> {
                onSelectTag(selectPosSet.isEmpty() ? -1 : selectPosSet.iterator().next());
            });

            // auto-select size if mProductSizes equals to 1
            if (mProductSizes.size() == 1) {
                onSelectTag(0);
            }

        }

        updatePriceDetails(saleDetail, false);

        showAddToCartButton();

        mPresenter.addToRecentlyViewedItems(saleDetail.getProductId(), saleDetail.getSeoIdentifier());

        mLikeButton.setVisibility(View.VISIBLE);
        mLikeFloatingButton.setVisibility(View.VISIBLE);
        updateLikeButtonImage(mPresenter.isProductInWishlist(mProductId));

        hasLoadedDetails = true;
        if (isAddToBasketInputBuffered) {
            isAddToBasketInputBuffered = false;
            addToBasket();
        }

        setupSeller(saleDetail);
        setupBuyBox(saleDetail);
    }

    public void setupPartialProductDetails(SaleItemProduct saleDetail) {
        mSeoIdentifierId = saleDetail.getSeoIdentifier();
        mSaleName = saleDetail.getName();
        mBrandName = saleDetail.getBrandName();
        mSalePrice = saleDetail.getPrice() != null ? PriceUtils.getPriceStringValue(saleDetail.getPrice().getValue()) : null;
        mSaleOldPrice = saleDetail.getOriginalPrice() != null ? PriceUtils.getPriceStringValue(saleDetail.getOriginalPrice().getValue()) : null;

        mIsFreeDelivery = saleDetail.getFreeDelivery();
        mFreeDeliveryImageView.setVisibility(View.GONE); //the pog style; don't show

        String name = saleDetail.getName() == null ? "" : saleDetail.getName();
        String brandName = saleDetail.getBrandName() == null ? "" : saleDetail.getBrandName();

        if (brandName != null && !brandName.isEmpty()) {
            mToolbarItemBrandTextView.setText(brandName);
            final String toolbarItemNameText = name.trim() + " • " + PriceUtils.getPriceStringValue(saleDetail.getPrice().getValue());
            mToolbarItemNameTextView.setText(toolbarItemNameText);
            mProductName.setText(name.trim());
            mProductBrand.setText(Html.fromHtml("<u>" + brandName.trim() + "</u>"));
        } else {
            mToolbarItemBrandTextView.setText(name.trim());
            mToolbarItemNameTextView.setText(saleDetail.getPrice() != null ? PriceUtils.getPriceStringValue(saleDetail.getPrice().getValue()) : null);
            mProductBrand.setText(Html.fromHtml("<u>" + name.trim() + "</u>"));
        }

        mIsSoldout = saleDetail.isSoldOut();

        updatePartialPriceDetails(saleDetail);

        if (mIsFreeDelivery) {
            mShippingPreviewPrice.setText(getFreeShippingSpan());
            mShippingPreviewPrice.setVisibility(mShippingPreviewPrice.getText() != null && mShippingPreviewPrice.getText().length() > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void setupSeller(SaleItemDetails saleDetail) {
        if (saleDetail.getSellerName() != null && !saleDetail.getSellerName().isEmpty() &&
                saleDetail.getSeoStoreId() != null && !saleDetail.getSeoStoreId().isEmpty()) {
            final String productId = saleDetail.getProductId();
            final String sellerName = saleDetail.getSellerName();
            final String storeId = saleDetail.getSeoStoreId();
            final Context context = sellerContainer.getContext();
            SpannableStringBuilder sellerText = new SpannableStringBuilder();
            sellerText.append(context.getResources().getString(R.string.product_details_seller_text));
            sellerText.append(" ");
            final int start = sellerText.length();
            sellerText.append(sellerName, new UnderlineSpan(), SPAN_INCLUSIVE_EXCLUSIVE);
            sellerText.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.text_link_color)), start, sellerText.length(), SPAN_INCLUSIVE_EXCLUSIVE);
            sellerTextView.setText(sellerText);
            sellerTextView.setOnClickListener(v -> {
                gotoProductListWithParameters(new SaleItemsController.Parameters.FromSeller(sellerName, storeId));
                logSellerLinkEvent(sellerName, productId, storeId);
            });
            sellerContainer.setVisibility(View.VISIBLE);
        } else {
            sellerTextView.setOnClickListener(null);
            sellerContainer.setVisibility(View.GONE);
        }
    }

    private void setupBuyBox(SaleItemDetails saleDetail) {
        if (saleDetail.getBuyBoxGroup() != null && !saleDetail.getBuyBoxGroup().isEmpty()) {
            buyboxContainer.setVisibility(View.VISIBLE);
            buyBoxTitleTextView.setText(StringUtils.toTitleCase(mPresenter.getBuyboxTemplateTextTitle()));
            buyBoxItemsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
            buyBoxItemsRecyclerView.setAdapter(new BuyboxItemsAdapter(
                    saleDetail.getBuyBoxGroup(),
                    mPresenter.getBuyboxTemplateTextSellerTemplate(),
                    mPresenter.getBuyboxTemplateTextButtonText(),
                    new BuyboxItemsAdapter.BuyBoxItemsHelper() {
                        @Override
                        public void onLinkPressed(SaleItemDetails.BuyBoxItem item) {
                            gotoProductListWithParameters(new SaleItemsController.Parameters.FromSeller(
                                    item.getSellerName(),
                                    item.getSeoStoreId()
                            ));
                        }

                        @Override
                        public void onButtonPressed(SaleItemDetails.BuyBoxItem item) {
                            mProductDetailScrollView.smoothScrollTo(0, 0);
                            loadProductDetails(null, item.getSeoIdentifier());
                        }
                    }));
            setBuyboxMinimized(true);
            buyBoxHeaderContainer.setOnClickListener(v -> {
                setBuyboxMinimized(!isBuyboxMinimized());
            });
        } else {
            buyBoxHeaderContainer.setOnClickListener(null);
            buyboxContainer.setVisibility(View.GONE);
            buyBoxItemsRecyclerView.setAdapter(null);
        }
    }

    private void setBuyboxMinimized(boolean isMinimized) {
        if (isMinimized == isBuyboxMinimized()) {
            return;
        }

        if (isMinimized) {
            buyBoxMinimizeButtonView.setImageResource(R.drawable.ic_chevron_down);
            buyBoxItemsRecyclerView.setVisibility(View.GONE);
        } else {
            buyBoxMinimizeButtonView.setImageResource(R.drawable.ic_chevron_up);
            buyBoxItemsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isBuyboxMinimized() {
        return buyBoxItemsRecyclerView.getVisibility() != View.VISIBLE;
    }

    private void loadProductDetails(String saleId, String seoIdentifierId) {
        if (currentBottomPopupView != null) {
            currentBottomPopupView.dismiss(true);
        }
        mPresenter.loadProductDetails(saleId, seoIdentifierId);
        onLoadProductDetails = this::setupProductDetails;
    }

    private void setupDelayedProgressBar() {
        stopDelayedProgressBar();
        addToCartDelayHandler = new Handler(Looper.getMainLooper());
        addToCartDelayRunnable = () -> showAddToCartButtonContent(isAddToBasketInputBuffered);
        addToCartDelayHandler.postDelayed(addToCartDelayRunnable, ACTIVITY_INDICATOR_DELAY);
    }

    private void stopDelayedProgressBar() {
        if (addToCartDelayHandler != null && addToCartDelayRunnable != null) {
            addToCartDelayHandler.removeCallbacks(addToCartDelayRunnable);
            addToCartDelayHandler = null;
            addToCartDelayRunnable = null;
        }
    }

    private void showAddToCartButton() {
        if (mAddToCartButton != null) {
            if (!mIsSoldOutCombined || !mIsSoldout) {
                if (mActivity.getResources().getBoolean(R.bool.is_sale_countdown_timer_enabled) &&
                        (mEndDate != null && !mEndDate.isEmpty()) &&
                        DateUtils.getRemainingTimeInMillis(mEndDate) >= 0 &&
                        DateUtils.isLessThanADay(DateUtils.getRemainingTimeInMillis(mEndDate))) {
                    setupSaleRemainingTime(mEndDate);
                    mAddToCartTimer.setVisibility(View.VISIBLE);
                    mAddToCartButtonContainer.setVisibility(View.GONE);
                } else {
                    mAddToCartTimer.setVisibility(View.GONE);
                    mAddToCartButtonContainer.setVisibility(View.VISIBLE);
                    mAddToCartButton.setEnabled(true);
                    mAddToCartButton.bringToFront();
                }
            } else {
                mAddToCartButtonContainer.setVisibility(View.VISIBLE);
                mAddToCartButton.setEnabled(false);
                mAddToCartButton.bringToFront();
            }

            showAddToCartButtonContent(false);
        }

    }

    private void showAddToCartButtonContent(boolean showProgressBar) {
        if (showProgressBar) {
            mAddToCartButton.setText("");
            mAddToCartTimerTextView.setText("");
            mAddToCartProgressBar.setVisibility(View.VISIBLE);
            mAddToCartTimerProgressBar.setVisibility(View.VISIBLE);
        } else {
            mAddToCartTimerTextView.setText(R.string.add_to_cart);
            if (mIsSoldout && mIsSoldOutCombined) {
                mAddToCartButton.setText(R.string.sold_out);
            } else {
                mAddToCartButton.setText(R.string.add_to_cart);
            }
            mAddToCartProgressBar.setVisibility(View.GONE);
            mAddToCartTimerProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showAddToCartResponse(CheckoutDetailsMapper cartDetailsResponse) {

        if (mSharedImageLocation == null) {
            mSharedImageLocation = ImageUtils.getDisplayedImageLocation(mProductSharedImage);
        }
        animateAddToCart(() -> CustomAlertDialog.showCustomAlertDialog(
                getActivity(), CustomAlertDialog.CustomDialogIconState.POSITIVE,
                mActivity.getString(R.string.add_to_cart_success)));

        RxBus.instance().post(IntrospectionUtils.EVENT_ADD_TO_CART);
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.SOURCE, mOrigin);
        parameters.put(DataCollector.EventParameters.ATTEMPTS, mAttempts);
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, SaleItemDetailsController.class.getSimpleName());
        parameters.put(DataCollector.EventParameters.ITEM_ID, cartDetailsResponse.getSaleID());
        parameters.put(DataCollector.EventParameters.ITEM_NAME, mSaleName);
        parameters.put(DataCollector.EventParameters.PRICE,
                Double.valueOf(mSalePrice.substring(Settings.getSelectedCountry().currencySign.length())));
        parameters.put(DataCollector.EventParameters.COUNTRY_ID, Settings.getSelectedCountry().countryId);
        parameters.put(DataCollector.EventParameters.ITEM_CATEGORY, mProductBrand);
        parameters.put(DataCollector.EventParameters.ADD_TO_CART_QUANTITY, "1");
        parameters.put(DataCollector.EventParameters.ADD_TO_CART_CURRENCY,
                Settings.getSelectedCountry().currencySign);
        parameters.put(DataCollector.EventParameters.ADD_TO_CART_SOURCE, SaleItemDetailsController.class.getSimpleName());
        DataCollector.logEvent(Events.AddedToCartEvent, parameters);

        if (mPresenter.isProductInWishlist(mProductId)) {
            HashMap<String, Object> parametersForWishlistEvent = new HashMap<>();
            parametersForWishlistEvent.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
            parametersForWishlistEvent.put(DataCollector.EventParameters.SCREEN_NAME, SaleItemDetailsController.class.getSimpleName());
            DataCollector.logEvent(Events.WishlistAddToCartEvent, parametersForWishlistEvent);
        }

        mAttempts = 0;
        //notify bottom navigation view(checkout) with success.
        CartUtil.addValueToCart(1);
        mActivity.getMainController().updateBasketItemsQuantity();

        mActivity.getMainController().sendSaleItemToCheckout(cartDetailsResponse);
    }

    @Override
    public void showAddToCartResponseFailed() {
        CustomAlertDialog.showCustomAlertDialog(
                mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                mActivity.getString(R.string.add_to_cart_failed));
    }

    @Override
    public void showMyPayDetails(SaleItemDetails value, Ourpay ourpay) {
        if (ourpay != null) {
            mOurpay = ourpay;
            OurpayPanel panel = new OurpayPanel(mActivity);
            mOurpayHolder.setVisibility(View.VISIBLE);
            mOurpayHolder.removeAllViews();
            mOurpayHolder.addView(panel.generatePanel(ourpay));
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void showAfterpayDetails(int installmentsCount, double installmentAmount, String currency) {
        AfterpayPanelViewHolder viewHolder = new AfterpayPanelViewHolder(mActivity);

        SpannableStringBuilder spannableString;

        Drawable drawable = mActivity.getDrawable(R.drawable.afterpay);
        assert drawable != null;

        drawable.setBounds(0, 0,
                (int) mActivity.getResources().getDimension(R.dimen.afterpay_logo_width),
                (int) mActivity.getResources().getDimension(R.dimen.afterpay_logo_height));

        ImageSpan imageSpan = new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);

        if (installmentsCount <= 0 || installmentAmount <= 0 || currency == null || currency.isEmpty()) {
            String description = mActivity.getResources()
                    .getString(R.string.afterpay_panel_description_is_unavailable);

            spannableString = new SpannableStringBuilder(description);

            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            String description = String.format(mActivity.getResources()
                            .getString(R.string.afterpay_panel_description_normal),
                    installmentsCount,
                    currency,
                    installmentAmount);

            spannableString = new SpannableStringBuilder(description);

            StringUtils.applySpanToSubstringsMatching(
                    spannableString,
                    new StyleSpan(BOLD),
                    mActivity.getResources().getString(R.string.regex_currency),
                    SPAN_EXCLUSIVE_INCLUSIVE);

            spannableString.setSpan(imageSpan, description.length() - 1, description.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        viewHolder.setDescription(spannableString);

        viewHolder.setInfoButtonOnClickListener(v -> {
            Bundle bundle = new BundleBuilder(new Bundle())
                    .putString(FloatingImageViewerController.KEY_SOURCE_URL, mPresenter.getAfterpayLightboxImgUrl())
                    .putInt(FloatingImageViewerController.KEY_SOURCE_DRAWABLE_ID, R.drawable.afterpay_lightbox)
                    .build();

            FloatingImageViewerController controller = new FloatingImageViewerController(bundle);

            controller.setImageClickListener((view, x, y) -> {
                Intent openUrl = new Intent(Intent.ACTION_VIEW);
                openUrl.setData(Uri.parse(mPresenter.getAfterpayTermsLink()));
                startActivity(openUrl);
            });

            RouterTransaction routerTransaction = RouterTransaction.with(controller)
                    .popChangeHandler(new FadeChangeHandler())
                    .pushChangeHandler(new FadeChangeHandler());

            if (mActivity.getMainController().getPopUpHostRouter() != null) {
                mActivity.getMainController().getPopUpHostRouter().setRoot(routerTransaction);
            } else {
                getRouter().pushController(routerTransaction);
            }
        });

        mAfterpayHolder.removeAllViews();
        mAfterpayHolder.addView(viewHolder.getView());
    }

    @Override
    public void onCallGetBasketItemsQuantity() {

    }

    @Override
    public void setDynamicDiscount(String discountText) {
        if (!IS_DISCOUNT_POG_ENABLED || discountText == null) {
            mProductDiscountPogTextView.setVisibility(View.GONE);
            return;
        }
        String percentOffText = discountText.trim();
        String[] discountWordArray = discountText.split(" ");
        percentOffText = percentOffText.replace(' ', '\n');

        int percentSymbolLength = 1;
        int spannableStringEndParameter = SPANNABLE_STRING_START_INDEX + discountWordArray[1].length() + percentSymbolLength;
        SpannableString string = new SpannableString(percentOffText);
        string.setSpan(new StyleSpan(BOLD), SPANNABLE_STRING_START_INDEX, spannableStringEndParameter, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new RelativeSizeSpan(DISCOUNT_VALUE_SCALE_FACTOR), SPANNABLE_STRING_START_INDEX, spannableStringEndParameter, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mProductDiscountPogTextView.setVisibility(View.VISIBLE);
        mProductDiscountPogTextView.setText(string);
    }

    @Override
    public void setIsAfterpayDetailsVisible(boolean visible) {
        shouldAfterpayDetailsBeVisible = visible;
        mAfterpayHolder.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showFreeShipping(String deliveryType, String deliveryThreshold) {
        if (deliveryType.equalsIgnoreCase(AppConstants.THRESHOLD_RESTRICT) ||
                deliveryType.equalsIgnoreCase(AppConstants.ORDER_PRICE_RESTRICT)) {

            mFreeDeliveryImageView.setOnClickListener(v -> {
                mActivity.showFreeShippingDialog(deliveryThreshold, mActivity.getShippingTemplateText(),
                        mActivity.getShippingTitle());
            });
            getPriceBlockHelper().setFreeDeliveryOnClickListener(v -> {
                mActivity.showFreeShippingDialog(deliveryThreshold, mActivity.getShippingTemplateText(),
                        mActivity.getShippingTitle());
            });

        }
    }

    private void onPriceInfoClicked(SaleItemProduct item) {
        String rrpText = item instanceof SaleItemDetails ? ((SaleItemDetails) item).getRrpText() : rrpTextCache.get(item.getId());
        String pricingText = item instanceof SaleItemDetails ? ((SaleItemDetails) item).getPricing() : pricingTextCache.get(item.getId());
        if (mActivity.getSupplierOriginalPriceInfoHelper() != null) {
            showItemPricingInfoView(
                    mActivity.getSupplierOriginalPriceInfoHelper().getOriginalPriceInfoWebViewContent(
                            rrpText, item));
        } else if (rrpText != null) {
            showItemPricingInfoView(getPricingInfo(rrpText, pricingText));
        }
        if (rrpText == null) {
            mPresenter.loadProductDetails(null, item.getSeoIdentifier());
            onLoadProductDetails = saleDetails -> {
                if (saleDetails.getRrpText() == null) {
                    return;
                }
                if (mActivity.getSupplierOriginalPriceInfoHelper() != null) {
                    showItemPricingInfoView(
                            mActivity.getSupplierOriginalPriceInfoHelper().getOriginalPriceInfoWebViewContent(
                                    saleDetails.getRrpText(), item));
                } else {
                    showItemPricingInfoView(getPricingInfo(saleDetails.getRrpText(), saleDetails.getPricing()));
                }
            };
        }
    }

    @Override
    public void showYouMayAlsoLike(List<GetYouMayAlsoLikeResponse> response) {
        mYouMayAlsoLikeList = response;

        showYouMayAlsoLike();
    }

    private void showYouMayAlsoLike() {
        if (mYouMayAlsoLikeList == null || mYouMayAlsoLikeList.isEmpty()) {
            mYouMayAlsoLikeContainer.setVisibility(View.GONE);
            return;
        }
        mYouMayAlsoLikeContainer.setVisibility(View.VISIBLE);

        HorizontalScrollingItemsAdapter adapter = new HorizontalScrollingItemsAdapter(mActivity.getSupplierOriginalPriceInfoHelper() != null);
        adapter.setYouMayAlsoLikeList(mYouMayAlsoLikeList);
        adapter.setOnItemTappedListener(new HorizontalScrollingItemsAdapter.OnItemTappedListener() {
            @Override
            public void onItemTapped(SaleItemProduct item, int position, int size) {
                SaleItemDetailsController.this.onItemTapped((GetYouMayAlsoLikeResponse) item, position, size);
            }

            @Override
            public void onPriceInfoTapped(SaleItemProduct item) {
                onPriceInfoClicked(item);
            }
        });
        adapter.setWishlistListener(horizontalItemsWishlistListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);

        mYouMayAlsoLikeRecyclerview.setAdapter(adapter);
        mYouMayAlsoLikeRecyclerview.setLayoutManager(linearLayoutManager);
        mYouMayAlsoLikeRecyclerview.getRecycledViewPool().clear();

        if (youMayAlsoLikeHelper != null) {
            youMayAlsoLikeHelper.onRecyclerViewDetach();
        }
        youMayAlsoLikeHelper = new SaleItemDetailsHorizontalScrollingItemsHelper(
                mYouMayAlsoLikeRecyclerview,
                mPresenter.isTablet(),
                false,
                true,
                mActivity.getSupplierOriginalPriceInfoHelper() != null
        );
    }

    @Override
    public void showRecommendedItems(List<RecommendedItemsResponse> recommendedItemsResponseList) {
        if (recommendedItemsResponseList == null || recommendedItemsResponseList.isEmpty()) {
            mRecommendedContainer.setVisibility(View.GONE);
            mRecommendedRecyclerView.setAdapter(null);
            return;
        }
        mRecommendedContainer.setVisibility(View.VISIBLE);

        mRecommendedList = recommendedItemsResponseList;

        HorizontalScrollingItemsAdapter adapter = null;
        if (!recommendedItemsResponseList.isEmpty()) {
            adapter = new HorizontalScrollingItemsAdapter(mActivity.getSupplierOriginalPriceInfoHelper() != null);
            adapter.setRecommendedList(recommendedItemsResponseList);
            adapter.setOnItemTappedListener(new HorizontalScrollingItemsAdapter.OnItemTappedListener() {
                @Override
                public void onItemTapped(SaleItemProduct item, int position, int size) {
                    SaleItemDetailsController.this.onItemTapped((RecommendedItemsResponse) item, position, size);
                }

                @Override
                public void onPriceInfoTapped(SaleItemProduct item) {
                    onPriceInfoClicked(item);
                }
            });
            adapter.setWishlistListener(horizontalItemsWishlistListener);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);

        mRecommendedRecyclerView.setAdapter(adapter);
        mRecommendedRecyclerView.setLayoutManager(linearLayoutManager);
        mRecommendedRecyclerView.getRecycledViewPool().clear();

        if (recommendedItemsHelper != null) {
            recommendedItemsHelper.onRecyclerViewDetach();
        }
        recommendedItemsHelper = new SaleItemDetailsHorizontalScrollingItemsHelper(
                mRecommendedRecyclerView,
                mPresenter.isTablet(),
                false,
                true,
                mActivity.getSupplierOriginalPriceInfoHelper() != null
        );
    }

    @Override
    public void showRecentlyViewedItems(List<RecentlyViewedItemResponse> response) {
        if (response == null || response.isEmpty()) {
            mRecentlyViewedContainer.setVisibility(View.GONE);
            mRecentlyViewedRecyclerView.setAdapter(null);
            return;
        }

        mRecentlyViewedContainer.setVisibility(View.VISIBLE);

        HorizontalScrollingItemsAdapter adapter = null;
        if (!response.isEmpty()) {
            adapter = new HorizontalScrollingItemsAdapter(mActivity.getSupplierOriginalPriceInfoHelper() != null);
            adapter.setRecentlyViewedList(response);
            adapter.setShouldRepeatCellsToFillWidth(false);
            adapter.setOnItemTappedListener(new HorizontalScrollingItemsAdapter.OnItemTappedListener() {
                @Override
                public void onItemTapped(SaleItemProduct item, int position, int size) {
                    SaleItemDetailsController.this.onItemTapped((RecentlyViewedItemResponse) item, position, size);
                }

                @Override
                public void onPriceInfoTapped(SaleItemProduct item) {
                    onPriceInfoClicked(item);
                }
            });
            adapter.setWishlistListener(horizontalItemsWishlistListener);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);

        mRecentlyViewedRecyclerView.setAdapter(adapter);
        mRecentlyViewedRecyclerView.setLayoutManager(linearLayoutManager);
        mRecentlyViewedRecyclerView.getRecycledViewPool().clear();

        if (recentlyViewedHelper != null) {
            recentlyViewedHelper.onRecyclerViewDetach();
        }
        recentlyViewedHelper = new SaleItemDetailsHorizontalScrollingItemsHelper(
                mRecentlyViewedRecyclerView,
                mPresenter.isTablet(),
                false,
                true,
                mActivity.getSupplierOriginalPriceInfoHelper() != null
        );
    }

    @Override
    public boolean handleBack() {
        willViewDisappear = true;
        if (!isAnimating) {
            mProductDetailScrollView.scrollTo(0, 0);
            if (mRootView != null) {
                mRootView.removeListener(mDragDismissListener);
            }
            mDragDismissListener = null;
            return false;
        }
        return true;
    }

    @OnClick({R.id.product_details_add_to_basket, R.id.product_details_add_to_basket_timer})
    void addToBasket() {
        if (!hasLoadedDetails) {
            if (!isAddToBasketInputBuffered) {
                isAddToBasketInputBuffered = true;
                setupDelayedProgressBar();
            }
            return;
        }

        if (currentBottomPopupView != null) {
            currentBottomPopupView.dismiss(true);
        }

        mAttempts++;

        CommonUtils.saveSaleItem(mActivity, mMasterProductId, mSeoIdentifierId, mSaleId);

        AddToCartRequest request = new AddToCartRequest();
        request.setSkuId(mSkuId);
        request.setItemName(mSaleName);
        request.setPrice(Double.parseDouble(mSalePrice.substring(Settings.getSelectedCountry().currencySign.length())));
        request.setPersonalizationData(mPersonalisationLayout.getDataForAddToCart());
        request.setUserClientType(String.valueOf(mPresenter.isTablet() ? AppConstants.ADD_TO_CART_TABLET :
                AppConstants.ADD_TO_CART_PHONE));

        boolean isSizeValid = !(mHasSizes && mSelectedSizeIndex < 0);

        boolean isPersonalisationValid = mPersonalisationLayout.verifyRequiredFields();

        String personalisationError = !mPresenter.getPersonalisationErrorText().equals("") ?
                mPresenter.getPersonalisationErrorText() :
                mActivity.getString(R.string.please_fill_up_personalisation_details);


        if (!isPersonalisationValid) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    personalisationError);

            mProductDetailScrollView.smoothScrollTo(0,
                    mPersonalisationLayout.getTop() + mProductDetailScrollView.getHeight() / 2);
            new Handler(Looper.getMainLooper())
                    .postDelayed(() -> CommonUtils.shakeView(mPersonalisationLayout),
                            PERSONALIZATION_SHAKE_DELAY);


        } else if (!isSizeValid) {
            bringAttentionToSizeSelection();
        } else {
            verifyAddToCart(request);
        }
    }

    private void bringAttentionToSizeSelection() {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mProductDetailsButtonContainer.getLayoutParams();
        int addToCartButtonHeight = Math.max(mAddToCartButtonContainer.getHeight(), mAddToCartTimer.getHeight());
        if (ViewUtils.isViewVisibleInScrollView(
                mSizesFlowLayout,
                mProductDetailScrollView,
                mProductDetailsToolbar.getVisibility() == View.VISIBLE ? mProductDetailsToolbar.getHeight() : 0,
                -(lp.bottomMargin + addToCartButtonHeight))) {
            shakeSizeButtons();
        } else {
            showBottomDialogWithSizeSelection();
        }
        mSizesNotSelectedNotice.setVisibility(View.VISIBLE);
    }

    private void showBottomDialogWithSizeSelection() {
        HashSet<Integer> indicesOfSoldOutSizes = new HashSet<>();
        for (int i = 0; i < mSkuVariants.size(); i++) {
            if (mSkuVariants.get(i).isSoldOut()) {
                indicesOfSoldOutSizes.add(i);
            }
        }
        BottomSheetSizesDialog.OnSizeGuideTappedListener onSizeGuideTappedListener = null;
        if (mSizeGuideLink != null && !mSizeGuideLink.isEmpty()) {
            onSizeGuideTappedListener = () -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mSizeGuideLink));
                startActivity(browserIntent);
            };
        }
        mActivity.showProductDetailsSizesBottomDialog(mProductSizes,
                indicesOfSoldOutSizes,
                onSizeGuideTappedListener,
                selectedIndex -> {
                    onSelectTag(selectedIndex);
                    addToBasket();
                });
    }

    private void shakeSizeButtons() {
        if (mSizesFlowLayout.getAnimation() == null) {
            CommonUtils.shakeView(mSizesFlowLayout);
        }
    }

    private String getSizeGuideLink(String sourceString) {
        if (sourceString == null) {
            return null;
        }

        final String[] extensions = new String[]{
                "pdf", "html", "jpg", "jpeg", "png", "webp"
        };

        for (String extension : extensions) {
            // This regex finds the a <a href="http://something">Size Guide</a>
            // and then selects the url inside the href quotes. Since most
            // Size Guides are pdfs or images, extensions are also checked.
            String regex = "(?!<a href=\\\")http.*?:\\/\\/.+\\." + extension + "(?=\\\".*>Size.*?Guide<\\/a>)";
            Matcher matcher = Pattern.compile(regex).matcher(sourceString);
            if (matcher.find()) {
                return matcher.group();
            }
        }

        return null;
    }

    private void verifyAddToCart(AddToCartRequest request) {
        if (!mPresenter.isAuthorized()) {
            mActivity.showLoginController(getRouter(), new AuthHandler() {
                @Override
                public void success() {
                    mActivity.getMainController().resetCheckoutRouter();
                    mActivity.callGCMRegisterSubscriber();
                    mPresenter.addToCart(request);
                }

                @Override
                public void error() {

                }
            });
        } else {
            mPresenter.addToCart(request);
        }
    }

    private void animateAddToCart(Runnable onAnimationEnd) {
        mProductDetailScrollView.scrollTo(0, 0);

        mImageViewToAnimate.setVisibility(View.VISIBLE);
        mImageViewToAnimate.bringToFront();
        SaleItemDetailsImageAdapter.ViewHolder vh = (SaleItemDetailsImageAdapter.ViewHolder) mProductImagesRv
                .findViewHolderForLayoutPosition(mProductImagesRvLayoutManager.findLastVisibleItemPosition());

        int productWidth;
        int productHeight;

        if (mImagesLoaded && vh != null) {
            mImageViewToAnimate.setImageDrawable(vh.image.getDrawable());
            productWidth = vh.image.getDrawable().getIntrinsicWidth();
            productHeight = vh.image.getDrawable().getIntrinsicHeight();
        } else {
            productWidth = mProductSharedImage.getDrawable().getIntrinsicWidth();
            productHeight = mProductSharedImage.getDrawable().getIntrinsicHeight();
            mImageViewToAnimate.setImageDrawable(mProductSharedImage.getDrawable());
        }

        mImageViewToAnimate.getLayoutParams().height = productHeight;
        mImageViewToAnimate.getLayoutParams().width = productWidth;

        mBottomNavView = mActivity.getMainController().getBottomNav();
        ArrayList<View> potentialViews = new ArrayList<>();
        mBottomNavView.findViewsWithText(potentialViews, "checkout", View.FIND_VIEWS_WITH_TEXT);
        mCheckoutView = mBottomNavView.getViewAtPosition(4);
        mCheckoutView.getLocationOnScreen(mCheckoutLocation);

        float origElevation = mBottomNavView.getElevation();
        mBottomNavView.setElevation(0);

        mImageViewToAnimate.getLocationOnScreen(mSharedImageLocation);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        int[] middle = {width, 0};

        ArcTranslateAnimation anim = new ArcTranslateAnimation(
                getResource().getInteger(R.integer.animation_duration),
                0, 0,
                (float) middle[0], (float) middle[1],
                (float) mCheckoutLocation[0], (float) mCheckoutLocation[1]);

        anim.setInterpolator(new LinearInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimating = false;
                if (mImageViewToAnimate != null) {
                    mImageViewToAnimate.setVisibility(View.GONE);
                }
                if (mBottomNavView != null) {
                    mBottomNavView.setElevation(origElevation);
                }

                if (onAnimationEnd != null) {
                    onAnimationEnd.run();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mImageViewToAnimate.startAnimation(anim);

    }

    private List<String> getQualityImages(List<String> images) {
        List<String> qualityImages = new LinkedList<>();
        for (int i = 3; i < images.size(); i += 4) {
            qualityImages.add(images.get(i));
        }
        return qualityImages;
    }

    @OnClick(R.id.toolbar_left_view)
    void dismissArrowDown() {
        mActivity.onBackPressed();
    }

    @OnClick(R.id.share_right)
    void likeButtonPress() {
        setLikeStatus(!mPresenter.isProductInWishlist(mProductId));
    }

    @OnClick(R.id.toolbar_right_view)
    void floatingLikeButtonPress() {
        setLikeStatus(!mPresenter.isProductInWishlist(mProductId));
    }

    private void setLikeStatus(boolean isLiked) {
        SaleItemDetailsMvpPresenter.WishlistDelayedCallback delayedCallback = () -> {
            logWishlistEvent(mProductId, isLiked);
        };
        if (isLiked) {
            mPresenter.addProductToWishlist(mProductId, mSeoIdentifierId, mMasterProductId, delayedCallback);
        } else {
            mPresenter.removeProductFromWishlist(mProductId, delayedCallback);
        }
        updateLikeButtonImage(isLiked);
    }

    private void updateLikeButtonImage(boolean isLiked) {
        int drawableId = isLiked ? R.drawable.wishlist_product_details_active : R.drawable.wishlist_product_details_inactive;
        mLikeButton.setImageDrawable(mLikeFloatingButton.getContext().getResources().getDrawable(drawableId));
        mLikeFloatingButton.setImageDrawable(mLikeFloatingButton.getContext().getResources().getDrawable(drawableId));
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

    public void onScrollChanged(int scrollY) {
        if (isViewAttached()) {
            mRootView.setIsHorizontalDismissEnabled(scrollY <= 0 && getCarouselPosition() == 0);
            mRootView.setIsVerticalDismissEnabled(scrollY <= 0);
            int[] location = new int[2];
            mProductDetailsTitleLayout.getLocationOnScreen(location);
            float top = location[1];
            float alphaFactor = 1 - top / (float) mProductDetailsTitleLayout.getHeight();

            if (mProductDetailsToolbar.getAnimation() == null && !willViewDisappear) {
                switch (mProductDetailsToolbar.getVisibility()) {
                    case View.GONE:
                    case View.INVISIBLE:
                        if (alphaFactor > 0.5) {
                            mProductDetailsToolbar.setVisibility(View.VISIBLE);
                            CommonUtils.fadeInView(mProductDetailsToolbar, null);
                        }
                        break;
                    case View.VISIBLE:
                        if (alphaFactor < 0) {
                            CommonUtils.fadeOutView(mProductDetailsToolbar, new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    super.onAnimationCancel(animation);
                                    if (mProductDetailsToolbar != null) {
                                        mProductDetailsToolbar.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if (mProductDetailsToolbar != null) {
                                        mProductDetailsToolbar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        break;
                }
            }
        }
    }

    private int getCarouselPosition() {
        if (mProductImagesRv != null) {
            int scrollX = mProductImagesRv.computeHorizontalScrollOffset();
            return Math.round(scrollX / (float) mProductImagesRv.getWidth());
        } else {
            return -1;
        }
    }

    private void updateCarouselPageIndicator(int newPosition) {
        int oldPosition = mCarouselPosition;
        mCarouselPosition = newPosition;

        SaleItemDetailsImageAdapter.ViewHolder vhOld = (SaleItemDetailsImageAdapter.ViewHolder) mOtherImagesRv.findViewHolderForLayoutPosition(oldPosition);
        if (vhOld != null && vhOld.image != null) {
            vhOld.image.setImageResource(R.drawable.circle_indicator_inactive);
        }

        SaleItemDetailsImageAdapter.ViewHolder vhNew = (SaleItemDetailsImageAdapter.ViewHolder) mOtherImagesRv.findViewHolderForLayoutPosition(newPosition);
        if (vhNew != null) {
            vhNew.image.setImageResource(R.drawable.circle_indicator_active);
        }

    }

    private void onSelectTag(int index) {
        int selectedIndex = index;
        boolean isVariant = true;

        if (selectedIndex < 0) {
            isVariant = false;
            return;
        }

        boolean isSizeSoldOut = mSkuVariants.get(selectedIndex).isSoldOut();
        if (isSizeSoldOut && !mAllowSelectingSoldoutSizes) {
            selectedIndex = mSelectedSizeIndex;
            if (selectedIndex < 0) {
                isVariant = false;
                return;
            }
            isSizeSoldOut = mSkuVariants.get(selectedIndex).isSoldOut();
        }

        // force selection - this prevents deselecting tags
        mSizesFlowLayout.getAdapter().setSelectedList(Sets.newHashSet(selectedIndex));

        mSkuId = mProductSizes.get(selectedIndex).second;

        mAddToCartButton.setText(!isSizeSoldOut ? R.string.add_to_cart : R.string.sold_out);
        mAddToCartButton.setEnabled(!isSizeSoldOut);
        mAddToCartButtonContainer.setVisibility(mAddToCartTimer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        mAddToCartButton.bringToFront();

        if (selectedIndex != mSelectedSizeIndex) {
            updatePriceDetails(mSkuVariants.get(selectedIndex));
            mSelectedSizeIndex = selectedIndex;
            if (currentBottomPopupView != null) {
                currentBottomPopupView.dismiss(true);
            }
        }

        if (mSizesNotSelectedNotice.getVisibility() == View.VISIBLE && mSelectedSizeIndex >= 0) {
            mSizesNotSelectedNotice.setVisibility(View.GONE);
        }
    }

    private SaleDetailsImageListener saleDetailsImageListener() {
        return new SaleDetailsImageListener() {
            @Override
            public void imagesLoaded() {
                if (isAttached()) {
                    mSharedImageLocation = ImageUtils.getDisplayedImageLocation(mProductSharedImage);
                    mProductSharedImage.setVisibility(View.GONE);

                    mImagesLoaded = true;

                    mProductImagesRv.setEnabled(true);
                    mOtherImagesRv.setVisibility(View.VISIBLE);
                    mProductImagesRv.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
                    mSizesContainer.setVisibility(!mProductSizes.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onImageRescale(float scale) {
                if (scale > 1.1f) { // set the threshold to 1.1 due to floating point error
                    mProductImagesRv.setZ(10);
                    mProductDetailsButtonContainer.setVisibility(View.GONE);
                    mSoldOutView.setVisibility(View.GONE);
                } else {
                    mProductImagesRv.setZ(0);
                    mProductDetailsButtonContainer.setVisibility(View.VISIBLE);
                    mSoldOutView.setVisibility(mIsSoldout ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public int getVerticalOffset() {
                return mToolbarVerticalOffset;
            }

            @Override
            public void toggleClipPadding(boolean isClipped) {
                mProductCoordinatorLayout.setClipChildren(isClipped);
                mProductCoordinatorLayout.setClipToPadding(isClipped);

            }
        };
    }

    private void getPreviewShippingPrice(CharSequence postcode, int operation) {
        if (postcode != null && postcode.length() > 0 &&
                mSkuId != null && !mSkuId.isEmpty() &&
                mSalePrice != null && !mSalePrice.isEmpty()) {
            int weight = 0;
            int width = 0;
            int height = 0;
            if (mAttributes != null) {
                weight = mAttributes.getWeight() == null ? 0 : mAttributes.getWeight().intValue();
                width = mAttributes.getWidth() == null ? 0 : mAttributes.getWidth().intValue();
                height = mAttributes.getHeight() == null ? 0 : mAttributes.getHeight().intValue();
            }
            showCalculateShippingPriceActivityIndicator(true);
            mPresenter.loadPreviewShippingPrice(
                    postcode.toString(),
                    mSkuId,
                    Float.parseFloat(mSalePrice.substring(Settings.getSelectedCountry().currencySign.length())),
                    weight,
                    width,
                    height,
                    operation);
        }
    }

    private void showCalculateShippingPriceActivityIndicator(boolean show) {
        if (show) {
            mShippingCalculateActivityIndicator.setVisibility(View.VISIBLE);
            mShippingCalculateButton.setText("");
            mShippingCalculateButton.setEnabled(false);
        } else {
            mShippingCalculateActivityIndicator.setVisibility(View.GONE);
            mShippingCalculateButton.setText("Ok");
            mShippingCalculateButton.setEnabled(true);
        }
    }

    @OnClick(R.id.product_details_shipping_postcode_button)
    public void onClickShippingPricePreview() {
        mActivity.hideKeyboard();
        final String postcode = mShippingPostcodeInput.getText() == null ? "" : mShippingPostcodeInput.getText().toString();
        getPreviewShippingPrice(postcode, DeliveryPriceViewEventRequest.OPERATION_BY_CLICK);
    }

    @Override
    public void showDefaultPostcode(String postcode) {
        final String oldPostcode = mShippingPostcodeInput.getText() == null ? "" : mShippingPostcodeInput.getText().toString();
        if (postcode != null && !postcode.isEmpty() && oldPostcode.isEmpty()) {
            mShippingPostcodeInput.setText(postcode);
        }
        // this callback will only take place when postcode form is to be shown
        mShippingContainer.setVisibility(View.VISIBLE);
        mShippingPostcodeHeader.setVisibility(View.VISIBLE);
        mShippingPostcodeContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPreviewShippingPrice(GetPostcodeShippingPriceResponse response, String postcode, Integer operation) {
        showCalculateShippingPriceActivityIndicator(false);
        mShippingPreviewPrice.setText(null);
        final Boolean isAvailable = response == null ? null : response.isShippingAvailable();
        final Float shippingPrice = response == null ? null : response.getPrice();
        if (isAvailable != null && isAvailable && shippingPrice != null) {
            if (shippingPrice > 0) {
                mShippingPreviewPrice.setText(PriceUtils.getPriceStringValue(shippingPrice));
            } else {
                mShippingPreviewPrice.setText(getFreeShippingSpan());
            }
        }
        mShippingPostcodeNotAvailable.setVisibility(isAvailable != null && !isAvailable ? View.VISIBLE : View.GONE);
        mShippingPreviewPrice.setVisibility(mShippingPreviewPrice.getText() != null && mShippingPreviewPrice.getText().length() > 0 ? View.VISIBLE : View.GONE);

        if (response != null && operation != null) {
            if (postcode == null) {
                postcode = mShippingPostcodeInput.getText() == null ? "" : mShippingPostcodeInput.getText().toString();
            }
            logDeliveryPriceViewEvent(operation, postcode, response);
        }
    }

    @Override
    public void showPostcodeForm(boolean show) {
        if (hasLoadedPostcodeForm) {
            return;
        }
        hasLoadedPostcodeForm = true;
        if (show) {
            mPresenter.loadDefaultPostcode();
        } else {
            mShippingPostcodeHeader.setVisibility(View.GONE);
            mShippingPostcodeContainer.setVisibility(View.GONE);
            logDeliveryPriceViewEvent(mIsFreeDelivery ? DeliveryPriceViewEventRequest.OPERATION_NONE : DeliveryPriceViewEventRequest.OPERATION_AUTO,
                    null,
                    null);
        }
    }

    private CharSequence getFreeShippingSpan() {
        final int color = mActivity.getResources().getColor(R.color.free_shipping_color);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int imagePosition = spannableStringBuilder.length();
        String freeShippingString = "   " +
                mActivity.getResources().getString(R.string.free_shipping_text).toUpperCase();
        spannableStringBuilder.append(
                freeShippingString,
                new StyleSpan(BOLD),
                SPAN_EXCLUSIVE_INCLUSIVE);

        spannableStringBuilder.setSpan(
                new ForegroundColorSpan(color),
                0,
                spannableStringBuilder.length(),
                SPAN_EXCLUSIVE_INCLUSIVE);

        Drawable d = ContextCompat.getDrawable(mActivity, R.drawable.ic_free_shipping);
        if (d != null) {
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(d, DynamicDrawableSpan.ALIGN_BASELINE);
            spannableStringBuilder.setSpan(imageSpan, imagePosition + 1, imagePosition + 2, SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannableStringBuilder;
    }

    private void logDeliveryPriceViewEvent(int operation, String postcode, GetPostcodeShippingPriceResponse response) {
        DeliveryPriceViewEventRequest.DeliveryPriceInfo info = new DeliveryPriceViewEventRequest.DeliveryPriceInfo();
        info.setOperation(operation);
        info.setPostcode(postcode);
        info.setFreeShipping(mIsFreeDelivery);
        if (operation != DeliveryPriceViewEventRequest.OPERATION_NONE) {
            if (response != null) {
                info.setDeliveryPrice(response.getPrice());
                info.setShippingAvailable(response.isShippingAvailable());
                if (response.getAdditional() != null) {
                    info.setShippingPolicyName(response.getAdditional().getShippingPolicyName());
                    info.setShippingPolicyId(response.getAdditional().getShippingPolicyId());
                }
            }
            info.setSupplierId(mSupplierId);
            info.setProductId(mMasterProductId);
        }
        DeliveryPriceViewEventRequest request = new DeliveryPriceViewEventRequest();
        request.setEventType(EventTypeId.EVENT_DELIVERY_PRICE_VIEW);
        request.setDeliveryPriceInfo(info);

        HashMap<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, SaleItemsController.class.getSimpleName());
        eventParameters.put(DataCollector.EventParameters.DELIVERY_PRICE_VIEW_EVENT_REQUEST, request);

        DataCollector.logEvent(Events.DeliveryPriceViewEvent, eventParameters);
    }

    private void logSellerLinkEvent(String sellerName, String productId, String storeId) {
        SellerLinkEventRequest.SellerInfo sellerInfo = new SellerLinkEventRequest.SellerInfo();
        sellerInfo.setSellerName(sellerName);
        sellerInfo.setProductId(productId);
        sellerInfo.setStoreId(storeId);

        SellerLinkEventRequest request = new SellerLinkEventRequest();
        request.setEventType(EventTypeId.EVENT_SELLER_LINK);
        request.setSellerInfo(sellerInfo);

        HashMap<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, SaleItemsController.class.getSimpleName());
        eventParameters.put(DataCollector.EventParameters.SELLER_LINK_EVENT_REQUEST, request);

        DataCollector.logEvent(Events.SellerLinkEvent, eventParameters);
    }

    private SaleItemProductPriceBlockHelper getPriceBlockHelper() {
        if (priceBlockHelper == null) {
            if (priceBlockContainer != null) {
                priceBlockHelper = new SaleItemProductPriceBlockHelper(priceBlockContainer);
            }
        }
        return priceBlockHelper;
    }

    private void onItemTapped(GetYouMayAlsoLikeResponse response, int position, int size) {

        YouMayAlsoLikeEventRequest YouMayAlsoLikeEventRequest = new YouMayAlsoLikeEventRequest();
        YouMayAlsoLikeEventRequest.setEventType(EventTypeId.EVENT_YOU_MAY_ALSO_LIKE);

        YouMayAlsoLikeEventRequest.RecommendationsViewInfo recommendationsViewInfo = new YouMayAlsoLikeEventRequest.RecommendationsViewInfo();
        recommendationsViewInfo.setType(EventTypeId.EVENT_SIMS);
        recommendationsViewInfo.setProductId(response.getMasterProductId());
        recommendationsViewInfo.setProductsQty(size);

        recommendationsViewInfo.setPosition(String.valueOf(position));
        YouMayAlsoLikeEventRequest.setRecommendationsViewInfo(recommendationsViewInfo);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.SALE_NAME, response.getName());
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, "Product Details");
        parameters.put(DataCollector.EventParameters.YOU_MAY_ALSO_LIKE_REQUEST, YouMayAlsoLikeEventRequest);
        DataCollector.logEvent(Events.YouMayAlsoLikeEvent, parameters);

        mProductDetailScrollView.smoothScrollTo(0, 0);
        mProductDiscountPogTextView.setVisibility(View.GONE);
        loadProductDetails(null, response.getSeoIdentifier());
    }

    private void onItemTapped(RecommendedItemsResponse response, int position, int size) {
        RecommendationEventRequest recommendationEventRequest = new RecommendationEventRequest();
        recommendationEventRequest.setEventType(EventTypeId.EVENT_RECOMMENDATION_VIEW);

        RecommendationEventRequest.RecommendationViewInfo recommendationViewInfo = new RecommendationEventRequest.RecommendationViewInfo();
        recommendationViewInfo.setProductId(response.getId());
        recommendationViewInfo.setProductsQty(size);
        recommendationViewInfo.setType(EventRecommendedField.HRNN);
        recommendationViewInfo.setPosition(EventRecommendedField.HRNN_PRODUCT_POSITION);

        recommendationEventRequest.setRecommendationViewInfo(recommendationViewInfo);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.RECOMMENDATION_EVENT_REQUEST, recommendationEventRequest);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, "Product Details");

        DataCollector.logEvent(Events.RecommendationClickEvent, parameters);

        mProductDetailScrollView.smoothScrollTo(0, 0);
        mProductDiscountPogTextView.setVisibility(View.GONE);
        loadProductDetails(null, response.getSeoIdentifier());
    }

    private void onItemTapped(RecentlyViewedItemResponse responseLike, int position, int size) {
        RecentlyViewedEventRequest recentlyViewedEventRequest = new RecentlyViewedEventRequest();
        recentlyViewedEventRequest.setEventType(EventTypeId.EVENT_RECENTLY_VIEWED);

        RecentlyViewedEventRequest.RecentlyViewedInfo recentlyViewedInfo = new RecentlyViewedEventRequest.RecentlyViewedInfo();
        recentlyViewedInfo.setProductId(responseLike.getId());
        recentlyViewedInfo.setProductsQty(size);
        recentlyViewedEventRequest.setRecentlyViewInfo(recentlyViewedInfo);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.RECENTLY_VIEWED_REQUEST, recentlyViewedEventRequest);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, "Product Details");
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SALE_NAME, responseLike.getName());

        DataCollector.logEvent(Events.RecentlyViewed, parameters);

        mProductDetailScrollView.smoothScrollTo(0, 0);
        mProductDiscountPogTextView.setVisibility(View.GONE);
        loadProductDetails(null, responseLike.getSeoIdentifier());
    }

    private static String getMasterProductId(SaleItemProduct item) {
        if (item instanceof SaleItemDetails) {
            return ((SaleItemDetails) item).getAttributes().getProductId();
        } else if (item instanceof GetYouMayAlsoLikeResponse) {
            return ((GetYouMayAlsoLikeResponse) item).getMasterProductId();
        } else if (item instanceof RecommendedItemsResponse) {
            return ((RecommendedItemsResponse) item).getMasterProductId();
        } else if (item instanceof RecentlyViewedItemResponse) {
            return ((RecentlyViewedItemResponse) item).getMasterProductId();
        } else {
            return "";
        }
    }

    private static String getPricingInfo(String rrpText, String pricingText) {
        String pricingInfoText = rrpText;
        if (pricingInfoText != null && !pricingInfoText.isEmpty()) {
            pricingInfoText += "<br/><br/>";
        }
        if (pricingInfoText == null) {
            pricingInfoText = pricingText;
        } else {
            pricingInfoText += pricingText;
        }
        return pricingInfoText;
    }

    private void showItemPricingInfoView(String pricingInfoText) {
        if (currentBottomPopupView != null) {
            currentBottomPopupView.dismiss(true);
        }
        final BottomPopupWebViewContentAdapter adapter = new BottomPopupWebViewContentAdapter();
        currentBottomPopupView = new BottomPopupView(mProductCoordinatorLayout, adapter);

        adapter.setWebViewContent(pricingInfoText);
        adapter.setOnCloseButtonClickListener(() -> currentBottomPopupView.dismiss(true));
        adapter.setWebViewClientOverrideUrlLoading(url -> {
            if (!url.contains("about:blank")) {
                ActivityLaunchUtil.launchActivity(mActivity, url);
            }
            return true;
        });
        currentBottomPopupView.show(true);
    }

    private void updatePricingInfoViewContent(String pricingInfoText) {
        if (currentBottomPopupView == null) {
            return;
        }

        if (currentBottomPopupView.getAdapter() instanceof BottomPopupWebViewContentAdapter) {
            final BottomPopupWebViewContentAdapter adapter = (BottomPopupWebViewContentAdapter) currentBottomPopupView.getAdapter();
            adapter.setWebViewContent(pricingInfoText);
        }
    }
}
