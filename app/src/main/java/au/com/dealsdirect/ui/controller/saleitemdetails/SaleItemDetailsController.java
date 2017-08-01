package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.LoadImagesListener;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.AnimationEngine;
import au.com.dealsdirect.ui.main.SharedActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.widget.ElasticDragDismissFrameLayout;
import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

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

    //product details views
    @BindView(R.id.discountLabel)
    TextView mDiscountLabel;
    @BindView(R.id.productImageRecyclerView)
    RecyclerViewPager mProductImagesRv;
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
    @BindView(R.id.product_about_pricing)
    WebView mProductAboutPricing;
    @BindView(R.id.product_details_shared_image)
    ImageView mProductSharedImage;
    @BindView(R.id.product_details_coordinator)
    RelativeLayout mProductCoordinatorLayout;
    @BindView(R.id.bottom_card)
    LinearLayout mProductDetailBottomCard;
    @BindView(R.id.name_price_category)
    LinearLayout mProductPriceCategory;
    @BindView(R.id.aboutPricing)
    LinearLayout mProductPricing;
    @BindView(R.id.controller_sale_item_detail_scrollview)
    NestedScrollView mProductDetailScrollView;
    @BindView(R.id.product_details_add_to_basket)
    Button mAddButton;

    @BindView(R.id.image_container)
    FrameLayout mImageContainerViewGroup;
    @BindView(R.id.imageViewToAnimate)
    ImageView mImageViewToAnimate;

    LinearLayoutManager mProductImagesRvLayoutManager;

    private String mHtmlHeader = "";
    private String mHtmlFooter = "";

    private SaleItemDetailsImageAdapter mSaleItemImagesAdapter;
    private SaleItemDetailsImageAdapter mSaleItemImagesIndicatorAdapter;

    private LoadImagesListener loadImagesListener;

    private TagAdapter<Pair<String, String>> mSizesAdapter;
    private ArrayList<Pair<String, String>> mProductSizes = new ArrayList<>();

    private boolean hasSizes = false;
    private boolean didSelectSize = false;
    private int mFromPosition = -1;

    boolean checkOutLocated = false;

    private SpringSystem mSpringSystem;
    private Spring addToCartAnimSpring;

    int[] checkoutLocation = new int[2];

    ElasticDragDismissFrameLayout mRootView;

    private final ElasticDragDismissFrameLayout.ElasticDragDismissCallback dragDismissListener
            = new ElasticDragDismissFrameLayout.ElasticDragDismissCallback() {
        @Override
        public void onDragDismissed() {
            mProductDetailScrollView.scrollTo(0, 0);
            getActivity().onBackPressed();
        }
    };

    private final ImageUtils.ImageLoadedCallback onGlideLoadedOnBackListener
            = new ImageUtils.ImageLoadedCallback() {
        @Override
        public void onImageResourceReady() {
            super.onImageResourceReady();

            mProductSharedImage.setVisibility(View.VISIBLE);
            mProductImagesRv.setVisibility(View.INVISIBLE);

            final Intent resultData = new Intent();
            resultData.putExtra(RESULT_EXTRA_CONTROLLER_ID, getInstanceId());
            getActivity().setResult(RESULT_OK, resultData);
            getActivity().finishAfterTransition();
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

    public static SaleItemsController newInstance(
            String seoIdentifierId,
            String imageUrl,
            String skuId,
            String saleId) {

        return new SaleItemsController(
                new BundleBuilder(new Bundle())
                        .putString("KEY_IMAGE_ID", imageUrl)
                        .putString("KEY_SEO_IDENTIFIER", seoIdentifierId)
                        .putString("KEY_ITEM_ID", skuId)
                        .putString("KEY_SALE_ID", saleId)
                        .build());
    }

    public SaleItemDetailsController(Bundle args) {
        super(args);
        mSaleId = args.getString(KEY_SALE_ID);
        mSkuId = args.getString(KEY_SKU_ID,"");
        mItemImageUrl = args.getString(KEY_ITEM_IMAGE_ID);
        mSeoIdentifierId = args.getString(KEY_SEO_IDENTIFIER_ID);
        mSaleName = args.getString(KEY_ITEM_NAME);
        mSalePrice = args.getString(KEY_ITEM_PRICE);
        mSaleOldPrice = args.getString(KEY_ITEM_OLD_PRICE);
        mFromPosition = args.getInt(KEY_POSITION);
    }


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        SaleItemDetailsView view = (SaleItemDetailsView)
                inflater.inflate(R.layout.controller_product_details, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mRootView = ((ElasticDragDismissFrameLayout) view);

        //product info
        mProductName.setText(mSaleName);

        mProductPrice.setText(mSalePrice);
        mProductPreviousPrice.setText(mSaleOldPrice);
        mProductPreviousPrice.setPaintFlags(
                mProductPreviousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mProductDetailBottomCard.setVisibility(View.VISIBLE);
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
//        Glide.with(getActivity())
//                .load(mItemImageUrl)
//                .apply(options)
//                .into(mProductSharedImage);

        ImageUtils.loadImageImmediate(getActivity(), mItemImageUrl, mProductSharedImage, null);

        mPresenter.loadSaleItemDetails(mSeoIdentifierId);

        mSaleItemImagesIndicatorAdapter = new SaleItemDetailsImageAdapter(this, loadImagesListener, null, mSaleId, 2);

        mProductImagesRvLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mProductImagesRv.setLayoutManager(mProductImagesRvLayoutManager);
        mSaleItemImagesAdapter = new SaleItemDetailsImageAdapter(this, loadImagesListener, null, mSaleId, 1);
        mProductImagesRv.setAdapter(mSaleItemImagesAdapter);

        mHtmlHeader = getActivity().getResources()
                .getString(R.string.base_html_template_header);
        mHtmlFooter = getActivity().getResources()
                .getString(R.string.base_html_template_footer);


        AHBottomNavigation bottomNavView = ((MainActivity) getActivity()).getMainController().getHomeController().getBottomNavigationView();
        bottomNavView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View checkoutView = bottomNavView.getViewAtPosition(4);
                if(checkoutView!=null && !checkOutLocated) {
                    checkoutView.getLocationOnScreen(checkoutLocation);
                    initSprings();
                    checkOutLocated = true;
                }
            }
        });




    }

    @Override
    protected void setUp(View view) {


        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_to_bottom);
        anim.setDuration(200);


    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        mProductDetailBottomCard.setVisibility(View.GONE);
        mProductPriceCategory.setVisibility(View.GONE);
        super.onDetach(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void showSaleDetails(GetSaleItemDetailsResponse saleDetail) {

        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_to_bottom);
        anim.setDuration(200);

        String shippingInformation = saleDetail.getShippingInformation();
        String shippingPricing = saleDetail.getPricing();

        mProductName.setText(saleDetail.getName().trim());
        mProductBrand.setText(saleDetail.getBrandName().trim());
        mProductPrice.setText(PriceUtils.getPriceStringValue(saleDetail.getPrice().getValue()));
        mProductPreviousPrice.setText(PriceUtils.getRpStringValue(saleDetail.getOriginalPrice().getValue()));

        if (shippingInformation != null) {
            mProductPricing.setVisibility(View.VISIBLE);
            mProductAboutPricing.startAnimation(anim);
            mProductAboutPricing.loadData(mHtmlHeader + shippingPricing + mHtmlFooter, "text/html; charset=UTF-8", null);
        } else {
            mProductPricing.setVisibility(View.GONE);
        }

        if (shippingInformation != null) {
            mShippingDescText.setVisibility(View.VISIBLE);
            mShippingDescText.startAnimation(anim);
            mShippingDescText.loadData(mHtmlHeader + shippingInformation + mHtmlFooter, "text/html; charset=UTF-8", null);
        } else {
            mShippingContainer.setVisibility(View.GONE);
        }

        List<String> qualitySaleImages = getQualityImages(saleDetail.getImages());

        mSaleItemImagesAdapter.replaceData(qualitySaleImages);

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
                    TextView tv = (TextView) getActivity().getLayoutInflater()
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
                    mSkuId = mProductSizes.get(selectPosSet.iterator()
                            .next()).second;
                    didSelectSize = true;
                } else {
                    didSelectSize = false;
                }
            });
        }


        if (saleDetail.getQuantity() <= 0) {
            mAddButton.setEnabled(false);
            mAddButton.setText("Sold Out");
        }
        mAddButton.setVisibility(View.VISIBLE);

        if (saleDetail.getOriginalPrice().getValue() <= 0) {
            mProductPreviousPrice.setVisibility(View.GONE);
        } else {
            mProductPreviousPrice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showAddToCartResponse(boolean val) {
        //notify bottom navigation view(checkout) with success.
        if (val) {
            HomeController homeController = ((MainActivity) getActivity()).getMainController().getHomeController();
            CartUtil.addValueToCart(1);
            homeController.updateBasketItemCount();
        }
    }

    @Override
    public boolean handleBack() {

        ImageUtils.loadImageImmediate(getActivity(), mItemImageUrl, mProductSharedImage, null);
        mProductImagesRv.setVisibility(View.GONE);
        mRootView.removeListener(dragDismissListener);
        return false;
    }

    @OnClick(R.id.product_details_add_to_basket)
    void addToBasket() {
        if (!mPresenter.isAuthorized()) {
            ((SharedActivity) getActivity()).showLoginController(getRouter(), new AuthHandler() {
                @Override
                public void success() {
                    verifyAddToCart();
                }

                @Override
                public void error() {

                }
            });
        } else {
            verifyAddToCart();
        }

    }

    private void verifyAddToCart() {
        AddToCartRequest request = new AddToCartRequest(mSkuId);

        if (hasSizes) {
            if (!didSelectSize) {
                CustomAlertDialog.showCustomAlertDialog(
                        getActivity(),
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        getActivity().getString(R.string.please_select_size));
            } else {
                mPresenter.addToCart(request);
                animateAddToCart();
            }
        } else {
            mPresenter.addToCart(request);
            animateAddToCart();
        }
    }

    private void animateAddToCart() {

        SaleItemDetailsImageAdapter.ViewHolder vh = (SaleItemDetailsImageAdapter.ViewHolder) mProductImagesRv
                .findViewHolderForLayoutPosition(mProductImagesRvLayoutManager.findLastVisibleItemPosition());

        mImageViewToAnimate.setImageDrawable(vh.image.getDrawable());
        mImageViewToAnimate.bringToFront();
        mImageViewToAnimate.invalidate();

        addToCartAnimSpring.setEndValue(1);

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
        ImageUtils.clearImage(getActivity(), mProductSharedImage);
    }

    public void readyViewsForTransition() {

        ImageUtils.loadImageImmediate(getActivity(), mItemImageUrl, mProductSharedImage, null);

    }

    @Override
    public void initSprings() {
        mSpringSystem = SpringSystem.create();

        addToCartAnimSpring = mSpringSystem.createSpring();
        addToCartAnimSpring.addListener(new FirstSimpleStringListener(this, addToCartAnimSpring, mImageViewToAnimate, checkoutLocation));
        addToCartAnimSpring.setSpringConfig(SpringConfig.fromBouncinessAndSpeed(0, 30));


    }

    public static class FirstSimpleStringListener extends SimpleSpringListener {
        private ImageView mImageView;
        private int[] mLocationCoords;
        private SaleItemDetailsMvpView mSaleItemsMvpView;
        private Spring mSpring;

        public FirstSimpleStringListener(SaleItemDetailsMvpView saleItemsMvpView, Spring thisSpring, ImageView imageView, int[] locationCoords) {
            mSaleItemsMvpView = saleItemsMvpView;
            mImageView = imageView;
            mLocationCoords = locationCoords;
            mSpring = thisSpring;
        }

        @Override
        public void onSpringActivate(Spring spring) {
            super.onSpringActivate(spring);
            mImageView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSpringAtRest(Spring spring) {
            super.onSpringAtRest(spring);
            if (spring.getEndValue() == 1f) {
                AnimationEngine.Builder.animate(mImageView)
                        .scales(0)
                        .translate(mLocationCoords[0], mLocationCoords[1])
                        .setDuration(200)
                        .withEndAction(() -> {
                            mImageView.setVisibility(View.INVISIBLE);
                            mImageView.setScaleX(1);
                            mImageView.setScaleY(1);
                            mImageView.setTranslationX(0);
                            mImageView.setTranslationY(0);
                            mSaleItemsMvpView.initSprings();
                        })
                        .build()
                        .start();
            }
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            float value = (float) spring.getCurrentValue();
            float scale = 1f - (value * 0.6f);
            mImageView.setScaleX(scale);
            mImageView.setScaleY(scale);
        }
    }
}
