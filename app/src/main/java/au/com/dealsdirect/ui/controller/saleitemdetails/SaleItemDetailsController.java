package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.SkuVariant;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.changehandler.DetailPopTransitionChangeHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.widget.ElasticDragDismissFrameLayout;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by smartwave on 08/06/2017.
 */

public class SaleItemDetailsController extends BaseController implements SaleItemDetailsMvpView {

    private final String KEY_ITEM_ID = "KEY_ITEM_ID";
    private final String KEY_SALE_ID = "KEY_SALE_ID";
    private final String KEY_ITEM_IMAGE_ID = "KEY_IMAGE_ID";
    private final String KEY_SEO_IDENTIFIER_ID = "KEY_SEO_IDENTIFIER";

    private final String KEY_COUNTRY_ID = "KEY_COUNTRY_ID";
    private final String KEY_LANGUAGE_ID = "KEY_LANGUAGE_ID";
    private final String KEY_USER_GROUP = "KEY_USER_GROUP";
    private final String KEY_GET_BIG_IMAGES = "KEY_GET_BIG_IMAGES";
    private final String KEY_INCLUDE_PRICES = "KEY_INCLUDE_PRICES";

    @Inject
    SaleItemDetailsMvpPresenter<SaleItemDetailsMvpView> mPresenter;

    private String mSaleId;
    private String mItemId;
    private String mItemImageUrl;

    private String mSeoIdentifierId;

    //product details views

    @BindView(R.id.discountLabel)
    TextView mDiscountLabel;
    @BindView(R.id.productImageRecyclerView)
    RecyclerViewPager mProductImagesRv;
    @BindView(R.id.otherImagesRecyclerView)
    RecyclerView mOtherImagesRv;
    @BindView(R.id.productName)
    TextView mProductName;
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

    private String mHtmlHeader = "";
    private String mHtmlFooter = "";

    private SaleItemDetailsImageAdapter mSaleItemImagesAdapter;
    private SaleItemDetailsImageAdapter mSaleItemImagesIndicatorAdapter;

    private TagAdapter<Pair<String, String>> mSizesAdapter;
    private ArrayList<Pair<String, String>> mProductSizes = new ArrayList<>();

    private boolean hasSizes = false;
    private boolean didSelectSize = false;
    private String selectedSkuId = "";

    private final ElasticDragDismissFrameLayout.ElasticDragDismissCallback dragDismissListener
            = new ElasticDragDismissFrameLayout.ElasticDragDismissCallback() {
        @Override
        public void onDragDismissed() {
            overridePopHandler(new DetailPopTransitionChangeHandler());
            getRouter().popController(SaleItemDetailsController.this);
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
        mItemImageUrl = args.getString(KEY_ITEM_IMAGE_ID);
        mSeoIdentifierId = args.getString(KEY_SEO_IDENTIFIER_ID);
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
        setRetainViewMode(RetainViewMode.RELEASE_DETACH);
        ((ElasticDragDismissFrameLayout) view).addListener(dragDismissListener);

        mProductSharedImage.setTransitionName(mItemId);
        ImageUtils.loadImage(getActivity(), mItemImageUrl, mProductSharedImage);


        assert getActivity() != null;
        ((BaseActivity) getActivity()).hideBottomNavigationView();
        //init api call

        mPresenter.loadSaleItemDetails(mSeoIdentifierId);

        mOtherImagesRv.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false));

        mSaleItemImagesIndicatorAdapter = new SaleItemDetailsImageAdapter(this, null, mSaleId, 2);
        mOtherImagesRv.setAdapter(mSaleItemImagesIndicatorAdapter);

        LinearLayoutManager mProductImagesRvLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mProductImagesRv.setLayoutManager(mProductImagesRvLayoutManager);
        mSaleItemImagesAdapter = new SaleItemDetailsImageAdapter(this, null, mSaleId, 1);
        mProductImagesRv.setAdapter(mSaleItemImagesAdapter);


        mProductImagesRv.addOnPageChangedListener((i, i1) -> {
            RecyclerView.ViewHolder vhNew = mOtherImagesRv.findViewHolderForLayoutPosition(i1);
            vhNew.itemView.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start();

            RecyclerView.ViewHolder vhOld = mOtherImagesRv.findViewHolderForLayoutPosition(i);
            vhOld.itemView.setAlpha(0.4f);
        });

        mHtmlHeader = getActivity().getResources()
                .getString(R.string.base_html_template_header);
        mHtmlFooter = getActivity().getResources()
                .getString(R.string.base_html_template_footer);

    }

    @Override
    protected void setUp(View view) {
        mProductSharedImage.setVisibility(View.VISIBLE);

    }

    @Override
    public void onDetach(View view) {

    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            ((MainActivity) getActivity()).showBottomNavigationView();

            super.onDestroyView(view);

        }, 300);

//        mProductSharedImage.setVisibility(View.VISIBLE);
//        mProductImagesRv.setVisibility(View.GONE);
//        mOtherImagesRv.setVisibility(View.GONE);
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void showSaleDetails(GetSaleItemDetailsResponse saleDetail) {

        String shippingInformation = saleDetail.getShippingInformation();

        mProductAboutPricing.loadData(mHtmlHeader + saleDetail.getPricing() + mHtmlFooter,
                "text/html; charset=UTF-8",
                null);
        mProductAboutPricing.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (shippingInformation != null) {
            mShippingDescText.loadData(mHtmlHeader + shippingInformation + mHtmlFooter,
                    "text/html; charset=UTF-8",
                    null);

        } else {
            mShippingContainer.setVisibility(View.GONE);
        }


        final android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(() -> {
            if (mProductImagesRv != null) {
                mProductImagesRv.setVisibility(View.VISIBLE);

            }

            if (mProductSharedImage != null) {

                ImageUtils.clearImage(getActivity(), mProductSharedImage);
            }
        }, 1000);

        List<String> qualitySaleImages = getQualityImages(saleDetail.getImages());

        mSaleItemImagesAdapter.replaceData(qualitySaleImages);
        mSaleItemImagesIndicatorAdapter.replaceData(qualitySaleImages);

        if (saleDetail.getImages()
                .size() != 0) {
            mOtherImagesRv.setVisibility(View.VISIBLE);
        }
        //bind UI values here

        //product info
        mProductName.setText(saleDetail.getName());
        //        mProductCategory.setText(productDetail.getBrandName());
        mProductPrice.setText(PriceUtils.getPriceStringValue(saleDetail.getPrice().getValue()));
        mProductPreviousPrice.setText(PriceUtils.getPriceStringValue(saleDetail.getOriginalPrice().getValue()));

        if (saleDetail.getOriginalPrice().getValue()
                .equals(0d)) {
            mProductPreviousPrice.setVisibility(View.GONE);
        } else {
            mProductPreviousPrice.setText(PriceUtils.getRpStringValue(saleDetail.getOriginalPrice().getValue()));
            mProductPreviousPrice.setPaintFlags(
                    mProductPreviousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

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

        //sizes
        if (saleDetail.getSkuVariants().size() != 0) {
            mSizesContainer.setVisibility(View.VISIBLE);
            hasSizes = true;
        }

        if (!saleDetail.getSkuVariants().isEmpty())
            for (SkuVariant skuVariant : saleDetail.getSkuVariants()) {
                String skuId = skuVariant.getSkuId();
                String size = skuVariant.getAttributes().getSize();
                if (!size.isEmpty())
                    mProductSizes.add(new Pair<>(size, skuId));
            }

        if (!(mProductSizes.get(0) == null)) {
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


    @OnClick(R.id.product_details_add_to_basket)
    void addToBasket() {

        /*
        if (Auth.isLoggedIn()){
            Log.d("productdetail", "add to basket");
        }else{
            getRouter().setRoot(RouterTransaction.with(LoginController.newInstance()));

            Log.d("productdetail", "no logged in");
        }*/
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
