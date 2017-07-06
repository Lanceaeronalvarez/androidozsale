package au.com.dealsdirect.ui.controller.shops;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.adapter.BannersAdapter;
import au.com.dealsdirect.ui.controller.shops.listener.BannerClickListener;
import au.com.dealsdirect.ui.custom.transitions.HorizontalNavTransitionChangeHandler;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.KeyboardUtils;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * dp Created by Admin on 6/6/17.
 */

public class ShopsController extends BaseController implements ShopsMvpView, BannerClickListener {

    private static final String KEY_TEXT = "ShopController.KEY_TEXT";
    private static final String KEY_CATEGORY_ID = "ShopController.KEY_CATEGORY_ID";
    private static final String KEY_CATEGORY_NAME = "ShopController.KEY_CATEGORY_NAME";
    private static final String KEY_CATEGORY_MAP = "ShopController.KEY_CATEGORY_KEY";

    @Inject
    ShopsMvpPresenter<ShopsMvpView> mPresenter;

    @BindView(R.id.controller_shop_banner_recycler)
    RecyclerView shopsControllerBannerRecyclerView;

    @BindView(R.id.partial_toolbar_search_icon)
    ImageView shopsControllerSearchView;

    @BindView(R.id.partial_toolbar_hamburger)
    ImageView mShopsControllerHamburgerView;

    @BindView(R.id.controller_shop_toolbar)
    View mShopsControllerToolbar;

    private BannersAdapter mBannersAdapter;
    private BannerClickListener mBannerClickListener;

    private String mCategoryID;
    private String mCategoryName;
    private String mCategoryKey;

    private List<GetBannerResponse> sales = new LinkedList<>();

    public static ShopsController newInstance(GetCategoryTreeResponse getCategoryTreeResponse) {

        return new ShopsController(
                new BundleBuilder(new Bundle())
                        .putString(KEY_CATEGORY_ID, getCategoryTreeResponse.getId())
                        .putString(KEY_CATEGORY_NAME, getCategoryTreeResponse.getName())
                        .putString(KEY_CATEGORY_MAP, getCategoryTreeResponse.getKey())
                        .build());
    }

    public ShopsController() {

    }

    public ShopsController(Bundle args) {
        super(args);
        mCategoryID = getArgs().getString(KEY_CATEGORY_ID);
        mCategoryName = getArgs().getString(KEY_CATEGORY_NAME);
        mCategoryKey = getArgs().getString(KEY_CATEGORY_MAP);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_shop, container, false);
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
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        if (sales.size() == 0) {
            mPresenter.loadShopsBanner(mCategoryName,mCategoryID);
        } else {
            mBannersAdapter = new BannersAdapter(getActivity(), sales, mBannerClickListener);

            shopsControllerBannerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            shopsControllerBannerRecyclerView.setAdapter(mBannersAdapter);
        }

    }

    @Override
    public void onError(String message) {
        super.onError(message);

//        mPresenter.loadShopsBanner(mCategoryName,mCategoryID);
    }


    @Override
    public void onBannerClicked(
            String saleId,
            String bannerTitle,
            String bannerId,
            int position,
            String imageUrl) {

        List<String> names = new ArrayList<>();
        names.add(bannerId + position);

        getRouter().pushController(RouterTransaction.with(
                SaleItemsController.newInstance(saleId, bannerTitle, bannerId, position, imageUrl, null))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @OnClick(R.id.partial_toolbar_hamburger)
    void onClickHamburger() {

        getRouter().pushController(RouterTransaction.with(CategoriesController.newInstance())
                .tag("category")
                .pushChangeHandler(new HorizontalNavTransitionChangeHandler(100))
                .popChangeHandler(new HorizontalNavTransitionChangeHandler(100)));

//        ((MainActivity) getActivity()).hideBottomNav();
    }


    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.partial_toolbar_search_icon)
    void onSearchClick(){

        shopsControllerSearchView.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));

        mShopsControllerHamburgerView.animate().rotation(-90).setDuration(200).start();

        RelativeLayout item = (RelativeLayout)getView().findViewById(R.id.controller_shop_toolbar_container);
        View child = getActivity().getLayoutInflater().inflate(R.layout.partial_toolbar_search, null);
        item.addView(child);

        child.setBackgroundColor(getResources().getColor(R.color.toolbar_active_skin));
        ImageView rightOption = (ImageView) child.findViewById(R.id.partial_toolbar_search_right_option);
        rightOption.setBackgroundColor(getResources().getColor(R.color.toolbar_active_skin));

        ImageView leftOption = (ImageView) child.findViewById(R.id.partial_toolbar_search_left_option);
        EditText searchField = (EditText) child.findViewById(R.id.partial_toolbar_search_field);
        searchField.setActivated(true);
        searchField.setFocusable(true);

        if(searchField.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }


        rightOption.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
        rightOption.animate().rotation(360).setDuration(200).start();

        searchField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchField.getText().toString());
                return true;
            }
            return false;
        });


        rightOption.setOnClickListener(view -> {
            child.startAnimation(outToRightAnimation());
            item.removeView(child);
            shopsControllerSearchView.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_search));
            rightOption.animate().rotation(-360).setDuration(200).start();
            mShopsControllerHamburgerView.animate().rotation(0).setDuration(200).start();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getActivity().dismissKeyboardShortcutsHelper();
            }

        });

        child.startAnimation(inFromRightAnimation());
    }

    @Override
    public void showShopBanners(List<GetBannerResponse> getBannerResponses) {

        sales = getBannerResponses;

        mBannerClickListener = this;

        mBannersAdapter = new BannersAdapter(getActivity(), sales, mBannerClickListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setAutoMeasureEnabled(false);
        shopsControllerBannerRecyclerView.setLayoutManager(linearLayoutManager);
        shopsControllerBannerRecyclerView.setAdapter(mBannersAdapter);
        mBannersAdapter.notifyDataSetChanged();

    }


    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(200);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }


    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(200);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }

    private void performSearch(String searchQuery){

        Bundle saleItemBundle = new BundleBuilder(new Bundle())
                .putString("SaleItemsController.KEY_TITLE", searchQuery)
                .putString("SaleItemsController.SEARCH_KEY", searchQuery)
                .build();

        if (!searchQuery.isEmpty())

            KeyboardUtils.hideSoftInput(getActivity());
            getRouter().pushController(RouterTransaction.with(
                    SaleItemsController.newInstance(saleItemBundle))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));


    }

}
