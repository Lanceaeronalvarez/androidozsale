package au.com.dealsdirect.ui.controller.brands;

import static au.com.dealsdirect.ui.controller.shops.adapter.HorizontalScrollingBannerAdapter.BannerStyle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.banner.GetTopBrandsResponse;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.brands.adapter.TopBrandsAdapter;
import au.com.dealsdirect.ui.controller.brands.adapter.TopBrandsAdapterHelper;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpView;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.adapter.HorizontalScrollingBannerAdapter;
import au.com.dealsdirect.ui.controller.shops.adapter.ResettableDimensions;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.StringUtils;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

public class TopBrandsController extends BaseController implements TopBrandsMvpView {

    public static final String TAG = "TopBrandsController";

    @Inject
    TopBrandsMvpPresenter<TopBrandsMvpView> mPresenter;

    @BindView(R.id.controller_top_bands_recyclerview)
    RecyclerView topBrandsRecyclerView;
    @BindView(R.id.partial_toolbar_cart)
    ImageButton mShopsControllerCartButton;
    @BindView(R.id.partial_toolbar_logo)
    ImageView mShopsControllerToolbarLogo;
    @BindView(R.id.partial_toolbar_logo_title_view)
    TextView mShopsControllerToolbarTextView;
    @BindView(R.id.partial_toolbar_badge)
    RelativeLayout mBadge;
    @BindView(R.id.partial_toolbar_badge_text)
    TextView mBadgeText;

    private TopBrandsAdapter mTopBrandsAdapter = null;
    private HorizontalScrollingBannerAdapter mTrendingBrandsAdapter = null;
    private ResettableDimensions mResettableDimensionsAdapter = null;
    private GridLayoutManager mLayoutManager;
    private boolean mIsChangeInProgress = false;
    private boolean shouldShowCartButton = false;

    private boolean shouldRefresh = false;

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        assert (mActivity) != null;

        super.onAttach(view);
    }

    public static TopBrandsController newInstance() {
        return new TopBrandsController(new BundleBuilder(new Bundle()).build());
    }

    public TopBrandsController(Bundle args) {
        super(args);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_top_brands, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    private void showProductList() {
        SaleItemsController.Parameters.FromShopSearch parameters = new SaleItemsController.Parameters.FromShopSearch(null, null);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        getRouter().pushController(RouterTransaction.with(controller).tag(getResources().getString(R.string.sale_items_controller_tag)).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity.getProfiler().setStartLogTime(DataCollector.EventParameters.CustomEventType.CV_SALEBANNERS.getValue());
        setUp(view);
    }

    @Override
    public void onDetach(View view) {
        hideLoading();
        super.onDetach(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        topBrandsRecyclerView.clearOnScrollListeners();
        topBrandsRecyclerView.setAdapter(null);
        super.onDestroyView(view);
    }

    @Override
    public void onViewWillAppear(Controller previousController) {
        super.onViewWillAppear(previousController);
        mIsChangeInProgress = true;
        if (mPresenter != null) {
            mPresenter.cancelRequest();
        }
    }

    @Override
    public void onViewWillDisappear(Controller nextController) {
        super.onViewWillDisappear(nextController);
        mIsChangeInProgress = true;
        mPresenter.cancelRequest();
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);

        mIsChangeInProgress = false;
    }

    @Override
    public void onViewDidDisappear(Controller nextController) {
        super.onViewDidDisappear(nextController);

        mIsChangeInProgress = false;
    }

    @Override
    protected void setUp(View view) {

        assert (mActivity) != null;

        hideKeyboard();

        setupTopBrandsView();

        mShopsControllerToolbarTextView.setVisibility(View.VISIBLE);
        mShopsControllerToolbarTextView.setText("Brands");
        mShopsControllerToolbarLogo.setVisibility(View.GONE);
        mShopsControllerCartButton.setImageDrawable(mActivity.getDrawable(R.drawable.ic_new_checkout));
        mShopsControllerCartButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        shouldShowCartButton = true;

        topBrandsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        setRetainViewMode(RetainViewMode.RETAIN_DETACH);

        mPresenter.loadTopBrands();
        loadTrendingBrands();
    }

    private void setupTopBrandsView() {
        int orientation = ScreenUtils.getOrientation(mActivity);
        if (mResettableDimensionsAdapter == null) {
            mTopBrandsAdapter = new TopBrandsAdapter(mActivity, new ArrayList<>(), orientation, mPresenter.isTablet(), new TopBrandsAdapterHelper() {
                @Override
                public int getColumnCount() {
                    return mPresenter.getBannerColumnCount();
                }

                @Override
                public void onBrandClick(GetTopBrandsResponse brand) {

                }

                @Override
                public void onInfoClick(String title, String description) {

                }
            });
            mTopBrandsAdapter.setTrendingBrandsAdapter(mTrendingBrandsAdapter);
            topBrandsRecyclerView.setAdapter(mTopBrandsAdapter);
            clearRecyclerViewItemDecorations();
            topBrandsRecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(mTopBrandsAdapter.getStickyRecyclerHeadersAdapter()));
            mResettableDimensionsAdapter = mTopBrandsAdapter;
        } else {
            mResettableDimensionsAdapter.setupDimensions(orientation);
        }

        resetLayoutManager();
    }

    private void resetLayoutManager() {
        mLayoutManager = new GridLayoutManager(mActivity, mResettableDimensionsAdapter.getNumberOfColumns(), RecyclerView.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };

        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (topBrandsRecyclerView.getAdapter() instanceof ResettableDimensions) {
                    return ((ResettableDimensions) topBrandsRecyclerView.getAdapter()).getNumberOfColumns();
                } else {
                    return 1;
                }
            }
        });

        topBrandsRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onTabSwitch(boolean intoThisView) {
        super.onTabSwitch(intoThisView);
        if (intoThisView) {
            loadTrendingBrands();
        }
    }

    @Override
    public boolean isChangeInProgress() {
        return mIsChangeInProgress;
    }

    @OnClick(R.id.partial_toolbar_cart)
    void onClickCart() {
        if (!shouldShowCartButton) {
            getRouter().handleBack();
            return;
        }
        Controller controller = mPresenter.isTablet() ? ControllerFactory.getInstance(GateKeeper.Destination.CHECKOUT_HOST) : ControllerFactory.getInstance(GateKeeper.Destination.CHECKOUT);

        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(getRouter(), new AuthHandler() {
                @Override
                public void success() {
                    getRouter().popCurrentController();
                    getRouter().pushController(RouterTransaction.with(controller).tag(controller.getClass().getName()).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
                }

                @Override
                public void error() {

                }
            });
        } else if (mActivity.isAuthorized()) {
            getRouter().pushController(RouterTransaction.with(controller).tag(controller.getClass().getName()).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    @Override
    public boolean handleBack() {
        mPresenter.cancelRequest();
        return false;
    }

    private final Runnable onClickLogoRunnable = () -> {
        topBrandsRecyclerView.stopScroll();
        topBrandsRecyclerView.scrollToPosition(0);
    };

    @OnClick(R.id.partial_toolbar_logo)
    void onClickLogo() {
        topBrandsRecyclerView.smoothScrollToPosition(0);
        if (topBrandsRecyclerView.getHandler() != null) {
            topBrandsRecyclerView.getHandler().removeCallbacks(onClickLogoRunnable);
            topBrandsRecyclerView.getHandler().postDelayed(onClickLogoRunnable, 500);
        }
    }

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    @OnClick(R.id.partial_toolbar_search_button)
    void onSearchClick() {
        showProductList();
    }


    @Override
    public void refreshContents() {
        super.refreshContents();
        if (!isViewAttached()) {
            return;
        }
        if (!shouldRefresh) {
            return;
        }
        topBrandsRecyclerView.setVisibility(View.GONE);

        mPresenter.loadTopBrands();
        loadTrendingBrands();

        resetBannerLayout();
    }

    @Override
    public void showTopBrands(List<GetTopBrandsResponse> topBrands) {
        mTopBrandsAdapter = new TopBrandsAdapter(mActivity, topBrands, ScreenUtils.getOrientation(mActivity), mPresenter.isTablet(), new TopBrandsAdapterHelper() {
            @Override
            public int getColumnCount() {
                return mPresenter.getBannerColumnCount();
            }

            @Override
            public void onBrandClick(GetTopBrandsResponse brand) {
                SaleItemsController.Parameters.FromBrandClick parameters = new SaleItemsController.Parameters.FromBrandClick(brand.getName());

                SaleItemsController controller = SaleItemsController.newInstance(parameters);

                getRouter().pushController(RouterTransaction.with(controller).tag(getResources().getString(R.string.sale_items_controller_tag)).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
            }

            @Override
            public void onInfoClick(String title, String description) {
                mActivity.showInfoDialog(title, description);
            }
        });
        mTopBrandsAdapter.setTrendingBrandsAdapter(mTrendingBrandsAdapter);
        mResettableDimensionsAdapter = mTopBrandsAdapter;
        topBrandsRecyclerView.setAdapter(mTopBrandsAdapter);
        clearRecyclerViewItemDecorations();
        topBrandsRecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(mTopBrandsAdapter.getStickyRecyclerHeadersAdapter()));
        topBrandsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(String message) {
        super.onError(message);

        if (mTopBrandsAdapter != null) {
            mTopBrandsAdapter.notifyDataSetChanged();
        }
    }

    private void resetBannerLayout() {
        if (mTopBrandsAdapter != null && topBrandsRecyclerView != null && mLayoutManager != null) {
            int currentScrollPosition = Math.max(0, mLayoutManager.findFirstVisibleItemPosition());
            setupTopBrandsView();
            mLayoutManager.scrollToPosition(currentScrollPosition);
        }
    }

    public void updateBasketItemsQuantity(int quantity) {
        if (quantity == 0) {
            mBadge.setVisibility(View.GONE);
        } else {
            if (shouldShowCartButton) {
                mBadge.setVisibility(View.VISIBLE);
                mBadgeText.setText(Integer.toString(quantity));
            }
        }
    }

    private void clearRecyclerViewItemDecorations() {
        while (topBrandsRecyclerView.getItemDecorationCount() > 0) {
            topBrandsRecyclerView.removeItemDecorationAt(0);
        }
    }

    public void loadTrendingBrands() {
        final String categoryId = null;
        GetBannerRequest request = new GetBannerRequest();
        request.setOffset(null);
        request.setLimit("50");
        request.setBannergroups("7,8,9");
        request.setCategory(categoryId);

        mPresenter.loadTrendingBrands(request);
    }

    @Override
    public void showTrendingBrands(GetBannerResponse response) {
        if (mTopBrandsAdapter == null) {
            setupTopBrandsView();
        }
        if (mTopBrandsAdapter == null) {
            return;
        }

        HorizontalScrollingBannerAdapter adapter = null;
        String title = null;
        if (response != null) {
            List<GetBannerResponse.Banner> categoryBanners = new ArrayList<>();
            List<GetBannerResponse.Group> groups = response.getGroups();
            if (groups != null) {
                for (GetBannerResponse.Group group : groups) {
                    if (group.getType().equals("brand")) {
                        List<GetBannerResponse.Banner> banners = group.getBanners();
                        if (banners != null) {
                            for (GetBannerResponse.Banner banner : banners) {
                                if (banner.getBannerType().equals("brandBanner")) {
                                    categoryBanners.add(banner);
                                }
                            }
                            title = group.getTitle();
                        }
                    }
                }
            }
            if (!categoryBanners.isEmpty()) {
                adapter = new HorizontalScrollingBannerAdapter(BannerStyle.CIRCULAR);
                adapter.setDataSource(categoryBanners);
                adapter.setTitle(title.toUpperCase());
                adapter.setShowHeader(true);
                adapter.setShouldShowTitle(true);
                adapter.setShouldShowSubtitle(true);
                adapter.setImageResolutionOverride(mActivity.getResources().getInteger(R.integer.trending_brands_resolution_override));
                adapter.setBackgroundColorOverride(mActivity.getResources().getColor(R.color.background_default));
                adapter.setOnBannerTappedListener(this::onTrendingBrandClicked);
            }
        }
        mTopBrandsAdapter.setTrendingBrandsAdapter(adapter);
        mTrendingBrandsAdapter = adapter;
    }

    private void onTrendingBrandClicked(GetBannerResponse.Banner banner, int position) {
        if (banner.getLinkOptions() != null && banner.getLinkOptions().getLinkOptionType() == GetBannerResponse.LinkOptions.LinkOptionType.CATEGORY) {
            SaleItemsController.Parameters.FromCategoryBannerClick parameters = new SaleItemsController.Parameters.FromCategoryBannerClick(banner.getLinkOptions());

            SaleItemsController controller = SaleItemsController.newInstance(parameters);

            getRouter().pushController(RouterTransaction.with(controller).tag(mActivity.getString(R.string.sale_items_controller_tag)).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
        } else if (banner.getBannerType() != null && banner.getBannerType().equals("brandBanner")) {
            // support for no linkOptions category banner

            Uri uri = Uri.parse(banner.getLink());

            String saleId = uri.getQueryParameter("saleID");

            if (saleId != null && !saleId.isEmpty()) {
                String saleName = null;
                final String path = uri.getPath();
                if (path != null && !path.isEmpty()) {
                    final ArrayList<String> directories = new ArrayList<>(Arrays.asList(path.split("/")));
                    if (directories.get(0).isEmpty()) {
                        directories.remove(0);
                    }
                    saleName = StringUtils.toTitleCase(
                            StringUtils.fixApostropheS(
                                    directories.get(2)
                                            .replace('-', ' ')
                                            .replace(" or ", " | ")
                                            .replace(" and ", " & ")));
                }
                onBannerClicked(saleId, saleName, banner.getId(), position, banner.getEndDate(), banner.getIsAvailable(), banner.getLinkOptions());
            } else {
                String id = uri.getLastPathSegment();

                onBannerClicked(id);
            }
        } else if (banner.getLink() != null && !banner.getLink().isEmpty()) {
            String link = banner.getLink();
            Pattern pattern = Pattern.compile("(?<=/s/)([^?\\n\\r])+");
            Matcher matcher = pattern.matcher(link);
            String title = " ";
            if (banner.getDescription() != null && !banner.getDescription().isEmpty()) {
                title = banner.getDescription();
            }

            if (matcher.find()) {
                String id = matcher.group();
                onBannerClicked(id, title, banner.getId(), position, banner.getEndDate(), banner.getIsAvailable(), banner.getLinkOptions());

            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(banner.getLink()));
                mActivity.startActivity(browserIntent);
            }
        } else {

            onBannerClicked(banner.getDestinationId(), banner.getDescription(), banner.getId(), position, banner.getEndDate(), banner.getIsAvailable(), banner.getLinkOptions());

        }
    }

    private void onBannerClicked(String saleId, String bannerTitle, String bannerId, int position, String endDate, boolean isAvailable, GetBannerResponse.LinkOptions linkOptions) {
        SaleItemsController.Parameters.FromBannerClick parameters = new SaleItemsController.Parameters.FromBannerClick(bannerTitle, saleId, bannerId, null, endDate, linkOptions, position);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        List<String> names = new ArrayList<>();
        names.add(bannerId + position);

        if (isAvailable) {
            getRouter().pushController(RouterTransaction.with(controller).tag(mActivity.getString(R.string.sale_items_controller_tag)).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
        } else {
            DialogUtils.showYesDialog(mActivity, "", "Sale is currently closed", "OK", (dialogInterface, i) -> dialogInterface.dismiss());
        }
    }

    private void onBannerClicked(String categoryId) {
        final CategoriesMvpView categoriesMvpView = mActivity.getCategoriesController();
        if (categoriesMvpView == null) {
            return;
        }
        categoriesMvpView.showSaleItems(categoryId);
        mActivity.getMainController().showCategoryController();
    }
}
