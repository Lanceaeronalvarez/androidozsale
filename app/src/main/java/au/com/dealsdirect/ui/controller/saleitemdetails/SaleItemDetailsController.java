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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.LoadImagesListener;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.main.MainMvpView;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.widget.ElasticDragDismissFrameLayout;
import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/*
 * Created by smartwave on 08/06/2017.
 */

public class SaleItemDetailsController extends BaseController implements SaleItemDetailsMvpView, LoadImagesListener {

    private final String KEY_ITEM_ID = "KEY_ITEM_ID";
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
    private String mItemId;
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


    private String mHtmlHeader = "";
    private String mHtmlFooter = "";

    private SaleItemDetailsImageAdapter mSaleItemImagesAdapter;
    private SaleItemDetailsImageAdapter mSaleItemImagesIndicatorAdapter;

    private LoadImagesListener loadImagesListener;

    private TagAdapter<Pair<String, String>> mSizesAdapter;
    private ArrayList<Pair<String, String>> mProductSizes = new ArrayList<>();

    private boolean hasSizes = false;
    private boolean didSelectSize = false;
    private String selectedSkuId = "";

    private GetSaleItemDetailsResponse mProductDetailsItem;

    private final ElasticDragDismissFrameLayout.ElasticDragDismissCallback dragDismissListener
            = new ElasticDragDismissFrameLayout.ElasticDragDismissCallback() {
        @Override
        public void onDragDismissed() {
            setResultAndFinish();
        }
    };

    private final ImageUtils.ImageLoadedCallback onGlideLoadedOnBackListener
            = new ImageUtils.ImageLoadedCallback() {
        @Override
        public void onImageResourceReady() {
            super.onImageResourceReady();

            mProductSharedImage.setVisibility(View.VISIBLE);
            mProductImagesRv.setVisibility(View.INVISIBLE);
            mOtherImagesRv.setVisibility(View.INVISIBLE);

            final Intent resultData = new Intent();
            resultData.putExtra(RESULT_EXTRA_CONTROLLER_ID, getInstanceId());
            getActivity().setResult(RESULT_OK, resultData);
            getActivity().finishAfterTransition();
        }
    };

    public SaleItemDetailsController(
            String seoIdentifierId, String imageUrl, String itemId, String saleId) {
        this(new BundleBuilder(new Bundle())
                .putString("KEY_IMAGE_ID", imageUrl)
                .putString("KEY_SEO_IDENTIFIER", seoIdentifierId)
                .putString("KEY_ITEM_ID", itemId)
                .putString("KEY_SALE_ID", saleId)
                .build());
    }

    public static SaleItemDetailsController newInstance(Bundle bundle) {
        return new SaleItemDetailsController(bundle);
    }

    public static SaleItemsController newInstance(
            String seoIdentifierId,
            String imageUrl,
            String itemId,
            String saleId) {

        return new SaleItemsController(
                new BundleBuilder(new Bundle())
                        .putString("KEY_IMAGE_ID", imageUrl)
                        .putString("KEY_SEO_IDENTIFIER", seoIdentifierId)
                        .putString("KEY_ITEM_ID", itemId)
                        .putString("KEY_SALE_ID", saleId)
                        .build());
    }

    public SaleItemDetailsController(Bundle args) {
        super(args);
        mSaleId = args.getString(KEY_SALE_ID);
        mItemId = args.getString(KEY_ITEM_ID);
        selectedSkuId = mItemId;
        mItemImageUrl = args.getString(KEY_ITEM_IMAGE_ID);
        mSeoIdentifierId = args.getString(KEY_SEO_IDENTIFIER_ID);
        mSaleName = args.getString(KEY_ITEM_NAME);
        mSalePrice = args.getString(KEY_ITEM_PRICE);
        mSaleOldPrice = args.getString(KEY_ITEM_OLD_PRICE);

        Log.d("LogBundle", mSaleName + " , " + mSalePrice + " , " + mSaleOldPrice);
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

        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_to_bottom);
        anim.setDuration(200);

        //product info
        mProductName.setText(mSaleName);
        mProductName.startAnimation(anim);

        mProductPrice.startAnimation(anim);
        mProductPrice.setText(mSalePrice);
        mProductPreviousPrice.setText(mSaleOldPrice);
        mProductPreviousPrice.startAnimation(anim);
        mProductPreviousPrice.setPaintFlags(
                mProductPreviousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mProductSharedImage.setVisibility(View.VISIBLE);

        mProductDetailBottomCard.setVisibility(View.VISIBLE);
        mProductDetailBottomCard.startAnimation(anim);
        mProductSharedImage.setVisibility(View.VISIBLE);

        mProductDetailBottomCard.setVisibility(View.VISIBLE);
        mProductDetailBottomCard.startAnimation(anim);


        //noinspection ConstantConditions
        ((ElasticDragDismissFrameLayout) view).addListener(dragDismissListener);

        loadImagesListener = this;
        mProductSharedImage.setTransitionName("transition");
        ImageUtils.loadImageImmediate(getActivity(), mItemImageUrl, mProductSharedImage, null);

        mPresenter.loadSaleItemDetails(mSeoIdentifierId);

        mOtherImagesRv.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false));

        mSaleItemImagesIndicatorAdapter = new SaleItemDetailsImageAdapter(this, loadImagesListener, null, mSaleId, 2);
        mOtherImagesRv.setAdapter(mSaleItemImagesIndicatorAdapter);

        LinearLayoutManager mProductImagesRvLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mProductImagesRv.setLayoutManager(mProductImagesRvLayoutManager);
        mSaleItemImagesAdapter = new SaleItemDetailsImageAdapter(this, loadImagesListener, null, mSaleId, 1);
        mProductImagesRv.setAdapter(mSaleItemImagesAdapter);

        mProductImagesRv.addOnPageChangedListener((i, i1) -> {
            SaleItemDetailsImageAdapter.ViewHolder vhNew = (SaleItemDetailsImageAdapter.ViewHolder) mOtherImagesRv.findViewHolderForLayoutPosition(i1);
            vhNew.image.setImageResource(R.drawable.circle_indicator_active);

            SaleItemDetailsImageAdapter.ViewHolder vhOld = (SaleItemDetailsImageAdapter.ViewHolder) mOtherImagesRv.findViewHolderForLayoutPosition(i);
            vhOld.image.setImageResource(R.drawable.circle_indicator_inactive);
        });

        mHtmlHeader = getActivity().getResources()
                .getString(R.string.base_html_template_header);
        mHtmlFooter = getActivity().getResources()
                .getString(R.string.base_html_template_footer);


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

        mProductBrand.setText(saleDetail.getBrandName());

        if (shippingInformation != null) {
            mProductPricing.setVisibility(View.VISIBLE);
            mProductAboutPricing.startAnimation(anim);
            mProductAboutPricing.loadData(mHtmlHeader + shippingPricing + mHtmlFooter,
                    "text/html; charset=UTF-8",
                    null);

        } else {
            mProductPricing.setVisibility(View.GONE);

        }

        if (shippingInformation != null) {
            mShippingDescText.setVisibility(View.VISIBLE);
            mShippingDescText.startAnimation(anim);
            mShippingDescText.loadData(mHtmlHeader + shippingInformation + mHtmlFooter,
                    "text/html; charset=UTF-8",
                    null);
        } else {
            mShippingContainer.setVisibility(View.GONE);
        }

        List<String> qualitySaleImages = getQualityImages(saleDetail.getImages());

        mSaleItemImagesAdapter.replaceData(qualitySaleImages);
        mSaleItemImagesIndicatorAdapter.replaceData(qualitySaleImages);

        if (saleDetail.getImages()
                .size() != 0) {
            mOtherImagesRv.setVisibility(View.VISIBLE);
        }
        //bind UI values here


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
                if (!size.isEmpty()) {
                    mProductSizes.add(new Pair<>(size, skuId));
                }
            }
        }

        if (!(mProductSizes.get(0) == null)) {

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
        }

        mSizesFlowLayout.setOnSelectListener(selectPosSet -> {
            if (selectPosSet.size() != 0) {
                selectedSkuId = mProductSizes.get(selectPosSet.iterator()
                        .next()).second;
                didSelectSize = true;
            } else {
                didSelectSize = false;
            }
        });

    }

    @Override
    public void showAddToCartResponse(boolean val) {
        //notify bottom navigation view(checkout) with success.
    }


    @OnClick(R.id.product_details_add_to_basket)
    void addToBasket() {

        if (!mPresenter.isAuthorized()) {
            ((MainMvpView) getActivity()).showLoginController(getRouter(), new AuthHandler() {
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
        AddToCartRequest request = new AddToCartRequest(selectedSkuId);

        if (hasSizes) {
            if (!didSelectSize) {
                CustomAlertDialog.showCustomAlertDialog(
                        getActivity(),
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        getActivity().getString(R.string.please_select_size));
            } else {
                mPresenter.addToCart(request);
            }
        } else {
            mPresenter.addToCart(request);
        }
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

    private void setResultAndFinish() {

        mProductDetailScrollView.scrollTo(0, 0);
        ImageUtils.loadImageImmediate(getActivity(), mItemImageUrl, mProductSharedImage, onGlideLoadedOnBackListener);
//        mProductSharedImage.setVisibility(View.VISIBLE);
//        mProductImagesRv.setVisibility(View.INVISIBLE);
//        mOtherImagesRv.setVisibility(View.INVISIBLE);


//        final Handler handler = new Handler();
//        handler.postDelayed(() -> {
//
//            final Intent resultData = new Intent();
//            resultData.putExtra(RESULT_EXTRA_CONTROLLER_ID, getInstanceId());
//            getActivity().setResult(RESULT_OK, resultData);
//            getActivity().finishAfterTransition();
//
//        }, 200);
    }

    @Override
    public void imagesLoaded() {
        if (mProductSharedImage != null) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (getActivity() != null) {
                    ImageUtils.clearImage(getActivity(), mProductSharedImage);
                    if (mProductImagesRv != null) {
                        mProductImagesRv.setVisibility(View.VISIBLE);
                    }
                }
            }, 500);
        }
    }


    public void readyViewsForTransition() {

        ImageUtils.loadImageImmediate(getActivity(), mItemImageUrl, mProductSharedImage, null);

    }
}
