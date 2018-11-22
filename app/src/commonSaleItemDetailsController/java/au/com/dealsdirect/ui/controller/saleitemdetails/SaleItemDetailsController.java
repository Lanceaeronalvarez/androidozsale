package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
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
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPanel;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.LoadImagesListener;
import au.com.dealsdirect.ui.custom.ArcTranslateAnimation;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.widget.ElasticDragDismissFrameLayout;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by smartwave on 08/06/2017.
 */

public class SaleItemDetailsController extends BaseController implements SaleItemDetailsMvpView, LoadImagesListener {

    private final String KEY_POSITION = "KEY_POSITION";
    private final String KEY_SKU_ID = "KEY_SKU_ID";
    private final String KEY_SALE_ID = "KEY_SALE_ID";
    private final String KEY_ITEM_IMAGE_ID = "KEY_IMAGE_ID";
    private final String KEY_SEO_IDENTIFIER_ID = "KEY_SEO_IDENTIFIER";
    private final String KEY_ITEM_NAME = "KEY_SALE_NAME";
    private final String KEY_ITEM_PRICE = "KEY_SALE_PRICE";
    private final String KEY_ITEM_OLD_PRICE = "KEY_SALE_OLD_PRICE";

    private final String KEY_COUNTRY_ID = "KEY_COUNTRY_ID";
    private final String KEY_LANGUAGE_ID = "KEY_LANGUAGE_ID";
    private final String KEY_USER_GROUP = "KEY_USER_GROUP";
    private final String KEY_GET_BIG_IMAGES = "KEY_GET_BIG_IMAGES";
    private final String KEY_INCLUDE_PRICES = "KEY_INCLUDE_PRICES";

    public final static String RESULT_EXTRA_CONTROLLER_ID = "SALE_ITEM_DETAILS_ID";

    @Inject
    SaleItemDetailsMvpPresenter<SaleItemDetailsMvpView> mPresenter;

    private String mSaleId;
    private String mSkuId;
    private String mItemImageUrl;
    private String mSeoIdentifierId;
    private String mSaleName;
    private String mSalePrice;
    private String mSaleOldPrice;
    private List<GetSaleItemDetailsResponse> mSkuVariants = new ArrayList<>();

    @BindView(R.id.productImageRecyclerView)
    RecyclerViewPager mProductImagesRv;
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
    @BindView(R.id.sizes)
    RelativeLayout mSizesContainer;
    @BindView(R.id.sizeList)
    TagFlowLayout mSizesFlowLayout;
    @BindView(R.id.shipping_desc_container)
    LinearLayout mShippingContainer;
    @BindView(R.id.shipping_desc_text)
    WebView mShippingDescText;
    @BindView(R.id.product_description_text)
    WebView mProductDescriptionText;
    @BindView(R.id.product_about_pricing_text)
    WebView mProductAboutPricing;
    @BindView(R.id.product_about)
    WebView mProductAboutText;
    @BindView(R.id.return_policy_text)
    WebView mReturnPolicyText;
    @BindView(R.id.product_details_shared_image)
    ImageView mProductSharedImage;
    @BindView(R.id.product_details_coordinator)
    RelativeLayout mProductCoordinatorLayout;
    @BindView(R.id.bottom_card)
    LinearLayout mProductDetailBottomCard;
    @BindView(R.id.name_price_category)
    LinearLayout mProductPriceCategory;
    @BindView(R.id.about_pricing_container)
    LinearLayout mProductPricing;
    @BindView(R.id.product_about_container)
    LinearLayout mProductAboutContainer;
    @BindView(R.id.return_policy_container)
    LinearLayout mReturnPolicyContainer;
    @BindView(R.id.partial_item_details_ourpay_panel_holder)
    LinearLayout mOurpayHolder;
    @BindView(R.id.controller_sale_item_detail_scrollview)
    NestedScrollView mProductDetailScrollView;
    @BindView(R.id.product_details_add_to_basket)
    Button mAddToCartButton;
    @BindView(R.id.product_details_button_overlay)
    ImageView mAddToCartOverlay;

    @BindView(R.id.image_container)
    FrameLayout mImageContainerViewGroup;

    ImageView mImageViewToAnimate;

    int[] mSharedImageLocation;

    LinearLayoutManager mProductImagesRvLayoutManager;

    private String mHtmlHeader = "";
    private String mHtmlFooter = "";

    private SaleItemDetailsImageAdapter mSaleItemImagesAdapter;
    private SaleItemDetailsImageAdapter mSaleItemImagesIndicatorAdapter;

    private LoadImagesListener loadImagesListener;
    private boolean imagesLoaded = false;

    private TagAdapter<Pair<String, String>> mSizesAdapter;
    private ArrayList<Pair<String, String>> mProductSizes = new ArrayList<>();

    private boolean hasSizes = false;
    private boolean didSelectSize = false;
    private boolean isSizeSoldOut = false;
    private int mFromPosition = -1;

    boolean checkOutLocated = false;

    int[] checkoutLocation = new int[2];
    boolean isAnimating = false;

    ElasticDragDismissFrameLayout mRootView;
    View mCheckoutView;
    AHBottomNavigation mBottomNavView;

    private final ElasticDragDismissFrameLayout.ElasticDragDismissCallback dragDismissListener
            = new ElasticDragDismissFrameLayout.ElasticDragDismissCallback() {
        @Override
        public void onDragDismissed() {
            mProductDetailScrollView.scrollTo(0, 0);
            mActivity.onBackPressed();
        }
    };


    public SaleItemDetailsController(
            String seoIdentifierId, String imageUrl, String skuId, String saleId) {
        this(new BundleBuilder(new Bundle())
                .putString("KEY_IMAGE_ID", imageUrl)
                .putString("KEY_SEO_IDENTIFIER", seoIdentifierId)
                .putString("KEY_SKU_ID", skuId)
                .putString("KEY_SALE_ID", saleId)
                .build());
    }

    public static SaleItemDetailsController newInstance(Bundle bundle) {
        return new SaleItemDetailsController(bundle);
    }

    public SaleItemDetailsController(Bundle args) {
        super(args);
        mSaleId = args.getString(KEY_SALE_ID);
        mSkuId = args.getString(KEY_SKU_ID, "");
        mItemImageUrl = args.getString(KEY_ITEM_IMAGE_ID);
        mSeoIdentifierId = args.getString(KEY_SEO_IDENTIFIER_ID);
        mSaleName = args.getString(KEY_ITEM_NAME);
        mSalePrice = args.getString(KEY_ITEM_PRICE);
        mSaleOldPrice = args.getString(KEY_ITEM_OLD_PRICE);
        mFromPosition = args.getInt(KEY_POSITION);
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
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        mRootView = ((ElasticDragDismissFrameLayout) view);

        //product info
        mProductName.setText(mSaleName);

        mProductPrice.setText(mSalePrice);
        mProductPreviousPrice.setText(mSaleOldPrice);
        mProductPreviousPrice.setPaintFlags(
                mProductPreviousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mProductDetailBottomCard.setVisibility(View.VISIBLE);

        //noinspection ConstantConditions
        mRootView.addListener(dragDismissListener);

        loadImagesListener = this;
        mProductSharedImage.setTransitionName(getResources().getString(R.string.transition_sale_image_indexed, mFromPosition));

//        RequestOptions options = new RequestOptions().encodeQuality(50)
//                .encodeFormat(Bitmap.CompressFormat.JPEG)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .priority(Priority.IMMEDIATE)
//                .dontAnimate()
//                .format(DecodeFormat.PREFER_RGB_565);
//
//        Glide.with(mActivity)
//                .load(mItemImageUrl)
//                .apply(options)
//                .into(mProductSharedImage);

        ImageUtils.loadImageImmediate(mItemImageUrl, mProductSharedImage, null);

        mPresenter.loadSaleItemDetails(mSeoIdentifierId);

        mOtherImagesRv.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mSaleItemImagesIndicatorAdapter = new SaleItemDetailsImageAdapter(null, loadImagesListener, new ArrayList<>(), mSaleId, 2, null);
        mOtherImagesRv.setAdapter(mSaleItemImagesIndicatorAdapter);
        mOtherImagesRv.setVisibility(View.INVISIBLE);

        mProductImagesRvLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mProductImagesRv.setLayoutManager(mProductImagesRvLayoutManager);
        mSaleItemImagesAdapter = new SaleItemDetailsImageAdapter(new ArrayList<View>() {{
            add(mOtherImagesRv);
            add(mAddToCartButton);
            add(mProductPriceCategory);
            add(mAddToCartOverlay);
        }}, loadImagesListener,
                new ArrayList<>(), mSaleId, 1, mProductSharedImage.getDrawable());
        mProductImagesRv.setAdapter(mSaleItemImagesAdapter);
        mProductImagesRv.setEnabled(false);
        mProductImagesRv.setOverScrollMode(View.OVER_SCROLL_NEVER);


        mProductImagesRv.addOnPageChangedListener((i, i1) -> {
            SaleItemDetailsImageAdapter.ViewHolder vhNew = (SaleItemDetailsImageAdapter.ViewHolder) mOtherImagesRv.findViewHolderForLayoutPosition(i1);

            if (vhNew != null) {
                vhNew.image.setImageResource(R.drawable.circle_indicator_active);
            }

            SaleItemDetailsImageAdapter.ViewHolder vhOld = (SaleItemDetailsImageAdapter.ViewHolder) mOtherImagesRv.findViewHolderForLayoutPosition(i);
            if (vhOld != null && vhOld.image != null) {
                vhOld.image.setImageResource(R.drawable.circle_indicator_inactive);
            }
        });

        mHtmlHeader = mActivity.getResources()
                .getString(R.string.base_html_template_header);
        mHtmlFooter = mActivity.getResources()
                .getString(R.string.base_html_template_footer);

        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void showSaleDetails(GetSaleItemDetailsResponse saleDetail) {

        Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_to_bottom);
        anim.setDuration(200);

        String deliveryInformation = saleDetail.getDeliveryInformation();
        String shippingInformation = saleDetail.getShippingInformation();
        String shippingPricing = saleDetail.getPricing();
        String returnPolicy = saleDetail.getReturnPolicy();
        String productAbout = saleDetail.getAttributes() == null ? "" : saleDetail.getAttributes().getBrandDescription() == null ? "" : saleDetail.getAttributes().getBrandDescription();
        String name = saleDetail.getName() == null ? "" : saleDetail.getName();
        String branName = saleDetail.getBrandName() == null ? "" : saleDetail.getBrandName();


        mProductName.setText(name.trim());
        mProductBrand.setText(branName.trim());
        mProductPrice.setText(PriceUtils.getPriceStringValue(saleDetail.getPrice().getValue()));
        mProductPreviousPrice.setText(PriceUtils.getRpStringValue(saleDetail.getOriginalPrice().getValue()));

        if (shippingInformation != null) {

            mShippingContainer.setVisibility(View.VISIBLE);
            mShippingDescText.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mShippingDescText.startAnimation(anim);

            if (deliveryInformation == null) {
                mShippingDescText.loadData(mHtmlHeader + shippingInformation + mHtmlFooter, "text/html; charset=UTF-8", null);
            } else {
                mShippingDescText.loadData(mHtmlHeader + deliveryInformation + "<br/><br/>" + shippingInformation + mHtmlFooter, "text/html; charset=UTF-8", null);
            }

            mProductPricing.setVisibility(View.VISIBLE);
            mProductAboutPricing.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mProductAboutPricing.startAnimation(anim);
            mProductAboutPricing.loadData(mHtmlHeader + shippingPricing + mHtmlFooter, "text/html; charset=UTF-8", null);

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

        List<String> qualitySaleImages = getQualityImages(saleDetail.getImages());

        mSaleItemImagesAdapter.replaceData(qualitySaleImages);
        mSaleItemImagesIndicatorAdapter.replaceData(qualitySaleImages);

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

        if (!saleDetail.getSkuVariants().isEmpty()) {
            mSkuVariants = saleDetail.getSkuVariants();
            for (GetSaleItemDetailsResponse skuVariant : saleDetail.getSkuVariants()) {
                String skuId = skuVariant.getSkuId();
                String size = skuVariant.getAttributes().getSize();
                if (size != null && !size.isEmpty()) {
                    mProductSizes.add(new Pair<>(size, skuId));
                }
            }
        }

        if (!mProductSizes.isEmpty()) {

            mSizesContainer.setVisibility(View.VISIBLE);
            hasSizes = true;

            mSizesAdapter = new TagAdapter<Pair<String, String>>(mProductSizes) {

                @SuppressWarnings("ConstantConditions")
                @Override
                public View getView(FlowLayout parent, int position, Pair<String, String> data) {
                    TextView tv = (TextView) mActivity.getLayoutInflater()
                            .inflate(R.layout.sizes_chips_layout,
                                    parent,
                                    false);
                    tv.setText(data.first);
                    return tv;
                }
            };

            mSizesFlowLayout.setAdapter(mSizesAdapter);

            mSizesFlowLayout.setOnSelectListener(selectPosSet -> {
                if (selectPosSet.size() != 0) {
                    int selectedIndex = selectPosSet.iterator().next();

                    mSkuId = mProductSizes.get(selectedIndex).second;

                    isSizeSoldOut = saleDetail.getSkuVariants().get(selectedIndex).isSoldOut();

                    mAddToCartButton.setEnabled(!isSizeSoldOut);
                    mAddToCartButton.setText(!isSizeSoldOut ? R.string.add_to_cart : R.string.sold_out);

                    didSelectSize = true;
                } else {
                    didSelectSize = false;
                }
            });
        }


        mAddToCartButton.setEnabled(true);


    /* Should only set button to 'Sold Out' if sold out size is selected
        if (saleDetail.isSoldOut()) {
            mAddToCartButton.setEnabled(false);
            mAddToCartButton.setText("Sold Out");
        }
    */

        if (saleDetail.getOriginalPrice().getValue() <= 0) {
            mProductPreviousPrice.setVisibility(View.GONE);
        } else {
            mProductPreviousPrice.setVisibility(View.VISIBLE);
        }

//        mPresenter.generateOurpay(saleDetail);
    }

    @Override
    public void showAddToCartResponse(Value addToCartDetailsResponse) {
        RxBus.instance().post(IntrospectionUtils.EVENT_ADD_TO_CART);

        //notify bottom navigation view(checkout) with success.
        if (!addToCartDetailsResponse.isEmpty()) {
            CartUtil.addValueToCart(1);
            mActivity.getMainController().getHomeController().updateBasketItemCount();

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    mActivity.getString(R.string.add_to_cart_success));
        }
    }

    @Override
    public void showAddToCartResponseFailed() {

    }

    @Override
    public void showMyPayDetails(GetSaleItemDetailsResponse value, Ourpay ourpay) {
        if (ourpay != null) {
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
        return 0;
    }

    @Override
    public void toggleClipPadding(boolean isClipped) {

    }

    @Override
    public boolean handleBack() {
        if (!isAnimating) {
            mProductDetailScrollView.scrollTo(0, 0);
            mRootView.removeListener(dragDismissListener);
            ImageUtils.loadImageImmediate(mItemImageUrl, mProductSharedImage, null);
            mProductImagesRv.setVisibility(View.GONE);
            return false;
        }
        return true;
    }

    @OnClick(R.id.product_details_add_to_basket)
    void addToBasket() {

        AddToCartRequest request = new AddToCartRequest();
        request.setSkuId(mSkuId);
        request.setItemName(mSaleName);
        request.setPrice(Double.valueOf(mSalePrice.substring(1)));

        if (hasSizes) {
            if (!didSelectSize) {
                CustomAlertDialog.showCustomAlertDialog(
                        mActivity,
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        mActivity.getString(R.string.please_select_size));

                mProductDetailScrollView.scrollTo(0, mProductDetailBottomCard.getTop());
            } else {
                verifyAddToCart(request);
            }
        } else {
            verifyAddToCart(request);
        }
    }

    private void verifyAddToCart(AddToCartRequest request) {
        if (!mPresenter.isAuthorized()) {
            mActivity.showLoginController(getRouter(), new AuthHandler() {
                @Override
                public void success() {
                    mActivity.getMainController().getHomeController().resetInviteRouter();
                    mActivity.getMainController().getHomeController().resetContactsRouter();
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
            if (mSharedImageLocation != null) {
                animateAddToCart();
            }
        }
    }

    private void animateAddToCart() {
        mProductDetailScrollView.scrollTo(0, 0);
        mImageViewToAnimate = mActivity.getMainController().getHomeController().getAddToCartImage();

        mImageViewToAnimate.setVisibility(View.VISIBLE);
        mImageViewToAnimate.setX(mSharedImageLocation[0]);
        mImageViewToAnimate.setY(mSharedImageLocation[1]);
        mImageViewToAnimate.getLayoutParams().width = mSharedImageLocation[2];
        mImageViewToAnimate.getLayoutParams().height = mSharedImageLocation[3];
        SaleItemDetailsImageAdapter.ViewHolder vh = (SaleItemDetailsImageAdapter.ViewHolder) mProductImagesRv
                .findViewHolderForLayoutPosition(mProductImagesRvLayoutManager.findLastVisibleItemPosition());

        if (imagesLoaded) {
            mImageViewToAnimate.setImageDrawable(vh.image.getDrawable());
        } else {
            mImageViewToAnimate.setImageDrawable(mProductSharedImage.getDrawable());
        }

        mImageViewToAnimate.bringToFront();

        mBottomNavView = mActivity.getMainController().getBottomNav();
        ArrayList<View> potentialViews = new ArrayList<View>();
        mBottomNavView.findViewsWithText(potentialViews, "checkout", View.FIND_VIEWS_WITH_TEXT);
//        mCheckoutView = !potentialViews.isEmpty() ? potentialViews.get(0) : null;
        mCheckoutView = mBottomNavView.getViewAtPosition(4);
        mCheckoutView.getLocationOnScreen(checkoutLocation);

        float origElevation = mBottomNavView.getElevation();
        mBottomNavView.setElevation(0);

        ArcTranslateAnimation anim = new ArcTranslateAnimation(
                700, Animation.ABSOLUTE,
                mSharedImageLocation[0],
                Animation.ABSOLUTE,
                checkoutLocation[0],
                Animation.ABSOLUTE,
                checkoutLocation[1]);

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
        int x = 0;

        for (int i = 0; i < images.size(); i++) {
            if (x == 3) {

                qualityImages.add(images.get(i));
                x = -1;
            }
            x++;
        }
        return qualityImages;
    }

    @Override
    public void imagesLoaded() {
        if (isAttached()) {
            mSharedImageLocation = ImageUtils.getDisplayedImageLocation(mProductSharedImage);
            ImageUtils.clearImage(mProductSharedImage);

            imagesLoaded = true;

            mProductImagesRv.setEnabled(true);
            mOtherImagesRv.setVisibility(View.VISIBLE);
            mProductImagesRv.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        }

    }

    public void readyViewsForTransition() {

        ImageUtils.loadImageImmediate(mItemImageUrl, mProductSharedImage, null);

    }

    @OnClick(R.id.arrow_left)
    void dismissArrowDown() {
        mActivity.onBackPressed();
    }


}