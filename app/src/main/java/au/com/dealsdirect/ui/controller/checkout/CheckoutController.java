package au.com.dealsdirect.ui.controller.checkout;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;
import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;

import javax.inject.Inject;


/**
 * dp Created by Admin on 6/6/17.
 */

public class CheckoutController extends BaseController implements CheckoutMvpView {

    private int[] PAGE_COLORS = new int[]{R.color.white, R.color.white};

    private static final String KEY_TEXT = "HomeController.KEY_TEXT";

    @Inject
    CheckoutMvpPresenter<CheckoutMvpView> mPresenter;

    private final RouterPagerAdapter pagerAdapter;

    public CheckoutController() {
        pagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {

                    if (position==0){
                        Controller firstView = CategoriesController.newInstance();
                        router.setRoot(RouterTransaction.with(firstView));

                    }if (position==1){
                        Controller page = new ShopsController();
                        router.setRoot(RouterTransaction.with(page));
                    }

                }
            }

            @Override
            public int getCount() {
                return PAGE_COLORS.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + position;
            }
        };
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_home, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
    }




    @Override protected void setUp(View view) {

    }
}
