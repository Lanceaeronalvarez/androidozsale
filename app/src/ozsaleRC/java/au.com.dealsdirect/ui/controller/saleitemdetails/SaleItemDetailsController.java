package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.events.ProductViewRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.Personalisation;
import au.com.dealsdirect.service.afterpay.AfterpayPanelViewHolder;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.EventTypeId;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPanel;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.floatingimageviewer.FloatingImageViewerController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.LoadImagesListener;
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.SaleDetailsImageListener;
import au.com.dealsdirect.ui.custom.ArcTranslateAnimation;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.PersonalisationLayout;
import au.com.dealsdirect.utils.ActivityLaunchUtil;
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
import au.com.dealsdirect.widget.ElasticDragDismissFrameLayout;
import butterknife.BindView;
import butterknife.OnClick;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;

/*
 * Created by smartwave on 08/06/2017.
 */

public class SaleItemDetailsController extends BaseController implements SaleItemDetailsMvpView, LoadImagesListener, SaleDetailsImageListener {

    public abstract static class Parameters {
        private Parameters() {
        }

        public static final class FromItemsList extends Parameters {
            private Integer mPosition;
            private String mImageURL;
            private String mSeoIdentifierId;
            private String mSkuId;
            private String mSaleId;
            private String mProductName;
            private String mProductBrand;
            private String mPrice;
            private String mOldPrice;
            private String mSalesOrigin;
            private String mEndDate;
            private boolean mIsFreeDelivery;

            public FromItemsList(Integer position,
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
                                 boolean isFreeDelivery) {
                mPosition = position;
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
            }

            public Integer getPosition() {
                return mPosition;
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
        }

        public static final class FromDeepLink extends Parameters {
            private String mSeoIdentifierId;
            private String mSkuId;

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

    @Inject
    SaleItemDetailsMvpPresenter<SaleItemDetailsMvpView> mPresenter;

    private String mSaleId;
    private String mSkuId;
    private String mItemImageUrl;
    private String mSeoIdentifierId;
    private String mSaleName;
    private String mSalePrice;
    private String mSaleOldPrice;
    private String mBrandName;
    private Ourpay mOurpay;
    private List<GetSaleItemDetailsResponse> mSkuVariants = new ArrayList<>();
    private String mEndDate;
    private boolean mIsFreeDelivery;
    private CountDownTimer mCountDownTimer;

    private boolean shouldAfterpayDetailsBeVisible = false;

    @BindView(R.id.arrow_left)
    View mLeftView;
    @BindView(R.id.productImageRecyclerView)
    RecyclerView mProductImagesRv;
    @BindView(R.id.otherImagesRecyclerView)
    RecyclerView mOtherImagesRv;
    @BindView(R.id.productName)
    TextView mProductName;
    @BindView(R.id.productBrand)
    TextView mProductBrand;
    @BindView(R.id.productPrice)
    TextView mProductPrice;
    @BindView(R.id.productPreviousPrice)
    TextView mProductPreviousPrice;
    @BindView(R.id.product_details_sizes_container)
    ViewGroup mSizesContainer;
    @BindView(R.id.product_details_size_list)
    TagFlowLayout mSizesFlowLayout;
    @BindView(R.id.product_details_personalisation_layout)
    PersonalisationLayout mPersonalisationLayout;
    @BindView(R.id.product_details_shipping_desc_container)
    LinearLayout mShippingContainer;
    @BindView(R.id.product_details_shipping_desc_webview)
    WebView mShippingDescText;
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
    LinearLayout mProductDetailBottomCard;
    @BindView(R.id.product_details_name_price_container)
    LinearLayout mProductPriceCategory;
    @BindView(R.id.about_pricing_container)
    LinearLayout mProductPricingContainer;
    @BindView(R.id.product_about_container)
    LinearLayout mProductAboutContainer;
    @BindView(R.id.product_details_return_policy_container)
    LinearLayout mReturnPolicyContainer;
    @BindView(R.id.partial_item_details_ourpay_panel_holder)
    LinearLayout mOurpayHolder;
    @BindView(R.id.partial_item_details_afterpay_panel_holder)
    LinearLayout mAfterpayHolder;
    @BindView(R.id.controller_sale_item_detail_scrollview)
    NestedScrollView mProductDetailScrollView;
    @BindView(R.id.product_details_add_to_basket)
    Button mAddToCartButton;
    @BindView(R.id.product_details_button_overlay)
    ImageView mAddToCartOverlay;
    @BindView(R.id.productPreviousPriceLabel)
    TextView mProductPreviousPriceLabel;

    @BindView(R.id.controller_image_frame_layout)
    RelativeLayout mProductDetailsImageLayout;
    @BindView(R.id.controller_sale_details_toolbar)
    RelativeLayout mProductDetailsToolbar;
    @BindView(R.id.controller_product_details_title_description)
    LinearLayout mProductDetailsTitleLayout;
    @BindView(R.id.toolbar_item_brand)
    TextView mToolbarItemBrandTextView;
    @BindView(R.id.toolbar_item_name)
    TextView mToolbarItemNameTextView;
    @BindView(R.id.controller_details_price_info)
    ImageButton mPriceInfoButton;
    @BindView(R.id.controller_details_old_price_info)
    ImageButton mOldPriceInfoButton;
    @BindView(R.id.product_about_old_pricing_text)
    WebView mOldProductPricing;
    @BindView(R.id.main_layout)
    LinearLayout mMainContentLayout;
    @BindView(R.id.product_details_image_animate)
    ImageView mImageViewToAnimate;
    @BindView(R.id.product_details_add_to_basket_timer)
    LinearLayout mAddToCartTimer;
    @BindView(R.id.product_details_timer)
    TextView mTimerTextView;
    @BindView(R.id.product_details_free_delivery)
    ImageView mFreeDeliveryImageView;
    @BindView(R.id.product_details_percent_off)
    TextView mProductDiscountTextView;
    @BindView(R.id.controller_product_details_button_container)
    RelativeLayout mProductDetailsButtonContainer;
    int[] mSharedImageLocation;

    private static final int SPANNABLE_STRING_START_INDEX = 6;
    private static final float DISCOUNT_VALUE_SCALE_FACTOR = 1.8f;

    LinearLayoutManager mProductImagesRvLayoutManager;

    private String mHtmlHeader = "";
    private String mHtmlFooter = "";

    private LoadImagesListener mLoadImagesListener;
    private boolean mImagesLoaded = false;

    private ArrayList<Pair<String, String>> mProductSizes = new ArrayList<>();

    private boolean mHasSizes = false;
    private int mSelectedSizeIndex = -1;
    private boolean mAllowSelectingSoldoutSizes = false;
    private int mFromPosition = -1;
    private int mToolbarVerticalOffset;
    private boolean mIsSoldOutCombined = true;
    private int mAttempts = 0;
    private String mProductId;

    private boolean hasLoadedDetails = false;

    //default sales origin
    private String mOrigin = DataCollector.EventParameters.ViewSource.SALE;

    int[] mCheckoutLocation = new int[2];
    boolean isAnimating = false;

    ElasticDragDismissFrameLayout mRootView;
    View mCheckoutView;
    AHBottomNavigation mBottomNavView;

    private int mDefaultHeight;
    private boolean mHasSavedInstance = false;

    private int mCarouselPosition = 0;
    private static final int CAROUSEL_VELOCITY_THRESHOLD = 100;

    private ElasticDragDismissFrameLayout.ElasticDragDismissCallback mDragDismissListener;

    private Map<String, String> mSavedPersonalizationData;
    private SaleDetailsImageListener mSaleDetailsImageListener;

    final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new
            ViewTreeObserver.OnScrollChangedListener() {

                @Override
                public void onScrollChanged() {
                    if (mProductDetailScrollView != null) {
                        SaleItemDetailsController.this.onScrollChanged(mProductDetailScrollView.getScrollY());
                    }
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

        if (parameters instanceof Parameters.FromItemsList) {
            controller.mSaleId = ((Parameters.FromItemsList) parameters).getSaleId();
            controller.mSkuId = ((Parameters.FromItemsList) parameters).getSkuId();
            controller.mItemImageUrl = ((Parameters.FromItemsList) parameters).getImageURL();
            controller.mSeoIdentifierId = ((Parameters.FromItemsList) parameters).getSeoIdentifierId();
            controller.mSaleName = ((Parameters.FromItemsList) parameters).getProductName();
            controller.mBrandName = ((Parameters.FromItemsList) parameters).getProductBrand();
            controller.mSalePrice = ((Parameters.FromItemsList) parameters).getPrice();
            controller.mSaleOldPrice = ((Parameters.FromItemsList) parameters).getOldPrice();
            controller.mFromPosition = ((Parameters.FromItemsList) parameters).getPosition();
            String origin = ((Parameters.FromItemsList) parameters).getSalesOrigin();
            controller.mEndDate = ((Parameters.FromItemsList) parameters).getEndDate();
            controller.mIsFreeDelivery = ((Parameters.FromItemsList) parameters).getIsFreeDelivery();
            controller.mOrigin = origin != null ? origin : DataCollector.EventParameters.ViewSource.SALE;
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
        mActivity.setDraggableViewPager(false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity.getProfiler().setStartLogTime(DataCollector.EventParameters.CustomEventType.CV_ITEMDETAILS.getValue());
        setUp(view);
    }

    @Override
    public void onDetach(View view) {
        mSavedPersonalizationData = mPersonalisationLayout.getDataForAddToCart();
        super.onDetach(view);
    }

    @Override
    public void onOrientationChanged(Configuration newConfiguration) {
        mProductDetailScrollView.scrollTo(0, 0);
        stretchImageView();
        if (mOurpay != null) {
            showMyPayDetails(null, mOurpay);
        }
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);

        mPresenter.loadSaleItemDetails(mSaleId, mSeoIdentifierId);
        if (mHasSavedInstance) {
            mActivity.getMainController().getHomeController().setSavedCurrentItem();
        }
    }

    @Override
    public void refreshContents() {
        super.refreshContents();

        if (!hasLoadedDetails) {
            mPresenter.loadSaleItemDetails(mSaleId, mSeoIdentifierId);
        }
        stretchImageView();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setUp(View view) {

        if (view instanceof ElasticDragDismissFrameLayout) {
            mRootView = ((ElasticDragDismissFrameLayout) view);

            mRootView.setDragActivationAreaWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            mRootView.setDragActivationAreaHeight(Float.MAX_VALUE);
            mRootView.setDragDismissScale(0.40f);
            mRootView.setDragVerticalThreshold(8);
        }

        stretchImageView();

        if (mBrandName != null && !mBrandName.isEmpty()) {
            mProductBrand.setText(mSaleName);
            mProductName.setText("");
        } else {
            mProductBrand.setText(mBrandName);
            mProductName.setText(mSaleName);
        }

        mProductPrice.setText(mSalePrice);
        mProductPreviousPrice.setText(mSaleOldPrice);
        mProductPreviousPrice.setPaintFlags(
                mProductPreviousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

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

        mLoadImagesListener = this;
        mSaleDetailsImageListener = this;
        mProductSharedImage.setTransitionName(getResources().getString(R.string.transition_sale_image_indexed, mFromPosition));

        ImageUtils.loadImageImmediate(mItemImageUrl, mProductSharedImage, null);

        mOtherImagesRv.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        SaleItemDetailsImageAdapter mSaleItemImagesIndicatorAdapter = new SaleItemDetailsImageAdapter(mActivity, mPresenter.isTablet(),
                mProductDetailScrollView, null, mLoadImagesListener, new ArrayList<>(), 2, null, this,
                mSaleDetailsImageListener);
        mOtherImagesRv.setAdapter(mSaleItemImagesIndicatorAdapter);
        mOtherImagesRv.setVisibility(View.INVISIBLE);

        mProductImagesRvLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mProductImagesRv.setLayoutManager(mProductImagesRvLayoutManager);

        ArrayList<View> toggledViews = new ArrayList<View>() {{
            add(mLeftView);
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

        SaleItemDetailsImageAdapter mSaleItemImagesAdapter = new SaleItemDetailsImageAdapter(mActivity, mPresenter.isTablet(), null,
                toggledViews, mLoadImagesListener, new ArrayList<>(), 1, mProductSharedImage.getDrawable(), this,
                mSaleDetailsImageListener);
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

        mHtmlHeader = mActivity.getResources()
                .getString(R.string.base_html_template_header);
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

    }

    private void setupSaleRemainingTime(String endDate) {
        mCountDownTimer = new CountDownTimer(DateUtils.getRemainingTimeInMillis(endDate), DateUtils.DATE_UTIL_MILLIS_TO_SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerTextView.setText(DateUtils.getRemainingTimeValue(millisUntilFinished));
            }

            @Override
            public void onFinish() {
            }
        };
        mCountDownTimer.start();
    }

    private void stretchImageView() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mProductDetailsImageLayout.getLayoutParams();
        int bottomNavHeight = mActivity.getMainController().getHomeController().getBottomNavigationView().getHeight();

        int screenAllowanceSize = mPresenter.isTablet() ? bottomNavHeight * 3 : bottomNavHeight * 2 + (int) getDimension(R.dimen.margin_extra_small);
        mDefaultHeight = ScreenUtils.getScreenHeight(mActivity) - screenAllowanceSize;

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
        mPriceInfoButton.setOnClickListener(null);
        mOldPriceInfoButton.setOnClickListener(null);
        mProductImagesRv.setOnFlingListener(null);
        mProductImagesRv.setOnTouchListener(null);
        mProductImagesRv.setLayoutManager(null);
        mProductImagesRv.setAdapter(null);
        mOtherImagesRv.setAdapter(null);
//      not setting this to null may cause leak, but the library doesn't support setting this to null
//      mSizesFlowLayout.setAdapter(null);
        mSizesFlowLayout.setOnSelectListener(null);
        mProductDescriptionText.setWebViewClient(null);
        if (mCountDownTimer != null) mCountDownTimer.cancel();
        super.onDestroyView(view);
    }

    private void updatePriceDetails(GetSaleItemDetailsResponse saleDetail) {
        //update Price
        mProductPrice.setText(PriceUtils.getPriceStringValue(saleDetail.getPrice().getValue()));
        mProductPreviousPrice.setText(PriceUtils.getRpStringValue(saleDetail.getOriginalPrice().getValue()));

        //update Images
        List<String> qualitySaleImages = getQualityImages(saleDetail.getImages());
        ((SaleItemDetailsImageAdapter) mProductImagesRv.getAdapter()).replaceData(qualitySaleImages);
        ((SaleItemDetailsImageAdapter) mOtherImagesRv.getAdapter()).replaceData(qualitySaleImages);

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

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void showSaleDetails(GetSaleItemDetailsResponse saleDetail) {

        mProductId = saleDetail.getAttributes().getProductId();

        mSkuId = saleDetail.getSkuId();

        mActivity.getProfiler().setEndLogTime(DataCollector.EventParameters.CustomEventType.CV_ITEMDETAILS.getValue());

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

        mPresenter.getDynamicDiscount(saleDetail.getSkuId());

        mFreeDeliveryImageView.setVisibility(mIsFreeDelivery ? View.VISIBLE : View.GONE);

        Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_to_bottom);
        anim.setDuration(200);

        String personalisation = saleDetail.getPersonalisation();
        String deliveryInformation = saleDetail.getDeliveryInformation();
        String shippingInformation = saleDetail.getShippingInformation();
        String shippingPricing = saleDetail.getPricing();
        String rrpPricing = saleDetail.getRrpText();
        String returnPolicy = saleDetail.getReturnPolicy();
        String productAbout = saleDetail.getAttributes() == null ? "" : saleDetail.getAttributes().getBrandDescription() == null ? "" : saleDetail.getAttributes().getBrandDescription();
        String name = saleDetail.getName() == null ? "" : saleDetail.getName();
        String brandName = saleDetail.getBrandName() == null ? "" : saleDetail.getBrandName();

        if (brandName != null && !brandName.isEmpty()) {
            mToolbarItemBrandTextView.setText(brandName);
            mToolbarItemNameTextView.setText(name.trim() + " • " + PriceUtils.getPriceStringValue(saleDetail.getPrice().getValue()));
            mProductName.setText(name.trim());
            mProductBrand.setText(brandName.trim());
        } else {
            mToolbarItemBrandTextView.setText(name.trim());
            mToolbarItemNameTextView.setText(PriceUtils.getPriceStringValue(saleDetail.getPrice().getValue()));
            mProductBrand.setText(name.trim());
        }

        if (personalisation != null) {
            mPersonalisationLayout.inflateForProductDetails(mActivity, new Gson().fromJson(
                    personalisation, Personalisation.class));
            if (mSavedPersonalizationData != null) {
                mPersonalisationLayout.populateFieldsWithDataFromAddToCart(mSavedPersonalizationData);
                mSavedPersonalizationData = null;
            }
        }

        if (shippingInformation != null) {

            mShippingContainer.setVisibility(View.VISIBLE);
            mShippingDescText.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mShippingDescText.startAnimation(anim);

            if (deliveryInformation == null) {
                mShippingDescText.loadDataWithBaseURL(null, mHtmlHeader + shippingInformation + mHtmlFooter,
                        "text/html", "UTF-8", null);
            } else {
                mShippingDescText.loadDataWithBaseURL(null, mHtmlHeader + deliveryInformation + "<br/><br/>" + shippingInformation + mHtmlFooter,
                        "text/html", "UTF-8", null);
            }

            mPriceInfoButton.setOnClickListener(view -> toggleProductInfoWebView(shippingPricing, true));

            mOldPriceInfoButton.setOnClickListener(view -> toggleProductInfoWebView(rrpPricing, false));

        }

        if (returnPolicy != null) {
            mReturnPolicyContainer.setVisibility(View.VISIBLE);
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

        mProductDescriptionText.startAnimation(anim);
        mProductDescriptionText.loadDataWithBaseURL(null, mHtmlHeader + saleDetail.getDescription() + mHtmlFooter,
                "text/html", "UTF-8", null);

        mProductDescriptionText.getSettings()
                .setJavaScriptEnabled(true);

        mProductDescriptionText.getSettings()
                .setDomStorageEnabled(true);

        mProductDescriptionText.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                ActivityLaunchUtil.launchActivity(mActivity, url);
                return true;
            }

        });

        if (saleDetail.getSkuVariants() != null && !saleDetail.getSkuVariants().isEmpty()) {
            mSkuVariants = saleDetail.getSkuVariants();
            mProductSizes.clear();
            for (GetSaleItemDetailsResponse skuVariant : mSkuVariants) {
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
                    if (mSkuVariants.get(position).isSoldOut()) {
                        tv.setBackground(getDrawable(R.drawable.bg_chips_soldout));
                        tv.setTextColor(getColor(R.color.bg_chips_soldout_text));
                    }

                    return tv;
                }
            };

            mIsSoldOutCombined = true;
            for (GetSaleItemDetailsResponse response : mSkuVariants) {
                if (!response.isSoldOut()) {
                    mIsSoldOutCombined = false;
                    break;
                }
            }

            mSizesFlowLayout.setAdapter(mSizesAdapter);
            if (mSelectedSizeIndex >= 0) {
                mSizesFlowLayout.getAdapter().setSelectedList(Sets.newHashSet(mSelectedSizeIndex));
            }

            mSizesFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                @Override
                public boolean onTagClick(View view, int position, FlowLayout parent) {

                    return false;
                }
            });

            mSizesFlowLayout.setOnSelectListener(selectPosSet -> {
                onSelectTag(selectPosSet.isEmpty() ? -1 : selectPosSet.iterator().next());
            });

            // auto-select size if mProductSizes equals to 1
            if (mProductSizes.size() == 1) {
                onSelectTag(0);
            }

        }

        if (!mIsSoldOutCombined || !saleDetail.isSoldOut()) {
            if (mActivity.getResources().getBoolean(R.bool.is_sale_countdown_timer_enabled) &&
                    (mEndDate != null && !mEndDate.isEmpty()) &&
                    DateUtils.getRemainingTimeInMillis(mEndDate) >= 0 &&
                    DateUtils.isLessThanADay(DateUtils.getRemainingTimeInMillis(mEndDate))) {
                setupSaleRemainingTime(mEndDate);
                mAddToCartTimer.setVisibility(View.VISIBLE);
                mAddToCartButton.setVisibility(View.GONE);
            } else {
                mAddToCartTimer.setVisibility(View.GONE);
                mAddToCartButton.setVisibility(View.VISIBLE);
                mAddToCartButton.setText(R.string.add_to_cart);
                mAddToCartButton.setEnabled(true);
                mAddToCartButton.bringToFront();
            }
        } else {
            mAddToCartButton.setText(R.string.sold_out);
            mAddToCartButton.setVisibility(View.VISIBLE);
            mAddToCartButton.setEnabled(false);
            mAddToCartButton.bringToFront();
        }

        boolean isOldPriceInfoVisible = saleDetail.getOriginalPrice().getValue() <= 0;
        mOldPriceInfoButton.setVisibility(isOldPriceInfoVisible ? View.GONE : View.VISIBLE);
        mProductPreviousPrice.setVisibility(isOldPriceInfoVisible ? View.GONE : View.VISIBLE);
        mProductPreviousPriceLabel.setVisibility(isOldPriceInfoVisible ? View.GONE : View.VISIBLE);

        updatePriceDetails(saleDetail);

        hasLoadedDetails = true;
    }

    private void toggleProductInfoWebView(String shippingPricing, boolean isNewPricing) {
        boolean isPricingContainerVisible = mProductPricingContainer.getVisibility() == View.VISIBLE;
        if (isNewPricing) {
            mProductAboutPricing.loadDataWithBaseURL(null, mHtmlHeader + shippingPricing + mHtmlFooter,
                    "text/html", "UTF-8", null);
            mProductAboutPricing.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mProductPricingContainer.setVisibility(isPricingContainerVisible && mProductAboutPricing.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        } else {
            mOldProductPricing.loadDataWithBaseURL(null, mHtmlHeader + shippingPricing + mHtmlFooter,
                    "text/html", "UTF-8", null);

            mOldProductPricing.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mProductPricingContainer.setVisibility(isPricingContainerVisible && mOldProductPricing.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
        mProductAboutPricing.setVisibility(isNewPricing ? View.VISIBLE : View.GONE);
        mOldProductPricing.setVisibility(!isNewPricing ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAddToCartResponse(Value cartDetailsResponse) {

        if (mSharedImageLocation == null) {
            mSharedImageLocation = ImageUtils.getDisplayedImageLocation(mProductSharedImage);
        }
        animateAddToCart();

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

        mAttempts = 0;
        //notify bottom navigation view(checkout) with success.
        CartUtil.addValueToCart(1);
        mActivity.getMainController().getHomeController().updateBasketItemCount();

        CustomAlertDialog.showCustomAlertDialog(
                mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
                mActivity.getString(R.string.add_to_cart_success));

        mActivity.getHomeController().sendSaleItemToCheckout(cartDetailsResponse);
    }

    @Override
    public void showAddToCartResponseFailed() {
        CustomAlertDialog.showCustomAlertDialog(
                mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                mActivity.getString(R.string.add_to_cart_failed));
    }

    @Override
    public void showMyPayDetails(GetSaleItemDetailsResponse value, Ourpay ourpay) {
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

            if (mActivity.getHomeController().getPopUpHostRouter() != null) {
                mActivity.getHomeController().getPopUpHostRouter().setRoot(routerTransaction);
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
    public int getVerticalOffset() {
        return mToolbarVerticalOffset;
    }

    @Override
    public void toggleClipPadding(boolean isClipped) {
        mProductCoordinatorLayout.setClipChildren(isClipped);
        mProductCoordinatorLayout.setClipToPadding(isClipped);

    }

    @Override
    public void setDynamicDiscount(String discountText) {
        if (discountText == null) {
            mProductDiscountTextView.setVisibility(View.GONE);
            return;}
        String percentOffText = discountText.trim();
        String[] discountWordArray = discountText.split(" ");
        percentOffText = percentOffText.replace(' ', '\n');

        int percentSymbolLength = 1;
        int spannableStringEndParameter = SPANNABLE_STRING_START_INDEX + discountWordArray[1].length() + percentSymbolLength;
        SpannableString string = new SpannableString(percentOffText);
        string.setSpan(new StyleSpan(BOLD), SPANNABLE_STRING_START_INDEX, spannableStringEndParameter, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new RelativeSizeSpan(DISCOUNT_VALUE_SCALE_FACTOR), SPANNABLE_STRING_START_INDEX, spannableStringEndParameter, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mProductDiscountTextView.setVisibility(View.VISIBLE);
        mProductDiscountTextView.setText(string);

    }

    @Override
    public void setIsAfterpayDetailsVisible(boolean visible) {
        shouldAfterpayDetailsBeVisible = visible;
        mAfterpayHolder.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean handleBack() {
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
        mAttempts++;

        CommonUtils.saveSaleItem(mActivity, mProductId, mSeoIdentifierId, mSaleId);

        AddToCartRequest request = new AddToCartRequest();
        request.setSkuId(mSkuId);
        request.setItemName(mSaleName);
        request.setPrice(Double.valueOf(mSalePrice.substring(Settings.getSelectedCountry().currencySign.length())));
        request.setPersonalizationData(mPersonalisationLayout.getDataForAddToCart());

        boolean isSizeValid = !(mHasSizes && mSelectedSizeIndex < 0);

        boolean isPersonalisationValid = mPersonalisationLayout.verifyRequiredFields();

        String personalisationError = !mPresenter.getPersonalisationErrorText().equals("") ?
                mPresenter.getPersonalisationErrorText() :
                mActivity.getString(R.string.please_fill_up_personalisation_details);

        if (!isSizeValid || !isPersonalisationValid) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    !isSizeValid ? mActivity.getString(R.string.please_select_size) :
                            personalisationError);

            mProductDetailScrollView.scrollTo(0, mProductDetailBottomCard.getTop());
        } else {
            verifyAddToCart(request);
        }
    }

    private void verifyAddToCart(AddToCartRequest request) {
        if (!mPresenter.isAuthorized()) {
            mActivity.showLoginController(getRouter(), new AuthHandler() {
                @Override
                public void success() {
                    mActivity.getMainController().getHomeController().resetRouters();
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

    private void animateAddToCart() {
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

        mBottomNavView = mActivity.getMainController().getHomeController().getBottomNavigationView();
        ArrayList<View> potentialViews = new ArrayList<View>();
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
                mImageViewToAnimate.setVisibility(View.GONE);
                mBottomNavView.setElevation(origElevation);
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

    @OnClick(R.id.toolbar_left_view)
    void backPress() {
        dismissArrowDown();
    }

    @OnClick(R.id.arrow_left)
    void dismissArrowDown() {
        mActivity.onBackPressed();
    }

    public void onScrollChanged(int scrollY) {
        if (isViewAttached()) {
            mRootView.setIsHorizontalDismissEnabled(scrollY <= 0 && getCarouselPosition() == 0);
            mRootView.setIsVerticalDismissEnabled(scrollY <= 0);
            boolean isScrollGreater = scrollY >= ScreenUtils.getScreenHeight(mActivity) -
                    (mProductDetailsTitleLayout.getBottom() + mActivity.getMainController().getHomeController().getBottomNavigationView().getHeight());
            mProductDetailsToolbar.setVisibility(isScrollGreater && !mPresenter.isTablet() ? View.VISIBLE : View.GONE);
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

        // if selection is invalid, try reselecting previous index
        if (index < 0) {
            if (mSelectedSizeIndex >= 0) {
                selectedIndex = mSelectedSizeIndex;
            } else {
                return;
            }
        }

        boolean isSizeSoldOut = mSkuVariants.get(selectedIndex).isSoldOut();
        if (isSizeSoldOut && !mAllowSelectingSoldoutSizes) {
            // if current selection is sold out, try selecting previous index
            onSelectTag(-1);
            return;
        }

        // force selection - this prevents deselecting tags
        mSizesFlowLayout.getAdapter().setSelectedList(Sets.newHashSet(selectedIndex));

        mSkuId = mProductSizes.get(selectedIndex).second;

        mAddToCartButton.setText(!isSizeSoldOut ? R.string.add_to_cart : R.string.sold_out);
        mAddToCartButton.setEnabled(!isSizeSoldOut);
        mAddToCartButton.setVisibility(mAddToCartTimer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        mAddToCartButton.bringToFront();

        if (selectedIndex != mSelectedSizeIndex) {
            updatePriceDetails(mSkuVariants.get(selectedIndex));
            mSelectedSizeIndex = selectedIndex;
        }
    }

    @Override
    public void scaleImage(boolean hideImage) {
        if (!hideImage) {
            mProductImagesRv.setZ(10);
            mProductDetailsButtonContainer.setVisibility(View.GONE);
        } else {
            mProductImagesRv.setZ(0);
            mProductDetailsButtonContainer.setVisibility(View.VISIBLE);
        }
    }
}
