package au.com.dealsdirect.ui.controller.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.Price;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

/**
 * dp Created by Admin on 6/6/17.
 */

public class MainController extends BaseController implements MainMvpView {

    public static final String TAG = "MainController";

    private String mChosenSubCategoryItemKey = "";

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    HashMap<Integer,Router> mRouterList;
    Router mSaleItemsRouter;
    Router mAccountsRouter;
    Router mCheckoutRouter;

    @BindView(R.id.home_viewpager)
    MainCustomViewPager mHomeViewPager;

    @Inject
    MainActivity mActivity;

    Controller mSaleItemsController;
    Controller mCheckoutController;
    Controller mAccountsController;

    private int mCurrentVisibleIndex = -1;

    public static MainController newInstance() {

        return new MainController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public MainController(Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_main, container, false);
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

        mSaleItemsController = ControllerFactory.getInstance(GateKeeper.Destination.SALEITEMS);
        mCheckoutController = ControllerFactory.getInstance(GateKeeper.Destination.CHECKOUT);
        mAccountsController = ControllerFactory.getInstance(GateKeeper.Destination.ACCOUNT);

        setupViewPager();
        setPageChangeListener();

    }

    private void setupViewPager() {

        mRouterList = new HashMap<>();

        RouterPagerAdapter mViewPagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {
                    switch (position) {
                        case 0:
                            GateKeeper.setRoot(router, GateKeeper.Destination.ACCOUNT, RouterTransaction.with(mAccountsController));
                            break;
                        case 1:
                            GateKeeper.setRoot(router, GateKeeper.Destination.SALEITEMS, RouterTransaction.with(mSaleItemsController));
                            break;
                        case 2:
                            GateKeeper.setRoot(router, GateKeeper.Destination.CHECKOUT, RouterTransaction.with(mCheckoutController));
                            break;
                        default:
                            router.setRoot(RouterTransaction.with(mSaleItemsController));
                            break;
                    }
                }
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + position;
            }
        };


        mHomeViewPager.setAdapter(mViewPagerAdapter);
        mHomeViewPager.setCurrentItem(1);
        mCurrentVisibleIndex = 1;
        mHomeViewPager.setMyScroller();
        mHomeViewPager.setOffscreenPageLimit(2);

        mActivity.isViewPagerSet(true);

    }

    public void goToCheckout() {
        if (mHomeViewPager != null) {
            mHomeViewPager.setCurrentItem(2);
        }
    }

    public void goToSaleItems() {
        if (mHomeViewPager != null) {
            mHomeViewPager.setCurrentItem(1);
        }
    }

    public void goToAccounts() {
        if (mHomeViewPager != null) {
            mHomeViewPager.setCurrentItem(0);
        }
    }

    public void setViewpagerDraggable(boolean isDraggable) {

        if (mHomeViewPager != null) {
            mHomeViewPager.setSwipeable(isDraggable);
        }
    }

    public MainCustomViewPager getHomeViewPager() {
        return mHomeViewPager;
    }


    @Override
    public void hideBottomNav() {

    }

    @Override
    public void showBottomNav() {

    }

    @Override
    public void setChosenCategoryItemKey(String key) {

    }

    @Override
    public void setSelectedSubCategoryItem(View view) {

    }

    @Override
    public View getSelectedSubCategoryItem() {
        return null;
    }

    @Override
    public String getChosenCategoryItemKey() {
        return null;
    }

    @Override
    public HomeController getHomeController() {
        return null;
    }

    public Router getCurrentRouter(){
        return mRouterList.get(mCurrentVisibleIndex);
    }

    private void setPageChangeListener() {
        mHomeViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                hideKeyboard();
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentVisibleIndex = position;
                if (position==2){
                    if(mActivity.getCheckoutRouter() != null) {
                        Controller controller = GateKeeper.getCurrentControllerOnRouter(mActivity.getCheckoutRouter());
                        if (controller instanceof CheckoutController) {
                            ((CheckoutController) controller).loadCart();
                        }
                    }
                }
                hideKeyboard();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public void deepLinkProductDetails(String seoIdentifier, String skuId) {

        ((SaleItemsController) mSaleItemsController).deepLinkSaleItemDetails(seoIdentifier, skuId);
    }

    public void deepLinkProductDetails() {
        GetSaleItemDetailsResponse getSaleItemDetailsResponse = new GetSaleItemDetailsResponse();
        getSaleItemDetailsResponse.setSkuId("ZDQ5Nzk5M2UtMjU2Yy00ODMzLWJkZmEtNDE2OGNjMGIyOWRiX2FlMGRiNDQ2LWY4YWItNDFlMi1iZTY5LTIwYjNmYjQwZjg1Nw==");
        List<String> linkedList = new LinkedList<>();
        linkedList.add("https://c1.mysalec.com/brands/320f61c3-ad87-4b66-abc2-8f82f31c4b59/b4ceed87-debf-4502-9874-9eb558de3061/b82162ef-e0c5-4a34-80b3-c24b54dcdcd1_50x50.JPG");

        getSaleItemDetailsResponse.setImages(linkedList);
        getSaleItemDetailsResponse.setSeoIdentifier("qDzCZLx7gE_aKbguHqNfeg");
        Price price = new Price();
        OriginalPrice originalPrice = new OriginalPrice();
        price.setValue(Double.valueOf(1));
        originalPrice.setValue(Double.valueOf(2));
        getSaleItemDetailsResponse.setPrice(price);
        getSaleItemDetailsResponse.setOriginalPrice(originalPrice);

        ((SaleItemsController) mSaleItemsController).deepLinkSaleItemDetails("qDzCZLx7gE_aKbguHqNfeg", "ZDQ5Nzk5M2UtMjU2Yy00ODMzLWJkZmEtNDE2OGNjMGIyOWRiX2FlMGRiNDQ2LWY4YWItNDFlMi1iZTY5LTIwYjNmYjQwZjg1Nw==");
    }

    public void loadSaleItems(String categoryKey) {
        Log.d("deeplinking", "loadsaleitems");
        ((SaleItemsController) mSaleItemsController).updateSaleItems(categoryKey);
    }
}
