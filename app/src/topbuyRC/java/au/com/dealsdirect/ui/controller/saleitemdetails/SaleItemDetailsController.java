package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.content.Intent;
import android.content.res.TypedArray;
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

import com.jakewharton.rxbinding2.view.RxView;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.mysale.genie.utility.RxBus;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountMvpPresenter;
import au.com.dealsdirect.ui.controller.account.AccountMvpView;
import au.com.dealsdirect.ui.custom.ArcTranslateAnimation;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import au.com.dealsdirect.widget.ElasticDragDismissFrameLayout;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


/**
 * Created by smartwave on 30/10/2017.
 */

public class SaleItemDetailsController extends BaseController implements SaleItemDetailsMvpView, LoadImagesListener {

    @Inject
    SaleItemDetailsMvpPresenter<SaleItemDetailsMvpView> mPresenter;
    @Inject
    AccountMvpPresenter<AccountMvpView> mAccountsPresenter;

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
    @BindView(R.id.productCategory)
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
    @BindView(R.id.discountLabel)
    TextView mDiscountLabel;
    @BindView(R.id.cart_view)
    RelativeLayout mCartView;
    @BindView(R.id.cart_counter)
    TextView mCartCounter;

    @BindView(R.id.image_container)
    FrameLayout mImageContainerViewGroup;

    @BindView(R.id.product_details_add_to_cart_image)
    ImageView mImageViewToAnimate;

    int[] mSharedImageLocation;

    LinearLayoutManager mProductImagesRvLayoutManager;

    Disposable mCartViewClickListener;

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

    int[] cartLocation = new int[2];
    boolean isAnimating = false;

    ElasticDragDismissFrameLayout mRootView;
    View mCheckoutView;

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
        mSaleId = args.getString(BundleKeys.SALEITEMDETAILS_KEY_SALE_ID);
        mSkuId = args.getString(BundleKeys.SALEITEMDETAILS_KEY_SKU_ID, "");
        mItemImageUrl = args.getString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_IMAGE_ID);
        mSeoIdentifierId = args.getString(BundleKeys.SALEITEMDETAILS_KEY_SEO_IDENTIFIER_ID);
        mSaleName = args.getString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_NAME);
        mSalePrice = args.getString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_PRICE);
        mSaleOldPrice = args.getString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_OLD_PRICE);
        mFromPosition = args.getInt(BundleKeys.SALEITEMDETAILS_KEY_POSITION);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_sale_item_details, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mCartViewClickListener = RxView.clicks(mCartView)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> {
                    mActivity.getMainController().getHomeViewPager().setCurrentItem(2);
                });
    }

    @Override
    protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
        super.onRestoreViewState(view, savedViewState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mRootView = ((ElasticDragDismissFrameLayout) view);
        mRootView.setPadding(0, mActivity.getStatusBarHeight(), 0, 0);
        //product info
        mProductName.setText(mSaleName);

        mProductPrice.setText(mSalePrice);
        mProductPreviousPrice.setText(mSaleOldPrice);
        mProductPreviousPrice.setPaintFlags(mProductPreviousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mProductDetailBottomCard.setVisibility(View.VISIBLE);

        //noinspection ConstantConditions
        mRootView.addListener(dragDismissListener);

        loadImagesListener = this;
        mProductSharedImage.setTransitionName(getResources().getString(R.string.transition_sale_image_indexed, mFromPosition));

        ImageUtils.loadImageImmediate(mActivity, mItemImageUrl, mProductSharedImage, null);

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

        mCartCounter.setText(CartUtil.getCartValue() + "");
    }

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

        if (saleDetail.getLabelText() == null) {
            mDiscountLabel.setVisibility(View.GONE);
        } else {
            mDiscountLabel.setVisibility(View.VISIBLE);
            mDiscountLabel.setText(saleDetail.getLabelText());
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

        mProductDescriptionText.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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

        if (saleDetail.isSoldOut()) {
            mAddToCartButton.setEnabled(false);
            mAddToCartButton.setText("Sold Out");
        }

        if (saleDetail.getOriginalPrice().getValue() <= 0) {
            mProductPreviousPrice.setVisibility(View.GONE);
        } else {
            mProductPreviousPrice.setVisibility(View.VISIBLE);
        }

        mPresenter.generateOurpay(saleDetail);
    }

    @Override
    public void onDetach(View view) {
        mCartViewClickListener.dispose();
        mActivity.setDraggableViewPager(true);
        super.onDetach(view);
    }



    @Override
    public boolean handleBack() {
        if (!isAnimating) {
            mProductDetailScrollView.scrollTo(0, 0);
            mRootView.removeListener(dragDismissListener);
            ImageUtils.loadImageImmediate(mActivity, mItemImageUrl, mProductSharedImage, null);
            mProductImagesRv.setVisibility(View.GONE);
            return false;
        }
        return super.handleBack();
    }

    @Override
    public void showAddToCartResponse(boolean val) {
        RxBus.instance().post(IntrospectionUtils.EVENT_ADD_TO_CART);

        if (val) {
            CartUtil.addValueToCart(1);
            mCartCounter.setText(CartUtil.getCartValue() + "");
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    mActivity.getString(R.string.add_to_cart_success));
        }
    }

    @Override
    public void showMyPayDetails(GetSaleItemDetailsResponse value, Ourpay ourpay) {

    }

    @Override
    public void onCallGetBasketItemsQuantity() {
        mCartCounter.setText(CartUtil.getCartValue() + "");
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

    @OnClick(R.id.product_details_add_to_basket)
    void addToBasket() {

        AddToCartRequest request = new AddToCartRequest();
        request.setSkuId(mSkuId);

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

    @OnClick(R.id.cart_view)
    void goToCheckout() {
        mActivity.getMainController().getHomeViewPager().setCurrentItem(2);
    }

    private void verifyAddToCart(AddToCartRequest request) {
        if (!mPresenter.isAuthorized()) {
            mActivity.showLoginController(getRouter(), new AuthHandler() {
                @Override
                public void success() {
                    mActivity.callGCMRegisterSubscriber();
                    mPresenter.addToCart(request);
                    if (mSharedImageLocation != null) {
                        new Handler().postDelayed(() -> animateAddToCart(), 1000);
                    }

                    TypedArray title = mActivity.getResources().obtainTypedArray(R.array.account_title_array);
                    List<Integer> titles = new ArrayList<>();
                    for(int i = 0; i < title.length(); i++) {
                        titles.add(title.getResourceId(i,0));
                    }
                    TypedArray drawable = mActivity.getResources().obtainTypedArray(R.array.account_drawable_array);
                    List<Integer> drawables = new ArrayList<>();
                    for(int i = 0; i < drawable.length(); i++) {
                        drawables.add(drawable.getResourceId(i,0));
                    }

                    mAccountsPresenter.onAttach((AccountMvpView) GateKeeper.getCurrentControllerOnRouter(mActivity.getAccountsRouter()));
                    mAccountsPresenter.loadAccountItems(titles, drawables);
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

        SaleItemDetailsImageAdapter.ViewHolder vh = (SaleItemDetailsImageAdapter.ViewHolder) mProductImagesRv
                .findViewHolderForLayoutPosition(mProductImagesRvLayoutManager.findLastVisibleItemPosition());

        mImageViewToAnimate.setVisibility(View.VISIBLE);
        if (imagesLoaded) {
            mImageViewToAnimate.setImageDrawable(vh.image.getDrawable());
        } else {
            mImageViewToAnimate.setImageDrawable(mProductSharedImage.getDrawable());
        }

        mImageViewToAnimate.setElevation(5f);
        mImageViewToAnimate.bringToFront();

        mCartView.getLocationOnScreen(cartLocation);

        float origElevation = mCartView.getElevation();
        mCartView.setElevation(0);

        ArcTranslateAnimation anim = new ArcTranslateAnimation(
                700, Animation.ABSOLUTE,
                mSharedImageLocation[0],
                Animation.ABSOLUTE,
                cartLocation[0],
                Animation.ABSOLUTE,
                cartLocation[1]);

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
                mCartView.setElevation(origElevation);
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
}
