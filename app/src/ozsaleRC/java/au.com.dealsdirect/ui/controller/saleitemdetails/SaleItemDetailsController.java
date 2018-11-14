package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.mysale.genie.profiler.Profiler;
import com.mysale.genie.utility.RxBus;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.Personalisation;
import au.com.dealsdirect.service.event.ActionTracker;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPanel;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.LoadImagesListener;
import au.com.dealsdirect.ui.custom.ArcTranslateAnimation;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.PersonalisationLayout;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.widget.ElasticDragDismissFrameLayout;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by smartwave on 08/06/2017.
 */

public class SaleItemDetailsController extends BaseController implements SaleItemDetailsMvpView, LoadImagesListener {

    @Inject
    SaleItemDetailsMvpPresenter<SaleItemDetailsMvpView> mPresenter;

    private String mSaleId;
    private String mSkuId;
    private String mItemImageUrl;
    private String mSeoIdentifierId;
    private String mSaleName;
    private String mSalePrice;
    private String mSaleOldPrice;
    private Ourpay mOurpay;
    private List<GetSaleItemDetailsResponse> mSkuVariants = new ArrayList<>();

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

    int[] mSharedImageLocation;

    LinearLayoutManager mProductImagesRvLayoutManager;

    private String mHtmlHeader = "";
    private String mHtmlFooter = "";

    private LoadImagesListener mLoadImagesListener;
    private boolean mImagesLoaded = false;

    private ArrayList<Pair<String, String>> mProductSizes = new ArrayList<>();

    private boolean mHasSizes = false;
    private boolean mDidSelectSize = false;
    private int mFromPosition = -1;
    private int mToolbarVerticalOffset;
    private boolean mIsSoldOutCombined = true;
    private int mAttempts = 0;

    //default sales origin
    private String mOrigin = ActionTracker.ViewSource.SALE;

    int[] mCheckoutLocation = new int[2];
    boolean isAnimating = false;

    ElasticDragDismissFrameLayout mRootView;
    View mCheckoutView;
    AHBottomNavigation mBottomNavView;

    private int mDefaultHeight;
    private boolean mHasSavedInstance = false;
    private ControllerChangeHandler.ControllerChangeListener newControllerChangeHandler;

    private int mCarouselPosition = 0;
    private static final int CAROUSEL_VELOCITY_THRESHOLD = 100;

    private ElasticDragDismissFrameLayout.ElasticDragDismissCallback mDragDismissListener;

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
        mOrigin = args.getString(BundleKeys.SALEITEMDETAILS_KEY_SALE_ORIGIN, ActionTracker.ViewSource.SALE);
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
        mActivity.getProfiler().setStartLogTime(ActionTracker.CustomEventType.CV_ITEMDETAILS.getValue());
        setUp(view);
    }

    @Override
    public void onDetach(View view) {
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
    protected void setUp(View view) {

        if (view instanceof ElasticDragDismissFrameLayout) {
            mRootView = ((ElasticDragDismissFrameLayout) view);
        }

        stretchImageView();

        //product info
        mProductName.setText(mSaleName);

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
        mProductSharedImage.setTransitionName(getResources().getString(R.string.transition_sale_image_indexed, mFromPosition));

        ImageUtils.loadImageImmediate(mItemImageUrl, mProductSharedImage, null);

        if (mHasSavedInstance) {
            SaleItemDetailsController currentController = this;
            newControllerChangeHandler = new ControllerChangeHandler.ControllerChangeListener() {

                @Override
                public void onChangeStarted(@Nullable Controller to,
                                            @Nullable Controller from, boolean isPush,
                                            @NonNull ViewGroup container,
                                            @NonNull ControllerChangeHandler handler) {

                }

                @Override
                public void onChangeCompleted(@Nullable Controller to,
                                              @Nullable Controller from, boolean isPush,
                                              @NonNull ViewGroup container,
                                              @NonNull ControllerChangeHandler handler) {
                    if (to == currentController) {
                        mPresenter.loadSaleItemDetails(mSeoIdentifierId);
                        mActivity.getMainController().getHomeController().setSavedCurrentItem();
                    }
                }
            };
            getRouter().addChangeListener(newControllerChangeHandler);
        } else {
            mPresenter.loadSaleItemDetails(mSeoIdentifierId);
        }

        mOtherImagesRv.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        SaleItemDetailsImageAdapter mSaleItemImagesIndicatorAdapter = new SaleItemDetailsImageAdapter(mActivity, mPresenter.isTablet(),
                mProductDetailScrollView, null, mLoadImagesListener, new ArrayList<>(), 2, null, this);
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
            add(mProductDetailBottomCard);
            add(mAddToCartOverlay);
            add(mMainContentLayout);
        }};

        if (mPresenter.isTablet()) toggledViews.add(mAddToCartOverlay);

        SaleItemDetailsImageAdapter mSaleItemImagesAdapter = new SaleItemDetailsImageAdapter(mActivity, mPresenter.isTablet(), null,
                toggledViews, mLoadImagesListener, new ArrayList<>(), 1, mProductSharedImage.getDrawable(), this);
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
                }
                return false;
            }
        });
        mProductImagesRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        updateCarouselPageIndicator(getCarouselPosition());
                        break;
                    default:
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
                    }
                    else if (!observer.isAlive()) {
                        observer.removeOnScrollChangedListener(onScrollChangedListener);
                        observer = mProductDetailScrollView.getViewTreeObserver();
                        observer.addOnScrollChangedListener(onScrollChangedListener);
                    }

                    return false;
                }
            });
        }
    }

    private void stretchImageView() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mProductDetailsImageLayout.getLayoutParams();
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
        if (newControllerChangeHandler != null) {
            getRouter().removeChangeListener(newControllerChangeHandler);
            newControllerChangeHandler = null;
        }
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
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void showSaleDetails(GetSaleItemDetailsResponse saleDetail) {

        mActivity.getProfiler().setEndLogTime(ActionTracker.CustomEventType.CV_ITEMDETAILS.getValue());
        mActionTracker.CVItemDetails(Profiler.getTotalTime(ActionTracker.CustomEventType.CV_ITEMDETAILS.getValue()));

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

        mToolbarItemBrandTextView.setText(brandName);
        mToolbarItemNameTextView.setText(name.trim() + " • " + PriceUtils.getPriceStringValue(saleDetail.getPrice().getValue()));

        mProductName.setText(name.trim());
        mProductBrand.setText(brandName.trim());

        if (personalisation != null) {
            mPersonalisationLayout.inflateForProductDetails(mActivity, new Gson().fromJson(
                    personalisation, Personalisation.class));
        }

        if (shippingInformation != null) {

            mShippingContainer.setVisibility(View.VISIBLE);
            mShippingDescText.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mShippingDescText.startAnimation(anim);

            if (deliveryInformation == null) {
                mShippingDescText.loadData(mHtmlHeader + shippingInformation + mHtmlFooter, "text/html; charset=UTF-8", null);
            } else {
                mShippingDescText.loadData(mHtmlHeader + deliveryInformation + "<br/><br/>" + shippingInformation + mHtmlFooter, "text/html; charset=UTF-8", null);
            }

            mPriceInfoButton.setOnClickListener(view -> toggleProductInfoWebView(shippingPricing, true));

            mOldPriceInfoButton.setOnClickListener(view -> toggleProductInfoWebView(rrpPricing, false));

        }

        if (returnPolicy != null) {
            mReturnPolicyContainer.setVisibility(View.VISIBLE);
            mReturnPolicyText.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mReturnPolicyText.startAnimation(anim);
            mReturnPolicyText.loadData(mHtmlHeader + returnPolicy + mHtmlFooter, "text/html; charset=UTF-8", null);
        }

        if (!productAbout.isEmpty()) {
            mProductAboutContainer.setVisibility(View.VISIBLE);
            mProductAboutText.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mProductAboutText.startAnimation(anim);
            mProductAboutText.loadData(mHtmlHeader + productAbout + mHtmlFooter, "text/html; charset=UTF-8", null);
        }

        mProductDescriptionText.startAnimation(anim);
        mProductDescriptionText.loadData(mHtmlHeader + saleDetail.getDescription() + mHtmlFooter,
                "text/html; charset=UTF-8",
                null);

        mProductDescriptionText.getSettings()
                .setJavaScriptEnabled(true);

        mProductDescriptionText.getSettings()
                .setDomStorageEnabled(true);

        mProductDescriptionText.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                return true;
            }

        });

        if (saleDetail.getSkuVariants() != null && !saleDetail.getSkuVariants().isEmpty()) {
            mSkuVariants = saleDetail.getSkuVariants();
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

            //modify Initial state depending on the result of all variant's isSoldOut
            mAddToCartButton.setText(!mIsSoldOutCombined ? R.string.add_to_cart : R.string.sold_out);
            mAddToCartButton.setEnabled(!mIsSoldOutCombined);
            mAddToCartButton.bringToFront();

            mSizesFlowLayout.setAdapter(mSizesAdapter);

            mSizesFlowLayout.setOnSelectListener(selectPosSet -> {
                if (selectPosSet.size() != 0) {
                    int selectedIndex = selectPosSet.iterator().next();

                    mSkuId = mProductSizes.get(selectedIndex).second;

                    boolean isSizeSoldOut = mSkuVariants.get(selectedIndex).isSoldOut();

                    if (isSizeSoldOut) return;

                    mAddToCartButton.setText(!isSizeSoldOut ? R.string.add_to_cart : R.string.sold_out);
                    mAddToCartButton.setEnabled(!isSizeSoldOut);
                    mAddToCartButton.bringToFront();

                    mDidSelectSize = true;

                    updatePriceDetails(mSkuVariants.get(selectedIndex));
                } else {
                    mDidSelectSize = false;
                }
            });

        }

        boolean isOldPriceInfoVisible = saleDetail.getOriginalPrice().getValue() <= 0;
        mOldPriceInfoButton.setVisibility(isOldPriceInfoVisible ? View.GONE : View.VISIBLE);
        mProductPreviousPrice.setVisibility(isOldPriceInfoVisible ? View.GONE : View.VISIBLE);
        mProductPreviousPriceLabel.setVisibility(isOldPriceInfoVisible ? View.GONE : View.VISIBLE);

        updatePriceDetails(saleDetail);
    }

    private void toggleProductInfoWebView(String shippingPricing, boolean isNewPricing) {
        boolean isPricingContainerVisible = mProductPricingContainer.getVisibility() == View.VISIBLE;
        if (isNewPricing) {
            mProductAboutPricing.loadData(mHtmlHeader + shippingPricing + mHtmlFooter, "text/html; charset=UTF-8", null);
            mProductAboutPricing.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mProductPricingContainer.setVisibility(isPricingContainerVisible && mProductAboutPricing.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        } else {
            mOldProductPricing.loadData(mHtmlHeader + shippingPricing + mHtmlFooter, "text/html; charset=UTF-8", null);
            mOldProductPricing.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mProductPricingContainer.setVisibility(isPricingContainerVisible && mOldProductPricing.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
        mProductAboutPricing.setVisibility(isNewPricing ? View.VISIBLE : View.GONE);
        mOldProductPricing.setVisibility(!isNewPricing ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAddToCartResponse(Value cartDetailsResponse) {
        RxBus.instance().post(IntrospectionUtils.EVENT_ADD_TO_CART);
        mActionTracker.addToCartEvent(mOrigin, mAttempts);
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
    public boolean handleBack() {
        if (!isAnimating) {
            mProductDetailScrollView.scrollTo(0, 0);
            if (mRootView != null) {
                mRootView.removeListener(mDragDismissListener);
            }
            mDragDismissListener = null;
            mProductImagesRv.setVisibility(View.GONE);
            return false;
        }
        return true;
    }

    @OnClick(R.id.product_details_add_to_basket)
    void addToBasket() {
        mAttempts++;
        AddToCartRequest request = new AddToCartRequest();
        request.setSkuId(mSkuId);
        request.setItemName(mSaleName);
        request.setPrice(Double.valueOf(mSalePrice.substring(1)));
        request.setPersonalizationData(mPersonalisationLayout.getDataForAddToCart());

        boolean isSizeValid = !(mHasSizes && !mDidSelectSize);

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
                    if (mSharedImageLocation != null) {
                        new Handler().postDelayed(() -> animateAddToCart(), 1000);
                    }
                }

                @Override
                public void error() {

                }
            });
        } else {
            mPresenter.addToCart(request);
            if (mSharedImageLocation == null) { mSharedImageLocation = ImageUtils.getDisplayedImageLocation(mProductSharedImage); }
            animateAddToCart();
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

        if (mImagesLoaded) {
            if (vh == null) throw new AssertionError("Viewholder cannot be null");
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
                (float) middle[0], (float)middle[1],
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
            ImageUtils.clearImage(mProductSharedImage);

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
}
